package com.mux.muxplayback.player

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mux.muxplayback.BuildConfig
import com.mux.muxplayback.databinding.FragmentStaticExoplayerBinding
import com.mux.stats.sdk.core.model.*
import com.mux.stats.sdk.muxstats.MuxStatsExoPlayer
import java.util.*

/**
 * Shows a video specified by its URL, reporting playback statistics to Mux Data.
 */
class StaticExoPlayerFragment : Fragment() {

  private lateinit var playerView: StyledPlayerView
  private var exoPlayer: ExoPlayer? = null
  private var muxStats: MuxStatsExoPlayer? = null

  private val videoUrl: String get() = requireArguments().getString("video_url")!!

  companion object {
    fun addIfNotAdded(fragmentManager: FragmentManager, @IdRes containerId: Int, videoUrl: String) {
      if (fragmentManager.findFragmentById(containerId) == null) {
        fragmentManager.beginTransaction()
          .add(
            containerId,
            StaticExoPlayerFragment().apply {
              arguments = bundleOf("video_url" to videoUrl)
            },
          )
          .commit()
      }
    }
  }

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

  private fun createMuxStats(player: ExoPlayer): MuxStatsExoPlayer {
    // A SimpleExoPlayer is not strictly required, but if your ExoPlayer is a SimpleExoPlayer,
    //  additional metrics can be collected
    return MuxStatsExoPlayer(
      requireContext(),
      BuildConfig.MUX_DATA_ENV_KEY,
      player,
      CustomerData().apply {
        customerVideoData = CustomerVideoData().apply {
          // Add or change properties here to customize video metadata such as title,
          //   language, etc
          videoTitle = "Mux ExoPlayer Android Example"
          // Mux is not able to retrieve the URL of the video being played. It can be
          //   supplied via this property
          videoSourceUrl = videoUrl
        }
        customerViewData = CustomerViewData().apply {
          // A a unique identifier for this View event
          viewSessionId = UUID.randomUUID().toString()
        }
        customData = CustomData().apply {
          // Add values for your Custom Dimensions.
          // Up to 5 strings can be set to track your own data
          customData1 = "Hello"
          customData2 = "World"
          customData3 = "From"
          customData4 = "Mux"
          customData5 = "Data"
        }
      }
    )
  }

  private fun createPlayer(): ExoPlayer {
    return ExoPlayer.Builder(requireContext())
      .build().apply {
        addListener(object : Player.Listener {
          override fun onPlayerError(error: PlaybackException) {
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
