package com.bonda.bonda.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonda.bonda.R
import com.google.android.material.chip.Chip
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class RecentArticleAdapter(
    private val items: List<RecentArticle>,
    private val onClick: (RecentArticle) -> Unit
) : RecyclerView.Adapter<RecentArticleAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ShapeableImageView = view.findViewById(R.id.image)
        private val categoryChip: Chip = view.findViewById(R.id.category)
        private val titleTv: MaterialTextView = view.findViewById(R.id.title)

        fun bind(article: RecentArticle) {
            // TODO: 실제 이미지 로딩 라이브러리로 교체
            imageView.setImageResource(R.drawable.dummy_article_cover3)

            // Glide 사용 예시
//            Glide.with(imageView.context)
//                .load(article.imageUrl)
//                .centerCrop()
//                .into(imageView)

            categoryChip.text = article.category
            titleTv.text      = article.title

            itemView.setOnClickListener { onClick(article) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_recent_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}