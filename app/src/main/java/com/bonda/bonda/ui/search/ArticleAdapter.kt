package com.bonda.bonda.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bonda.bonda.databinding.ViewRecentArticleBinding
import com.bonda.bonda.model.setCategoryStyle

class ArticleAdapter(private val onItemClicked: (Article) -> Unit) :

    ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback) {

    inner class ArticleViewHolder(private val binding: ViewRecentArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.title.text = article.title.replace("\\n", "\n")
            binding.category.root.setCategoryStyle(article.category)
            binding.image.load(article.imageUrl)
            binding.root.setOnClickListener {
                onItemClicked(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ViewRecentArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }



        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

}