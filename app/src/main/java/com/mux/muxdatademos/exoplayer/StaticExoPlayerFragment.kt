package com.mux.muxdatademos.exoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mux.muxdatademos.VideoInfo
import com.mux.muxdatademos.databinding.FragmentStaticExoplayerBinding

/**
 * Shows a video specified by its URL, reporting playback statistics to Mux Data
 */
class StaticExoPlayerFragment : Fragment() {

    private lateinit var playerView: StyledPlayerView
    private var exoPlayer: SimpleExoPlayer? = null

    private val videoInfo: VideoInfo get() = requireArguments().getParcelable("video_info")!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStaticExoplayerBinding.inflate(inflater)
        playerView = binding.staticExoplayerNormal

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        exoPlayer = createPlayer()
        val mediaItem = MediaItem.fromUri(videoInfo.url)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    override fun onPause() {
        exoPlayer?.release()
        exoPlayer = null

        super.onPause()
    }

    private fun createPlayer(): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(requireContext()).apply {
            setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
        }.build()
    }
}
