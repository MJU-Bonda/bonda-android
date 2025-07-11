package com.bonda.bonda.ui.profile.recent.books

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonda.bonda.R
import com.google.android.material.chip.Chip
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class BookAdapter(
    private val items: List<Book>,
    private val onClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ShapeableImageView = view.findViewById(R.id.image)
        private val categoryChip: Chip = view.findViewById(R.id.category)
        private val titleTv: MaterialTextView = view.findViewById(R.id.title)
        private val subtitleTv: MaterialTextView   = view.findViewById(R.id.subtitle)

        fun bind(book: Book) {
            /// TODO 이미지 연결필요
            imageView.setImageResource(R.drawable.dummy_book4)
            // 이미지 로드 (Glide 사용 예시)
//            Glide.with(imageView.context)
//                .load(book.imageUrl)
//                .centerCrop()
//                .into(imageView)

            categoryChip.text = book.category
            titleTv.text      = book.title
            subtitleTv.text   = book.subtitle

            itemView.setOnClickListener { onClick(book) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_recent_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
