package com.bonda.bonda.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.bonda.bonda.R
import com.bonda.bonda.databinding.LayoutBaseBinding

abstract class BaseFragment : Fragment() {

    private var _binding: LayoutBaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.errorView.buttonRetry.setOnClickListener { onRetry() }
    }

    /**
     * 상속받는 클래스가 재정의할 컨텐츠 레이아웃 ID
     */
    protected fun setBaseContent(layoutResID: Int) {
        val contentFrame = view?.findViewById<FrameLayout>(R.id.content_frame)
        contentFrame?.let {
            layoutInflater.inflate(layoutResID, it, true)
        }
    }

    protected fun setBaseContent(view: View) {
        val contentFrame = getView()?.findViewById<FrameLayout>(R.id.content_frame)
        contentFrame?.addView(view)
    }

    /**
     * 오류 화면을 표시 여부를 설정합니다
     */
    protected fun showErrorView(isError: Boolean) {
        binding.errorView.root.visibility = if (isError) View.VISIBLE else View.GONE
    }

    /**
     * 오류 화면 다시 시도 버튼 클릭 시
     */
    protected abstract fun onRetry()

    /**
     * 로딩 화면을 표시 여부를 설정합니다
     */
    protected fun showLoadingView(isLoading: Boolean) {
        binding.loadingView.root.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}