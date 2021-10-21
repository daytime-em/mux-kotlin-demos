package com.mux.muxdatademos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.mux.muxdatademos.databinding.ActivityMainBinding
import com.mux.muxuploaddemo.ingest.IngestVideoActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private val ingestVideo =
        registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success) {
                // !! safe if value was set before launching the camera app
                Util.saveLastRecordedVideo(this, viewModel.recordedVideoFile.value!!)
                Intent(this, IngestVideoActivity::class.java).let {
                    it.putExtra("input_file", viewModel.recordedVideoFile.value)
                    startActivity(it)
                }
            } else {
                Toast.makeText(this, "Video file not found", Toast.LENGTH_SHORT).show()
            }
        }

    // In order to record via the Camera app, we must have the Camera permission
    // In order for files to appear in the user's gallery, External Storage is required
    // TODO: Give advice about how it might be hard to use ActivityResultContract.TakeVideo
    //  with internal storage, due to the URI permissions not being accessible (if true)
    //  (see other notes in Util.kt)
    private val requestRecordPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedPermission ->
            val missedPermission = grantedPermission.values.contains(false)
            if (missedPermission) {
                askForRecordPermission()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding = ActivityMainBinding.inflate(layoutInflater)

        // "Post New Video"/"Upload Last Video" links
        viewBinding.mainPostVideo.setOnClickListener {
            if (!Util.haveRecordVideoPermissions(this)) {
                askForRecordPermission()
            } else {
                launchRecordActivity()
            }
        }
        viewBinding.mainUploadLastVideo.setOnClickListener {
            val videoFile = Util.loadLastRecordedVideoPath(this)
            Intent(this, IngestVideoActivity::class.java).let {
                it.putExtra("input_file", videoFile)
                startActivity(it)
            }
        }
        viewBinding.mainUploadLastVideo.isEnabled = Util.loadLastRecordedVideoPath(this) != null

        // Link/Config for Static Player Example
        val staticPlayerSpinner = viewBinding.mainStaticOptions.demoInfoStaticPlayerType
        val staticPlayerTypesAdapter =
            ArrayAdapter<PlayerType>(this, android.R.layout.simple_list_item_1, android.R.id.text1)
        staticPlayerTypesAdapter.addAll(PlayerType.values().asList())
        staticPlayerSpinner.adapter = staticPlayerTypesAdapter
        staticPlayerSpinner.setSelection(0)
        val staticPlayerGo = viewBinding.mainStaticGo
        staticPlayerGo.setOnClickListener {
            val intent = Intent(this, StaticPlayerActivity::class.java)
            intent.putExtra("player", staticPlayerSpinner.selectedItemPosition)
            startActivity(intent)
        }

        setContentView(viewBinding.root)
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
            )
        )
    }
}
