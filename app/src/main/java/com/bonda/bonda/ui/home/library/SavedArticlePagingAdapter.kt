package com.bonda.bonda.ui.home.library

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ViewRecentArticleBinding
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.model.toArticleCategory
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
            binding.title.text = item.title.replace("\\n", "\n")

            item.articleCategory.also {
                val category = it.toArticleCategory()

                binding.category.root.text = category.label

                val bgColorRes = when (category) {
                    ArticleCategory.AUTHOR_OR_PUBLISHER -> R.color.surface_context_writer
                    ArticleCategory.BOOKSTORE -> R.color.surface_context_store
                    ArticleCategory.THEME -> R.color.surface_context_theme
                    else -> R.color.surface_default_primary
                }
                val textColorRes = when (category) {
                    ArticleCategory.AUTHOR_OR_PUBLISHER -> R.color.text_context_writer
                    ArticleCategory.BOOKSTORE -> R.color.text_context_store
                    ArticleCategory.THEME -> R.color.text_context_theme
                    else -> R.color.text_accent_primary
                }

                // Chip 에 적용
                binding.category.root.chipBackgroundColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.context, bgColorRes)
                    )
                binding.category.root.setTextColor(
                    ContextCompat.getColor(itemView.context, textColorRes)
                )
            }

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
