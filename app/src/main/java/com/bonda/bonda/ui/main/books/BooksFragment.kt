package com.bonda.bonda.ui.main.books

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.FragmentBooksBinding
import com.bonda.bonda.ui.detail.article.ArticleActivity
import com.bonda.bonda.ui.detail.book.BookActivity
import com.bonda.bonda.ui.detail.onboarding.OnboardingActivity
import com.bonda.bonda.ui.test.TestActivity

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val booksViewModel =
            ViewModelProvider(this)[BooksViewModel::class.java]

        val textView: TextView = binding.textBooks
        booksViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.buttonBookDetail.setOnClickListener {
            val intent = Intent(requireContext(), BookActivity::class.java)
            startActivity(intent)
        }


        binding.buttonOnboarding.setOnClickListener {
            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            startActivity(intent)
        }

        binding.buttonTest.setOnClickListener {
            val intent = Intent(requireContext(), TestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}