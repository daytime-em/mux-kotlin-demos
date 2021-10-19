package com.mux.muxdatademos.ingest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.mux.muxdatademos.databinding.ActivityIngestVideoBinding
import java.io.File

class IngestVideoActivity : AppCompatActivity() {

    private val inputFile get() = intent.getSerializableExtra("input_file") as File
    private val viewModel by viewModels<IngestVideoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityIngestVideoBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)
    }
}