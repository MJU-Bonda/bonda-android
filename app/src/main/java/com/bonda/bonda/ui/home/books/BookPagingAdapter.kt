package com.bonda.bonda.ui.home.books

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bonda.bonda.databinding.ViewBookVerticalBinding
import com.bonda.bonda.network.model.book.BooksByCategoryResponse

class BookPagingAdapter(
    private val onClick: (BooksByCategoryResponse.Book) -> Unit
) : PagingDataAdapter<BooksByCategoryResponse.Book, BookPagingAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<BooksByCategoryResponse.Book>() {
            override fun areItemsTheSame(
                oldItem: BooksByCategoryResponse.Book,
                newItem: BooksByCategoryResponse.Book
            ) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: BooksByCategoryResponse.Book,
                newItem: BooksByCategoryResponse.Book
            ) =
                oldItem == newItem
        }
    }

    inner class VH(private val binding: ViewBookVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BooksByCategoryResponse.Book) {
            binding.coverImage.load(item.imageUrl)
            binding.title.text = item.title
            binding.author.text = item.author
            binding.category.root.text = item.category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewBookVerticalBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}
