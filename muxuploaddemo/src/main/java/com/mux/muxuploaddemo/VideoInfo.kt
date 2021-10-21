package com.mux.muxuploaddemo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Information about a video to be played by the app
 */
@Parcelize
data class VideoInfo(
    /**
     * URL of the video to be played
     */
    val id: String
) : Parcelable
