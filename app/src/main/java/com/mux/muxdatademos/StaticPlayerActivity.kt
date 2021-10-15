package com.mux.muxdatademos

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.mux.muxdatademos.databinding.ActivityStaticPlayerBinding

class StaticPlayerActivity : AppCompatActivity() {

    companion object {
        const val PLAYER_FRAGMENT_TAG = "player_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityStaticPlayerBinding.inflate(layoutInflater)

        if(savedInstanceState == null) {
            val staticPlayerTypesAdapter =
                ArrayAdapter<PlayerType>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1
                )
            staticPlayerTypesAdapter.addAll(PlayerType.values().asList())
            viewBinding.staticPlayerHeader.demoInfoStaticPlayerType.adapter =
                staticPlayerTypesAdapter
            viewBinding.staticPlayerHeader.demoInfoStaticPlayerType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        addPlayerFragment(PlayerType.values()[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // no-op
                    }

                }
        }

        setContentView(viewBinding.root)
    }

    private fun addPlayerFragment(playerType: PlayerType) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.static_player_frag_container,
                playerType.createFragment(MuxDataConfigs.exampleVideoInfo()))
            .commit()
    }
}
