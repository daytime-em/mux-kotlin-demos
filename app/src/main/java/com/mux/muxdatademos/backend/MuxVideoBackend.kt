package com.mux.muxdatademos.backend

import com.mux.muxdatademos.Util
import retrofit2.http.*

/**
 * Retrofit interface representing the Mux Video backend
 */
interface MuxVideoBackend {
    /**
     * Creates a new Upload on the Mux backend. This does not upload the video itself. It just creates
     * a new asset and provides an authenticated URL to a resume-able upload.
     *
     * When the upload completes, the Mux backend will process the video and create an Asset
     */
    @POST("v1/uploads")
    @Headers("Content-Type: application/json")
    suspend fun postUploads(
        @Header("Authorization") basicAuth: String = Util.exampleVideoCredential,
        @Body postBody: VideoUploadPost
    ): MuxVideoUploadResponse

    /**
     * Gets the status of an Upload previously created. When processing is complete, the Upload will
     * have an Asset ID that can be used to modify the created Asset. (For example, to add a playback
     * ID and allow playback)
     */
    @GET("v1/uploads/{uploadId}")
    suspend fun getUpload(
        @Header("Authorization") basicAuth: String = Util.exampleVideoCredential,
        @Path("uploadId") uploadId: String
    ): MuxVideoUploadResponse

    @POST("v1/assets/{assetId}/playback-ids ")
    suspend fun createPlaybackId(
        @Header("Authorization") basicAuth: String = Util.exampleVideoCredential,
        @Path("assetId") assetId: String,
        @Body playbackPolicy: MuxPlaybackPolicy
    ): MuxPlaybackIdData
}
