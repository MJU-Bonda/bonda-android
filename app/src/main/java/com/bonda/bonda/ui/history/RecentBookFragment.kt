package com.bonda.bonda.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.databinding.FragmentRecentBooksBinding
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.book.BookActivity

class RecentBookFragment : Fragment() {

    private var _binding: FragmentRecentBooksBinding? = null
    private val binding get() = _binding!!


    val books = listOf(
        RecentBook(1, "https://…/book1.jpg", "에세이", "골라골라 나 같은 집", "스펙타클 편집부"),
        RecentBook(2, "https://…/book2.jpg", "소설", "어쩌구 저쩌구", "작가 이름"),
        RecentBook(3, "https://…/book2.jpg", "소설", "어쩌구 저쩌구", "작가 이름"),
        RecentBook(4, "https://…/book2.jpg", "소설", "어쩌구 저쩌구", "작가 이름"),
    )

    val articles = listOf(
        RecentArticle(1, "https://example.com", "작가/출판사", "오수영 작가의 사색과 감성"),
        RecentArticle(2, "https://example.com", "테마", "함께 살아가는 따뜻 이야기"),
        RecentArticle(3, "https://example.com", "테마", "집, 우리 삶의 거울"),
    )

    companion object {
        private const val ARG_TAB = "arg_tab"
        fun newInstance(tabIndex: Int) = RecentBookFragment().apply {
            arguments = bundleOf(ARG_TAB to tabIndex)
        }
    }

    private val tabIndex by lazy { requireArguments().getInt(ARG_TAB) }
    private val adapter by lazy {
        when (tabIndex) {
            0 -> RecentBookAdapter(books) { book ->
                // book 버튼 클릭 처리 코드를 여기에 입력하면 됩니다
                startActivity(Intent(requireContext(), BookActivity::class.java)
                    .putExtra("BOOK_ID", book.id))
            }
            else -> RecentArticleAdapter(articles) { article ->
                startActivity(Intent(requireContext(), ArticleActivity::class.java)
                    .putExtra("article_detail_id", article.id))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = binding.root
        recycler.layoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = 1
            }
        }
        recycler.adapter = adapter
//            RecentBookAdapter(books) {book ->
//            val intent = Intent(requireContext(), BookActivity::class.java)
//            intent.putExtra("book_detail_id", book.id)           // ← 여기서 id를 전달
//            startActivity(intent)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentBooksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}