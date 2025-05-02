package com.bonda.bonda.ui.article.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityArticleDetailBinding

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "화면 제목"
            setDisplayHomeAsUpEnabled(true)
        }

    }
}