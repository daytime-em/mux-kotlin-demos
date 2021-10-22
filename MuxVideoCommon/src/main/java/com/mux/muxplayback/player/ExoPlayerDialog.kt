package com.mux.muxplayback.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.mux.muxplayback.R
import com.mux.muxplayback.databinding.FragmentPlayerDialogBinding

/**
 * Wraps a StaticExoPlayerFragment in a Dialog Fragment and presents it
 */
class ExoPlayerDialog : DialogFragment() {

    private val videoUrl: String get() = requireArguments().getString("video_url")!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentPlayerDialogBinding.inflate(inflater).root
    }

    override fun onStart() {
        super.onStart()

        StaticExoPlayerFragment.addIfNotAdded(
            childFragmentManager,
            R.id.player_dialog_frag_container,
            videoUrl
        )
    }
}