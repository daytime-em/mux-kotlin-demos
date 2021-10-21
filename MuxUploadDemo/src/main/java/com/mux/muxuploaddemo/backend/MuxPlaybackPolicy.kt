package com.mux.muxuploaddemo.backend

/**
 * Represents a playback policy for a Mux Asset.
 *
 * The default values are suitable for this example
 */
data class MuxPlaybackPolicy(
    /**
     * Playback policy for the asset
     * Possible Values:
     * "pubilc"
     * "signed"
     */
    val policy: String = "public"
)
