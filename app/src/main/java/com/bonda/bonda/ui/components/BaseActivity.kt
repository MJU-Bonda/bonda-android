package com.bonda.bonda.ui.components

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bonda.bonda.R
import com.bonda.bonda.databinding.LayoutBaseBinding

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var binding: LayoutBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.errorView.buttonRetry.setOnClickListener { onRetry() }
    }

    /**
     * 상속받는 클래스가 재정의할 컨텐츠 레이아웃 ID
     */
    protected fun setBaseContent(layoutResID: Int) {
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        layoutInflater.inflate(layoutResID, contentFrame, true)
    }

    protected fun setBaseContent(view: View) {
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        contentFrame.addView(view)
    }

    /**
     * 오류 화면을 표시 여부를 설정합니다
     */
    protected fun showErrorView(isError: Boolean) {
        if (isError) {
            binding.errorView.root.visibility = View.VISIBLE
        } else {
            binding.errorView.root.visibility = View.GONE
        }
    }

    /**
     * 오류 화면 다시 시도 버튼 클릭 시
     */
    protected abstract fun onRetry()

    /**
     * 로딩 화면을 표시 여부를 설정합니다
     */
    protected fun showLoadingView(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingView.root.visibility = View.VISIBLE
        } else {
            binding.loadingView.root.visibility = View.GONE
        }
    }

}