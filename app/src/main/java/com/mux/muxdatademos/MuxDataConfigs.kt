package com.mux.muxdatademos

import com.mux.stats.sdk.core.model.*
import java.util.*

/**
 * Sample Mux API Configuration objects that are used in the examples.
 */
object MuxDataConfigs {
    /**
     * A CustomerPlayerData configured for Em's Production Environment
     */
    fun examplePlayerData() =
        CustomerPlayerData().apply { environmentKey = BuildConfig.MUX_ENV_KEY }

    /**
     * A CustomerVideoData that sets a video title but otherwise uses default values
     */
    fun exampleVideoData() = CustomerVideoData().apply { videoTitle = "Mux Data Demo" }

    /**
     * A CustomerViewData, representing a new view with a random UUID.
     */
    fun exampleViewData() =
        CustomerViewData().apply { viewSessionId = UUID.randomUUID().toString() }

    /**
     * An example CustomData, with all 5 fields filled
     */
    fun exampleCustomData() = CustomData().apply {
        customData1 = "Hello"
        customData2 = "World"
        customData3 = "From"
        customData4 = "Mux"
        customData5 = "!"
    }

    // TODO move somewhere else
    fun exampleVideoInfo(): VideoInfo {
        return VideoInfo(url = "https://mux.slack.com/files/U02H59VMPM2/F02HPLKKWKY/vid_20211014_235115.mp4")
    }
}
