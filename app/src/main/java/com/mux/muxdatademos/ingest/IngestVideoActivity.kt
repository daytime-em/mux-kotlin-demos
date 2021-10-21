package com.mux.muxdatademos.ingest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.exoplayer2.SimpleExoPlayer
import com.mux.muxdatademos.R
import com.mux.muxdatademos.Util
import com.mux.muxdatademos.VideoInfo
import com.mux.muxdatademos.databinding.ActivityIngestVideoBinding
import com.mux.muxdatademos.databinding.FragmentPlayerDialogBinding
import com.mux.muxdatademos.exoplayer.StaticExoPlayerFragment
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
                IngestVideoViewModel.State.CREATING_UPLOAD -> {
                    viewBinding.ingestVideoIndProgress.visibility = View.VISIBLE
                    viewBinding.ingestVideoState.text = "Creating Upload"
                }
                IngestVideoViewModel.State.UPLOADING -> {
                    viewBinding.ingestVideoIndProgress.visibility = View.VISIBLE
                    viewBinding.ingestVideoState.text = "Uploading Video File"
                }
                IngestVideoViewModel.State.DONE -> {
                    viewBinding.ingestVideoIndProgress.visibility = View.GONE
                    viewBinding.ingestVideoState.text = "Done!"

                    //TODO: Show Thumbnail or enable "go to player"
                }
                IngestVideoViewModel.State.AWAITING_PROCESSING -> {
                    viewBinding.ingestVideoState.text = "Awaiting Processing"
                }
                IngestVideoViewModel.State.CREATING_PLAYBACK_ID -> {
                    viewBinding.ingestVideoState.text = "Creating Playback ID"
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
        viewModel.playbackId.observe(this) {
            it?.let { playbackId ->
                viewBinding.ingestVideoPlaybackId.text = "playbackId: $playbackId"
                viewBinding.ingestVideoPlay.isEnabled = true
                viewBinding.ingestVideoPlay.setOnClickListener {
                    Log.d("MEEEP", "Click! Playback id is $playbackId")
                    PlayerDialog().apply {
                        Log.d("MEEEP", "INSIDE APPLY")
                        arguments = bundleOf("video_info" to VideoInfo(playbackId))
                        Log.d("MEEEP", "Frag Args: $arguments")
                    }.show(supportFragmentManager, "player_dialog")
                }
            }
        }

        setContentView(viewBinding.root)
    }

    override fun onStart() {
        super.onStart()

        viewModel.startUploadIfNotStarted(inputFile)
    }
}

/**
 * Wraps a StaticExoPlayerFragment in a Dialog Fragment and presents it
 */
class PlayerDialog : DialogFragment() {

    private val videoInfo: VideoInfo get() = requireArguments().getParcelable("video_info")!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentPlayerDialogBinding.inflate(inflater)

        val playerFragment = StaticExoPlayerFragment().apply {
            arguments = bundleOf("video_url" to Util.createMuxHlsUrl(videoInfo.id))
        }
        childFragmentManager.beginTransaction()
            .add(R.id.player_dialog_frag_container, playerFragment)
            .commit()

        return viewBinding.root
    }
}
