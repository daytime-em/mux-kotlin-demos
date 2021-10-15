package com.mux.muxdatademos

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

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
            TODO("Not yet implemented")
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
