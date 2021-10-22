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
        val viewBinding = FragmentPlayerDialogBinding.inflate(inflater)

        val playerFragment = StaticExoPlayerFragment().apply {
            arguments = bundleOf("video_url" to videoUrl)
        }
        childFragmentManager.beginTransaction()
            .add(R.id.player_dialog_frag_container, playerFragment)
            .commit()

        return viewBinding.root
    }
}