package com.mux.muxdatademos.exoplayer

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mux.muxdatademos.MuxDataConfigs
import com.mux.muxdatademos.VideoInfo
import com.mux.muxdatademos.databinding.FragmentStaticExoplayerBinding
import com.mux.stats.sdk.core.model.CustomerData
import com.mux.stats.sdk.muxstats.MuxStatsExoPlayer

/**
 * Shows a video specified by its URL, reporting playback statistics to Mux Data.
 */
class StaticExoPlayerFragment : Fragment() {

    private lateinit var playerView: StyledPlayerView
    private var exoPlayer: SimpleExoPlayer? = null
    private var muxStats: MuxStatsExoPlayer? = null

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
        // create your MuxStatsExoPlayer instance before calling prepare() on your ExoPlayer
        muxStats = createMuxStats(exoPlayer!!)
        // Screen Size is set from the WindowManager.
        Point().let { size ->
            requireActivity().windowManager.defaultDisplay.getSize(size)
            muxStats?.setScreenSize(size.x, size.y)
        }
        muxStats?.setPlayerView(playerView.videoSurfaceView)

        // Complete all MuxStats setup before calling prepare()
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    override fun onPause() {
        exoPlayer?.release()
        exoPlayer = null
        muxStats?.release()
        muxStats = null

        super.onPause()
    }

    private fun createMuxStats(player: SimpleExoPlayer): MuxStatsExoPlayer {
        return MuxStatsExoPlayer(
            requireContext(),
            player,
            "mux_data_android_demo",
            CustomerData().apply {
                customerPlayerData = MuxDataConfigs.examplePlayerData()
                customerVideoData = MuxDataConfigs.exampleVideoData()
                customerViewData = MuxDataConfigs.exampleViewData()
                customData = MuxDataConfigs.exampleCustomData()
            }
        )
    }

    private fun createPlayer(): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(requireContext())
            .setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: ExoPlaybackException) {
                        Log.e(javaClass.simpleName, "Player Error", error)
                        Toast.makeText(
                            requireContext(),
                            "Player Error: " + error.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
    }
}
