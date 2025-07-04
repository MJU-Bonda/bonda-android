package com.bonda.bonda.ui.home.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bonda.bonda.databinding.FragmentHomeLibraryScrollerBinding
import com.bonda.bonda.databinding.ViewRecentArticleBinding

class LibraryScrollerFragment : Fragment() {

    private var _binding: FragmentHomeLibraryScrollerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryScrollerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(arguments?.getInt("position")) {
            0 -> { // 도서 추가
                val parent = binding.container

                val gridLayout = GridLayout(parent.context).apply {
                    columnCount = 3
                    val pad = (16 * resources.displayMetrics.density).toInt()
                    setPadding(pad, 0, pad, 0)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }


                for (i in 1..16) {
                    val imageView = ImageView(parent.context).apply {
                        val resId = resources.getIdentifier("dummy_book$i", "drawable", parent.context.packageName)
                        setImageResource(resId)

                        adjustViewBounds = true
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }

                    val marginHorizontal = (18 * parent.context.resources.displayMetrics.density).toInt()
                    val marginTop = (40 * parent.context.resources.displayMetrics.density).toInt()

                    val specCol = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    val specRow = GridLayout.spec(GridLayout.UNDEFINED,GridLayout.BOTTOM, 1f)
                    val lp = GridLayout.LayoutParams(specRow, specCol).apply {
                        width = 0
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                        setMargins(marginHorizontal,marginTop, marginHorizontal,0)
                    }

                    gridLayout.addView(imageView, lp)
                }

                parent.addView(gridLayout)


            }
            1 -> { // 아티클 추가


                val parent = binding.container

                val gridLayout = GridLayout(parent.context).apply {
                    columnCount = 2
                    val pad = (24 * resources.displayMetrics.density).toInt()
                    setPadding(pad, 0, pad, 0)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }


                for (i in 1..16) {
                    val itemBinding = ViewRecentArticleBinding.inflate(layoutInflater, gridLayout, false)



                    val imageView = ImageView(parent.context).apply {
                        val resId = resources.getIdentifier("dummy_book$i", "drawable", parent.context.packageName)
                        setImageResource(resId)

                        adjustViewBounds = true
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }

                    val marginHorizontal = (18 * parent.context.resources.displayMetrics.density).toInt()
                    val marginTop = (40 * parent.context.resources.displayMetrics.density).toInt()

                    val specCol = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    val specRow = GridLayout.spec(GridLayout.UNDEFINED,GridLayout.BOTTOM, 1f)
                    val lp = GridLayout.LayoutParams(specRow, specCol).apply {
                        width = 0
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                        setMargins(marginHorizontal,marginTop, marginHorizontal,0)
                    }

                    gridLayout.addView(imageView, lp)
                }

                parent.addView(gridLayout)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TAB = "arg_tab"
        fun newInstance(position: Int): LibraryScrollerFragment {
            val fragment = LibraryScrollerFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }
}