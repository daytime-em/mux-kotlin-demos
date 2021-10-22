package com.mux.muxuploaddemo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.io.File

class UploadMainViewModel(stateHandle: SavedStateHandle): ViewModel() {

    private val _recordedVideoFile = stateHandle.getLiveData<File>("recordedVideoFile")
    val recordedVideoFile get() = _recordedVideoFile

    /**
     * Call to give the ViewModel the location of the video that is about it be recorded.
     * Call this before launching the record activity because Contracts.TakeVideo does not return
     * the video file's path
     */
    fun updateRecordedVideoFile(file: File) {
        _recordedVideoFile.value = file
    }
}
