package com.mux.muxplayback.player

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mux.muxplayback.MuxDataConfigs
import com.mux.muxplayback.databinding.FragmentStaticExoplayerBinding
import com.mux.stats.sdk.core.model.CustomerData
import com.mux.stats.sdk.muxstats.MuxStatsExoPlayer

/**
 * Shows a video specified by its URL, reporting playback statistics to Mux Data.
 */
class StaticExoPlayerFragment : Fragment() {

    private lateinit var playerView: StyledPlayerView
    private var exoPlayer: SimpleExoPlayer? = null
    private var muxStats: MuxStatsExoPlayer? = null

    private val videoUrl: String get() = requireArguments().getString("video_url")!!

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

        disposePlayer()
        exoPlayer = createPlayer()
        Log.d(javaClass.simpleName, "Playing Video at $videoUrl")
        val mediaItem = MediaItem.fromUri(videoUrl)
        playerView.player = exoPlayer
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
        disposePlayer()

        super.onPause()
    }

    private fun createMuxStats(player: SimpleExoPlayer): MuxStatsExoPlayer {
        // A SimpleExoPlayer is not strictly required, but if your ExoPlayer is a SimpleExoPlayer,
        //  additional metrics can be collected
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

    private fun disposePlayer() {
        playerView.player = null
        exoPlayer?.release()
        exoPlayer = null
        muxStats?.release()
        muxStats = null
    }
}
