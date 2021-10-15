package com.mux.muxdatademos

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mux.muxdatademos.exoplayer.StaticExoPlayerFragment

/**
 * Represents a player framework supported by Mux Data. Provides ViewHolders and Player Fragments
 * that demonstrate usage for each Mux Data SDK
 */
enum class PlayerType {

    EXO {
        override fun createViewHolder(video: VideoInfo): RecyclerView.ViewHolder {
            TODO("Not yet implemented")
        }

        override fun createFragment(video: VideoInfo): Fragment {
            return StaticExoPlayerFragment().apply {
                arguments = bundleOf("video_info" to video)
            }
        }
    },
    JW {
        override fun createViewHolder(video: VideoInfo): RecyclerView.ViewHolder {
            TODO("Not yet implemented")
        }

        override fun createFragment(video: VideoInfo): Fragment {
            TODO("Not yet implemented")
        }
    },
    KALTURA {
        override fun createViewHolder(video: VideoInfo): RecyclerView.ViewHolder {
            TODO("Not yet implemented")
        }

        override fun createFragment(video: VideoInfo): Fragment {
            TODO("Not yet implemented")
        }
    };

    /**
     * Create a ViewHolder with a player inside, configured to report playback information to Mux Data
     */
    abstract fun createViewHolder(video: VideoInfo): RecyclerView.ViewHolder

    /**
     * Create a ViewHolder with a player inside, configured to report playback information to Mux Data
     */
    abstract fun createFragment(video: VideoInfo): Fragment
}
