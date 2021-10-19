package com.mux.muxdatademos.backend

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Retrofit interface representing the Mux Video backend
 */
interface MuxVideoBackend {
    /**
     * Creates a new Asset on the Mux backend. This does not upload the video itself. It just creates
     * a new asset and provides an authenticated URL to a resume-able upload.
     */
    @POST("uploads")
    @Headers("Content-Type", "application/json")
    suspend fun postUploads(
        @Header("Authorization") basicAuth: String,
        @Body postBody: Any
    ): VideoUploadPost
}
