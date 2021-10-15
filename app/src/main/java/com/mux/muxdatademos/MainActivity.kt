package com.mux.muxdatademos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.mux.muxdatademos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var staticPlayerSpinner: Spinner
    private lateinit var staticPlayerGo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityMainBinding.inflate(layoutInflater)

        // Link/Config for Static Player Example
        staticPlayerSpinner = viewBinding.mainStaticOptions.demoInfoStaticPlayerType
        val staticPlayerTypesAdapter =
            ArrayAdapter<PlayerType>(this, android.R.layout.simple_list_item_1, android.R.id.text1)
        staticPlayerTypesAdapter.addAll(PlayerType.values().asList())
        staticPlayerSpinner.adapter = staticPlayerTypesAdapter
        staticPlayerSpinner.setSelection(0)
        staticPlayerGo = viewBinding.mainStaticGo
        staticPlayerGo.setOnClickListener {
            val intent = Intent(this, StaticPlayerActivity::class.java)
            intent.putExtra("player", staticPlayerSpinner.selectedItemPosition)
            startActivity(intent)
        }

        setContentView(viewBinding.root)
    }
}
