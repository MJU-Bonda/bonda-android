package com.bonda.bonda.ui.profile.recent.articles

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bonda.bonda.R
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.model.toArticleCategory
import com.google.android.material.chip.Chip
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import kotlin.coroutines.coroutineContext

class ArticleAdapter(
    private val onClick: (Article) -> Unit
) : ListAdapter<Article, ArticleAdapter.ViewHolder>(DIFF_CALLBACK){

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(a: Article, b: Article) = a.id == b.id
            override fun areContentsTheSame(a: Article, b: Article) = a == b
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTv: MaterialTextView = view.findViewById(R.id.title)
        private val imageView: ShapeableImageView = view.findViewById(R.id.image)
        private val categoryChip: Chip = view.findViewById(R.id.category)

        fun bind(article: Article) {
            titleTv.text      = article.title
            imageView.load(article.imageUrl)
            categoryChip.text = article.category

            article.category.also {
                val category = it.toArticleCategory()

                categoryChip.text = category.label

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
                categoryChip.chipBackgroundColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.context, bgColorRes)
                    )
                categoryChip.setTextColor(
                    ContextCompat.getColor(itemView.context, textColorRes)
                )
            }

            itemView.setOnClickListener { onClick(article) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.view_recent_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}