package com.bonda.bonda.ui.home.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bonda.bonda.databinding.ViewRecentArticleBinding
import com.bonda.bonda.network.model.article.SavedArticlesResponse

class SavedArticlePagingAdapter(
    private val onClick: (SavedArticlesResponse.Article) -> Unit
) : PagingDataAdapter<SavedArticlesResponse.Article, SavedArticlePagingAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SavedArticlesResponse.Article>() {
            override fun areItemsTheSame(
                oldItem: SavedArticlesResponse.Article,
                newItem: SavedArticlesResponse.Article
            ) =
                oldItem.articleId == newItem.articleId

            override fun areContentsTheSame(
                oldItem: SavedArticlesResponse.Article,
                newItem: SavedArticlesResponse.Article
            ) =
                oldItem == newItem
        }
    }

    inner class VH(private val binding: ViewRecentArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SavedArticlesResponse.Article) {
            binding.image.load(item.imageUrl)
            binding.title.text = item.title
            binding.category.root.text = item.articleCategory
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewRecentArticleBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}
