package com.mux.muxdatademos.ingest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mux.muxdatademos.Util
import com.mux.muxdatademos.asCountingFileBody
import com.mux.muxdatademos.backend.VideoUploadPost
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.atomic.AtomicReference

/**
 * Uploads a video to Mux Video, creating an asset.
 */
class IngestVideoViewModel(stateHandle: SavedStateHandle) : ViewModel() {

    val state: LiveData<State> get() = _state
    private val _state = stateHandle.getLiveData<State>("state")

    /**
     * Progress is provided as a Pair of values: (min, max). No guarantee about the scale of either
     * pair value is made
     */
    val uploadProgress: LiveData<Pair<Int, Int>> get() = _uploadProgress
    private val _uploadProgress = stateHandle.getLiveData<Pair<Int, Int>>("upload_bytes")
    private val _thumbnailUrl = stateHandle.getLiveData<String>("thumbnail_url")

    private val uploadJob = AtomicReference<Job?>()
    private val uploadExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(javaClass.simpleName, "Error thrown while attempting upload", throwable)
        _state.postValue(State.ERROR)
    }

    init {
        _state.value = State.IDLE
    }

    /**
     * Starts uploading the file to Mux Video, assuming an upload job has not already started. If an
     * upload job has already been started, a new job will not be started and LiveData callbacks wil
     * originate from the original job.
     *
     * The uploadBytes LiveData can be used to observe the progress of the upload
     */
    fun startUploadIfNotStarted(videoFile: File) {
        uploadJob.compareAndSet(null, createUploadJob(videoFile))
        uploadJob.get()?.start()
    }

    private fun createUploadJob(videoFile: File): Job =
        viewModelScope.launch(
            start = CoroutineStart.LAZY,
            context = Dispatchers.IO + uploadExceptionHandler
        ) {
            if(_state.value != State.DONE) {
                doUpload(videoFile)
            }
        }

    private suspend fun doUpload(videoFile: File) {
        // Create the Asset to which we'll be uploading our video
        _state.postValue(State.CREATING_ASSET)
        val destinationData = createUpload()

        // Upload to the provided URL
        _state.postValue(State.UPLOADING)
        uploadFile(destinationData.data.url, videoFile)

        // Done! Reset the state
        _state.postValue(State.DONE)
        uploadJob.set(null)
    }

    private suspend fun createUpload() =
        Util.muxVideoBackend.postUploads(
            Util.exampleVideoCredential,
            VideoUploadPost()
        ).also {
            Log.d(javaClass.simpleName, "Created Upload: $it")
        }

    private fun uploadFile(url: String, videoFile: File) {
        Log.d(javaClass.simpleName, "Uploading $videoFile to $url")

        val fileBody = videoFile.asCountingFileBody("application/mp4") { totalWrittenBytes ->
            _uploadProgress.postValue(totalWrittenBytes.toInt() to contentLength().toInt())

        }

        Log.d(javaClass.simpleName, "uploadFile(): Uploading ${fileBody.contentLength()} bytes")
        val request = Request.Builder()
            .url(url)
            .put(fileBody)
            .build()

        Util.muxHttpClient.newCall(request).execute().use { resp ->
            if(!resp.isSuccessful) {
                Log.e(javaClass.simpleName, "Failed to upload with error ${resp.code}:${resp.message}")
                _state.postValue(State.ERROR)
            } else {
                Log.i(javaClass.simpleName, "Successfully uploaded file $videoFile to $url")
                _state.postValue(State.DONE)
            }
        }
    }

    enum class State {
        IDLE,
        CREATING_ASSET,
        UPLOADING,
        DONE,
        ERROR
    }
}
