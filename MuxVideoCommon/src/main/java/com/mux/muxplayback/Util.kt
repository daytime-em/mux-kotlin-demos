package com.mux.muxplayback

import android.content.Context
import java.io.File

internal object Util {

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
}
