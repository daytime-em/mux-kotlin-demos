package com.mux.muxdatademos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.File

object Util {

    /**
     * Gets the URL for a video to play in the example UIs
     */
    fun exampleVideoInfo(): VideoInfo {
        return VideoInfo(url = "https://mux.slack.com/files/U02H59VMPM2/F02HPLKKWKY/vid_20211014_235115.mp4")
    }

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
}