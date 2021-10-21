package com.mux.muxdatademos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.mux.muxdatademos.databinding.ActivityExoMainBinding
import com.mux.muxplayback.player.StaticExoPlayerFragment

class ExoMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding = ActivityExoMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Replace this with any URL to collect stats for your video
        val mediaUrl = Util.createMuxHlsUrl("3FqPhJq8qXsqmIXPPKTjQa9005aT6HEUi0194BKYY27MA")
        StaticExoPlayerFragment.addIfNotAdded(this, R.id.main_frag_container, mediaUrl)
    }
}
