package com.mux.muxdatademos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mux.muxdatademos.databinding.ActivityStaticPlayerBinding

class StaticPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityStaticPlayerBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)
    }
}