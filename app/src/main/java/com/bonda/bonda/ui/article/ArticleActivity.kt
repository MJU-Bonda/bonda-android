package com.bonda.bonda.ui.article

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        supportActionBar?.apply {
            title = "BONDA"
            setDisplayHomeAsUpEnabled(true)
        }

        // view-model 적용
        val articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)
        articleViewModel.title.observe(this) { binding.articleTitle.text = it }
        articleViewModel.subTitle.observe(this) { binding.articleSubtitle.text = it }
        articleViewModel.body.observe(this) { binding.articleBody.text = it }
    }
}