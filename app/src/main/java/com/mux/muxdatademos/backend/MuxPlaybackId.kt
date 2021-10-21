package com.mux.muxdatademos.backend

/**
 * Represents a Mux Playback ID associated with an Asset
 */
data class MuxPlaybackId(
    val data: MuxPlaybackIdData
)

data class MuxPlaybackIdData(
    /**
     * Playback policy for the asset
     * Possible Values:
     * "pubilc"
     * "signed"
     */
    val policy: String,
    /**
     * The Playback ID that was created
     */
    val id: String,
)
