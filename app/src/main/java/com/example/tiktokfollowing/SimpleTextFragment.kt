package com.example.tiktokfollowing

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

class SimpleTextFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tv = TextView(requireContext())
        tv.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        tv.gravity = Gravity.CENTER
        tv.textSize = 16f
        tv.text = arguments?.getString(ARG_TEXT) ?: ""
        return tv
    }

    companion object {
        private const val ARG_TEXT = "text"

        fun newInstance(text: String): SimpleTextFragment {
            val f = SimpleTextFragment()
            f.arguments = bundleOf(ARG_TEXT to text)
            return f
        }
    }
}
