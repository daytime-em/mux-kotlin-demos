package com.mux.muxdatademos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.mux.muxdatademos.backend.MuxVideoBackend
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File

object Util {

    /**
     * A Retrofit interface that can be used for interacting with the Mux Video backend
     */
    val muxVideoBackend: MuxVideoBackend by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mux.com/video/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(muxHttpClient)
            .build().create(MuxVideoBackend::class.java)
    }

    /**
     * An OkHttpClient that can be used to upload videos directly. This OkHttpClient will also be
     * used to back the Retrofit interfaces
     */
    val muxHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor {
                Log.v("MuxDataDemos", it)
            }.apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
    }

    /**
     * Basic-Auth Credential for authorizing the Mux Video API
     */
    val exampleVideoCredential =
        Credentials.basic(BuildConfig.MUX_VIDEO_TOKEN_ID, BuildConfig.MUX_VIDEO_TOKEN_SECRET)

    /**
     * Create a URL for HLS Playback based on the given playback ID
     *
     * see MuxVideoBackend for more information about assets and playback IDs
     */
    fun createMuxHlsUrl(playbackId: String) = "https://stream.mux.com/$playbackId.m3u8"

    /**
     * Saves the URL of last-uploaded video for playback
     */
    fun saveLastUploadedVideo(context: Context, url: String) =
        context.getSharedPreferences("mux-prefs.xml", 0).edit()
            .putString("last_video_url", url).apply()

    /**
     * Saves the last-recorded video's file path to shared prefs
     */
    fun saveLastRecordedVideo(context: Context, videoFile: File) {
        context.getSharedPreferences("mux-prefs.xml", 0).edit()
            .putString("last_video_path", videoFile.absolutePath).apply()
    }

    /**
     * Loads the last-recorded video's file path from shared prefs
     */
    fun loadLastRecordedVideoPath(context: Context): File? =
        context.getSharedPreferences("mux-prefs.xml", 0)
            .getString("last_video_path", null)
            ?.let { File(it) }

    /**
     * Gets the directory where recorded/saved video files are stored. The file is on the device's
     * external storage and should appear in the user's gallery
     */
    // TODO: Explain that it might be hard to grant URI permission for recording to internal storage
    //  Or figure out a way and show that, or just suggest baking their own recording activity for
    //  that functionality (For The Record: Getting the default Camera package probably isn't hard,
    //  just mention that you have to always be matching the intent in .TakeVideo)
    fun getVideosDir(context: Context): File {
        val videoFile =
            File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "MuxDemos")
        videoFile.mkdirs()
        return videoFile
    }

    /**
     * Checks for the permissions required to record and access video files using the device's default
     * camera app
     */
    fun haveRecordVideoPermissions(context: Context): Boolean {
        val hasCamera = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val hasStorage = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        Log.d(javaClass.simpleName, "have videoRecordPermissions: camera $hasCamera")
        Log.d(javaClass.simpleName, "have videoRecordPermissions: storage $hasStorage")

        return hasCamera && hasStorage
    }

    private val gson by lazy {
        GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }
}
