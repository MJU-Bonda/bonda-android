package com.bonda.bonda.ui.article

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentBookCardBinding
import com.bonda.bonda.ui.book.BookActivity

class BookCardFragment: Fragment(R.layout.fragment_book_card) {

    private var _binding: FragmentBookCardBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_INDEX = "arg_index"
        private const val ARG_ID = "arg_id"
        private const val ARG_COVER_IMAGE = "arg_cover_image"
        private const val ARG_CATEGORY = "arg_category"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_AUTHOR = "arg_author"
        private const val ARG_BODY = "arg_body"

        fun newInstance(
            index: Int,
            id: Int,
            @DrawableRes coverImage: Int,
            category: String,
            title: String,
            author: String,
            body: String,
        ) = BookCardFragment().apply {
            arguments = bundleOf(
                ARG_INDEX to index,
                ARG_ID to id,
                ARG_COVER_IMAGE to coverImage,
                ARG_CATEGORY to category,
                ARG_TITLE to title,
                ARG_AUTHOR to author,
                ARG_BODY to body,
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookCardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()

        val index = args.getInt(ARG_INDEX)
        val id = args.getInt(ARG_ID)
        val coverImage = args.getInt(ARG_COVER_IMAGE)
        val category = args.getString(ARG_CATEGORY)!!
        val title = args.getString(ARG_TITLE)!!
        val author = args.getString(ARG_AUTHOR)!!
        val body = args.getString(ARG_BODY)!!

        binding.index.text = buildString {
            append("0")
            append((index + 1))
            append(" / 04")
        }
        binding.coverImage.setImageResource(coverImage)
        binding.category.text = category
        binding.title.text = title
        binding.author.text = author
        binding.body.text = body

        // start new book detail activity
        binding.button.setOnClickListener {
            val intent = Intent(requireContext(), BookActivity::class.java)
            intent.putExtra("book_detail_id", id)
            Log.d("DEBUG", "start_book_detail_activity_id : ${id}")
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}