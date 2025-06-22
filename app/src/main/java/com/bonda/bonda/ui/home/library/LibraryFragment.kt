package com.bonda.bonda.ui.home.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.FragmentHomeLibraryBinding

class LibraryFragment : Fragment() {

    private var _binding: FragmentHomeLibraryBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val libraryViewModel =
            ViewModelProvider(this)[LibraryViewModel::class.java]

//        libraryViewModel.text.observe(viewLifecycleOwner) {
//            binding.textLibrary.text = it
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}