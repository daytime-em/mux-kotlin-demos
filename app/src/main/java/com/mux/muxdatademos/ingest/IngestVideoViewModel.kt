package com.mux.muxdatademos.ingest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.atomic.AtomicReference

/**
 * Uploads a video to Mux Video, creating an asset.
 */
class IngestVideoViewModel(stateHandle: SavedStateHandle) : ViewModel() {

    private val _state = stateHandle.getLiveData<State>("state")
    private val _uploadPercent = stateHandle.getLiveData<Int>("upload_bytes")
    private val _thumbnailUrl = stateHandle.getLiveData<String>("thumbnail_url")

    private val uploadJob = AtomicReference<Job?>()

    init {
        _state.value = State.IDLE
    }

    /**
     * Starts uploading the file to Mux Video, assuming an upload job has not already started. If an
     * upload job has already been started, a new job will not be started and LiveData callbacks wil
     * originate from the original job.
     *
     * The uploadBytes LiveData can be used to observe the progress of the upload
     * The
     */
    fun startUploadIfNotStarted(videoFile: File) {
        uploadJob.compareAndSet(null, createUploadJob(videoFile))
        uploadJob.get()?.start()
    }

    private fun createUploadJob(videoFile: File): Job =
        viewModelScope.launch(start = CoroutineStart.LAZY) {
            doUpload(videoFile)
        }

    private suspend fun doUpload(videoFile: File) {
        _state.postValue(State.UPLOADING)

        // Done! Reset the state
        _state.postValue(State.DONE)
        uploadJob.set(null)
    }

    enum class State {
        IDLE,
        UPLOADING,
        DONE
    }
}