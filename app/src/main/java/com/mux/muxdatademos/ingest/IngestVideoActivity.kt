package com.mux.muxdatademos.ingest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.mux.muxdatademos.databinding.ActivityIngestVideoBinding
import java.io.File

class IngestVideoActivity : AppCompatActivity() {

    private val inputFile get() = intent.getSerializableExtra("input_file") as File
    private val viewModel by viewModels<IngestVideoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityIngestVideoBinding.inflate(layoutInflater)

        viewModel.state.observe(this) { state ->
            Log.d(javaClass.simpleName, "Moving to state $state")
            when (state) {
                IngestVideoViewModel.State.CREATING_ASSET -> {
                    viewBinding.ingestVideoIndProgress.visibility = View.VISIBLE
                    viewBinding.ingestVideoState.text = "Creating Asset"
                }
                IngestVideoViewModel.State.UPLOADING -> {
                    viewBinding.ingestVideoIndProgress.visibility = View.VISIBLE
                    viewBinding.ingestVideoState.text = "Uploading Video File"
                }
                IngestVideoViewModel.State.DONE -> {
                    viewBinding.ingestVideoIndProgress.visibility = View.GONE
                    viewBinding.ingestVideoState.text = "Done!"

                    //TODO: Show Thumbnail or enable player
                }
                IngestVideoViewModel.State.ERROR -> {
                    Toast.makeText(this, "Error uploading video. Check the logs", Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                    viewBinding.ingestVideoState.text = null
                    // No Special handling
                }
            }
        }
        viewModel.uploadProgress.observe(this) {
            //Log.v(javaClass.simpleName, "Upload Progress $it")
            viewBinding.ingestVideoUploadProgress.progress = it.first
            viewBinding.ingestVideoUploadProgress.max = it.second
        }

        setContentView(viewBinding.root)
    }

    override fun onStart() {
        super.onStart()

        viewModel.startUploadIfNotStarted(inputFile)
    }
}