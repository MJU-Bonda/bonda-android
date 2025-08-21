package com.bonda.bonda.ui.article

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import coil3.load
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentBookCardBinding
import com.bonda.bonda.model.toBookCategory
import com.bonda.bonda.ui.book.BookActivity

class BookCardFragment : Fragment(R.layout.fragment_book_card) {

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
            id: Long,
            coverImage: String,
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

    /**
     * fragment 데이터 바인딩
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()

        val index = args.getInt(ARG_INDEX)
        val id = args.getLong(ARG_ID)
        val coverImage = args.getString(ARG_COVER_IMAGE)
        val category = args.getString(ARG_CATEGORY)!!.toBookCategory().label
        val title = args.getString(ARG_TITLE)
        val author = args.getString(ARG_AUTHOR)
        val body = args.getString(ARG_BODY)

        binding.index.text = "%02d".format(index + 1)
        binding.coverImage.load(coverImage)
        binding.category.root.text = category
        binding.title.text = title
        binding.author.text = author
        binding.body.text = body

        /**
         * 도서 정보 확인하기 버튼 클릭 시 도서 상세 페이지로 이동합니다
         */
        binding.button.setOnClickListener {
            val intent = Intent(requireContext(), BookActivity::class.java)
            intent.putExtra("book_detail_id", id)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}