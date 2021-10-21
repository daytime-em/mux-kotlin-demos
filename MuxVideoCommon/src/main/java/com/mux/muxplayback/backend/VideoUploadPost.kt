package com.mux.muxplayback.backend

/**
 * Represents the POST body for creating a new video asset.
 *
 * The default values for this object are chosen for the purposes of this example and may not be
 * optimal for your use case.
 *
 * Further documentation can be found here:
 *  https://docs.mux.com/api-reference/video#operation/create-direct-upload
 */
data class VideoUploadPost(
    /**
     * List of new assets to create.
     */
    val assetSettings: List<NewAssetSettings> = listOf(NewAssetSettings()),
    /**
     * Origin for the CORS header in a browser playback situation. "*" is a reasonable default
     * TODO: Is it a good default?
     */
    val corsOrigin: String = "*"
)

/**
 * Settings for the newly-created asset.
 */
data class NewAssetSettings(
    /**
     * Possible values:
     *  "public": Public assets can be streamed without authentication
     *  "signed": Signed assets require authentication by token to be streamed.
     *      (see https://docs.mux.com/api-reference/video)
     */
    val playbackPolicy: List<String> = listOf("public", "signed"),
    /**
     * Arbitrary data that can be passed along with the asset.
     * NOTE: The backend limits the length of this string to 255 characters.
     */
    val passthrough: String = "Extra video data! This can be anything you want!",
    /**
     * Toggles support for simple mp4 playback for this asset. HLS is preferable for streaming
     * experience, but mp4 is useful for scenarios such as offline playback
     *
     * Possible Values:
     *  "none": mp4 support disabled
     *  "standard": mp4 support enabled
     */
    val mp4Support: String = "standard",
    /**
     * Toggles audio loudness normalization for this asset as part of the transcode process
     */
    val normalizeAudio: Boolean = true,
    /**
     * Marks the asset as a test asset when the value is set to true. A Test asset can help evaluate
     * the Mux Video APIs without incurring any cost. There is no limit on number of test assets
     * created. Test asset are watermarked with the Mux logo, limited to 10 seconds, deleted after 24 hrs.
     */
    val test: Boolean = false,
)
