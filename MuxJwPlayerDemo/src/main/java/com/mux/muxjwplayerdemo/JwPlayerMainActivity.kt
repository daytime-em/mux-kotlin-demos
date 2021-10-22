package com.mux.muxjwplayerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.longtailvideo.jwplayer.JWPlayerView
import com.longtailvideo.jwplayer.configuration.PlayerConfig
import com.longtailvideo.jwplayer.license.LicenseUtil
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem
import com.mux.stats.sdk.core.model.CustomerPlayerData
import com.mux.stats.sdk.core.model.CustomerVideoData
import com.mux.stats.sdk.muxstats.jwplayer.MuxStatsJWPlayer
import java.util.*

class JwPlayerMainActivity : AppCompatActivity() {

    companion object {
        const val VIDEO_URL = "https://cdn.jwplayer.com/manifests/Z6NjJRb2.m3u8"
    }

    lateinit var rootView: View
    lateinit var playerView: JWPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Replace with [YOUR KEY HERE]
        LicenseUtil.setLicenseKey(this, "5QwWG43Fz8JBm7dfyXaNI5wfNKHhzj88/ypd+ze445ufhExCdApPGX5KbGw=")

        setContentView(R.layout.activity_jw_player_main)

        rootView = findViewById(R.id.jw_player_root)
        playerView = findViewById(R.id.jw_player_view)
        playerView.addOnFullscreenListener {
            supportActionBar?.let { actionBar ->
                if(it.fullscreen) {
                    actionBar.hide()
                } else {
                    actionBar.show()
                }
            }
            rootView.fitsSystemWindows = !it.fullscreen
        }

        playerView.setup(createPlayerConfig(createPlaylist()))
    }

    override fun onStart() {
        super.onStart()

        playerView.onStart()
    }

    override fun onResume() {
        super.onResume()

        playerView.onResume()
    }

    override fun onPause() {
        playerView.onPause()

        super.onPause()
    }

    override fun onStop() {
        playerView.onStop()

        super.onStop()
    }

    override fun onDestroy() {
        playerView.onDestroy()

        super.onDestroy()
    }

    private fun createMuxStats(playerView: JWPlayerView): MuxStatsJWPlayer =
        MuxStatsJWPlayer(
            this,
            playerView,
            "Mux JW Player Demo",
                CustomerPlayerData().apply {
                    // Add or change properties here to customize player metadata such as ads,
                    //  experiments, etc
                    environmentKey = BuildConfig.MUX_DATA_ENV_KEY
                },
                CustomerVideoData().apply {
                    // Add or change properties here to customize video metadata such as title,
                    //   language, etc
                    videoTitle = "Mux ExoPlayer Android Example"
                    // Mux is not able to retrieve the URL of the video being played. It can be
                    //   supplied via this property
                    videoSourceUrl = VIDEO_URL
                }
        )

    private fun createPlayerConfig(playlist: List<PlaylistItem>): PlayerConfig =
        PlayerConfig.Builder()
            .playlist(playlist)
            .build()

    private fun createPlaylist(): List<PlaylistItem> =
        listOf(
            PlaylistItem.Builder()
                .title("Mux Sample Video")
                .file(VIDEO_URL)
                .build(),
        )
}
