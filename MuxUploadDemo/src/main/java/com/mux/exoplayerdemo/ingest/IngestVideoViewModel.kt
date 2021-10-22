package com.mux.exoplayerdemo.ingest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mux.exoplayerdemo.Util
import com.mux.exoplayerdemo.asCountingFileBody
import com.mux.exoplayerdemo.backend.MuxPlaybackPolicy
import com.mux.exoplayerdemo.backend.VideoUploadPost
import kotlinx.coroutines.*
import okhttp3.Request
import java.io.File
import java.util.concurrent.atomic.AtomicReference

/**
 * Uploads a video to Mux Video, creating an asset.
 *
 * For brevity all logic for ingesting and publishing videos is contained here. We recommend using a
 * Service in your application if your users want to leave the screen while the upload completes.
 */
class IngestVideoViewModel(stateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        const val POLL_DELAY_MS = 500L
    }

    val state: LiveData<State> get() = _state
    private val _state = stateHandle.getLiveData<State>("state")

    /**
     * Progress is provided as a Pair of values: (min, max). No guarantee about the scale of either
     * pair value is made
     */
    val uploadProgress: LiveData<Pair<Int, Int>> get() = _uploadProgress
    private val _uploadProgress = stateHandle.getLiveData<Pair<Int, Int>>("upload_bytes")

    /**
     * Playback ID of a completed upload. This ID is used to create the URL to the video for playback
     */
    val playbackId: LiveData<String> get() = _playbackId
    private val _playbackId = stateHandle.getLiveData<String>("playback_id")
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
        _state.postValue(State.CREATING_UPLOAD)
        val destinationData = createUpload()

        // Upload to the provided URL
        _state.postValue(State.UPLOADING)
        uploadFile(destinationData.data.url, videoFile)

        // Await processing in order to obtain an asset ID
        _state.postValue(State.AWAITING_PROCESSING)
        val assetId = awaitAssetId(destinationData.data.id)
        Log.d(javaClass.simpleName, "Got asset ID $assetId")

        // Create a Playback ID for the new asset, allowing it to be played
        _state.postValue(State.CREATING_PLAYBACK_ID)
        val playbackId = createPlaybackId(assetId)

        // Done! Reset the state
        _state.postValue(State.DONE)
        _playbackId.postValue(playbackId)
        uploadJob.set(null)
    }

    private suspend fun createUpload() =
        Util.muxVideoBackend.postUploads(
            postBody = VideoUploadPost()
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

    private suspend fun awaitAssetId(uploadId: String): String {
        // For brevity this example simply polls the GET/uploads API at regular intervals
        while (true) {
            delay(POLL_DELAY_MS)
            val uploadData = Util.muxVideoBackend.getUpload(uploadId = uploadId)
            Log.d(javaClass.simpleName, "Polled for upload data $uploadData")
            when(uploadData.data.status) {
                "errored" -> {
                    Log.e(javaClass.simpleName, "Processing error for uploadId $uploadId")
                    throw Exception("Processing error for uploadId $uploadId")
                }
                "cancelled" -> {
                    Log.e(javaClass.simpleName, "uploadId $uploadId Canceled")
                    throw Exception("uploadId $uploadId Canceled")
                }
                "timed_out" -> {
                    Log.e(javaClass.simpleName, "uploadId $uploadId Timed Out")
                    throw Exception("uploadId $uploadId Timed Out")
                }
                "asset_created" -> {
                    val assetId = uploadData.data.assetId
                    Log.e(javaClass.simpleName, "Asset $assetId created for uploadId $uploadId")
                    // The asset ID should be present at this point, making this !! safe
                    return assetId!!
                }
                else -> Log.d(javaClass.simpleName, "Still awaiting asset ID for $uploadId")
            }
        }
    }

    private suspend fun createPlaybackId(assetId: String): String {
        val playbackIdData = Util.muxVideoBackend.createPlaybackId(
            assetId = assetId,
            playbackPolicy = MuxPlaybackPolicy(
                policy = "public"
            )
        )

        Log.d(javaClass.simpleName, "Playback ID created: $playbackIdData")
        return playbackIdData.data.id
    }

    enum class State {
        IDLE,
        CREATING_UPLOAD,
        UPLOADING,
        AWAITING_PROCESSING,
        CREATING_PLAYBACK_ID,
        DONE,
        ERROR
    }
}
