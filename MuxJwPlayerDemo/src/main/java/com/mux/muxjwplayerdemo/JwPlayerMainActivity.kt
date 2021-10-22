package com.mux.muxjwplayerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.longtailvideo.jwplayer.JWPlayerView
import com.longtailvideo.jwplayer.configuration.PlayerConfig
import com.longtailvideo.jwplayer.license.LicenseUtil
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem

class JwPlayerMainActivity : AppCompatActivity() {

    lateinit var playerView: JWPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Replace with [YOUR KEY HERE]
        LicenseUtil.setLicenseKey(this, "5QwWG43Fz8JBm7dfyXaNI5wfNKHhzj88/ypd+ze445ufhExCdApPGX5KbGw=")

        setContentView(R.layout.activity_jw_player_main)

        playerView = findViewById(R.id.jwplayerview)
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

    private fun createPlayerConfig(playlist: List<PlaylistItem>): PlayerConfig =
        PlayerConfig.Builder()
            .playlist(playlist)
            .build()

    private fun createPlaylist(): List<PlaylistItem> =
        listOf(
            PlaylistItem.Builder()
                .title("Mux Sample Video")
                .file("https://cdn.jwplayer.com/manifests/Z6NjJRb2.m3u8")
                .build(),
        )
}
