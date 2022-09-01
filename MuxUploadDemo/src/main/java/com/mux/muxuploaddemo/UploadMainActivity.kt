package com.mux.muxuploaddemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.mux.muxplayback.player.ExoPlayerDialog
import com.mux.muxuploaddemo.databinding.ActivityUploadMainBinding
import com.mux.muxuploaddemo.ingest.IngestVideoActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class UploadMainActivity : AppCompatActivity() {

  private val viewModel by viewModels<UploadMainViewModel>()

  // In order to record via the Camera app, we must have the Camera permission
  // In order for files to appear in the user's gallery, External Storage is required
  private val requestRecordPermission =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedPermission ->
      val missedPermission = grantedPermission.values.contains(false)
      if (missedPermission) {
        askForRecordPermission()
      }
    }


  private val ingestVideo =
    registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
      if (success) {
        val videoFile = viewModel.recordedVideoFile.value!!
        // !! safe if value was set before launching the camera app
        Util.saveLastRecordedVideo(this, videoFile)
        lifecycleScope.launch(Dispatchers.IO) {
          // Add to the Device Gallery
          ContentValues().apply {
            put(MediaStore.Video.Media.TITLE, "Mux Upload Demo")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT < 29) {
              put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
            } else {
              // DATA is now deprecated and cannot be written to
              put(MediaStore.Video.Media.RELATIVE_PATH, videoFile.path)
            }
            contentResolver.insert(
              MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
              this
            )
          }
        }
        Intent(this, IngestVideoActivity::class.java).let {
          it.putExtra("input_file", viewModel.recordedVideoFile.value)
          startActivity(it)
        }
      } else {
        Toast.makeText(this, "Video file not found", Toast.LENGTH_SHORT).show()
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = ActivityUploadMainBinding.inflate(layoutInflater)

    binding.mainUploadNewVideo.setOnClickListener {
      if (!Util.haveRecordVideoPermissions(this)) {
        askForRecordPermission()
      } else {
        launchRecordActivity()
      }
    }
    binding.mainUploadLastVideo.setOnClickListener {
      val videoFile = Util.loadLastRecordedVideoPath(this)
      Intent(this, IngestVideoActivity::class.java).let {
        it.putExtra("input_file", videoFile)
        startActivity(it)
      }
    }
    binding.mainUploadLastVideo.isEnabled = Util.loadLastRecordedVideoPath(this) != null
    binding.mainPlayLastVideo.setOnClickListener {
      ExoPlayerDialog().apply {
        arguments =
          bundleOf("video_url" to Util.loadLastRecordedVideoUrl(this@UploadMainActivity))
      }.show(supportFragmentManager, "player")
    }

    setContentView(binding.root)
  }

  @SuppressLint("QueryPermissionsNeeded") // The Camera app is not hidden by the system
  private fun launchRecordActivity() {
    // Create a Video File, wrap it in a URI, and grant
    val videoFile =
      File(Util.getVideosDir(this), "example-video-${System.currentTimeMillis()}.mp4")
    val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, videoFile)
    // Get the default Camera app and grant URI permission for our new video file
    val defaultCameraInfo = packageManager.queryIntentActivities(
      ingestVideo.contract.createIntent(this, uri),
      PackageManager.MATCH_DEFAULT_ONLY
    ).also {
      Log.d(javaClass.simpleName, "Available Camera Apps: $it")
    }.firstOrNull()?.activityInfo

    if (defaultCameraInfo == null) {
      Log.d(javaClass.simpleName, "No Camera App was found")
      Toast.makeText(this, "No Camera App was found", Toast.LENGTH_SHORT).show()
    } else {
      viewModel.updateRecordedVideoFile(videoFile)
      grantUriPermission(
        defaultCameraInfo.applicationInfo.packageName,
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
      )
      ingestVideo.launch(uri)
    }

  }

  private fun askForRecordPermission() {
    Toast.makeText(
      this, "External Storage Permission is required for video" +
              " recording/upload", Toast.LENGTH_LONG
    ).show()
    requestRecordPermission.launch(
      arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
      )
    )
  }
}

