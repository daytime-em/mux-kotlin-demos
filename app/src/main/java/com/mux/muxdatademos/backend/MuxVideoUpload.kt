package com.mux.muxdatademos.backend

/**
 * Represents an active (or finished) video upload, connected with a recently-created asset
 * For more information, see:
 *  https://docs.mux.com/api-reference/video#operation/create-direct-upload
 */
data class MuxVideoUpload(
    /**
     * Resume-able PUT URL for the video file being uploaded
     */
    val url: String,
    /**
     * Timeout (in seconds) before this upload link times out
     */
    val timeout: Long?,
    /**
     * Status of this video upload. Possible values are:
     *  "waiting",
     *  "asset_created",
     *  "errored",
     *  "cancelled",
     *  "timed_out
     */
    val status: String,
    /**
     * The settings for this asset, most of which can be updated by calling the POST/uploads API
     * again
     */
    val newAssetSettings: List<NewAssetSettings>,
    /**
     * The ID of the created asset on Mux Video. Will be non-null if the status is "asset_created"
     */
    val assetId: String?,
    /**
     * Arbitrary object representing an error, if one occurred while trying to create the asset
     */
    val error: MuxVideoUploadError,
    /**
     * The origin for a videos CORS headers. Only relevant if sent from a browser
     */
    val corsOrigin: String,
    /**
     * True if the upload is a Test upload. Test uploads are free to store, up to 10 seconds long,
     * and disappear after 24 hours
     */
    val test: Boolean,
    )

data class MuxVideoUploadError(
    val type: String,
    val message: String
)
