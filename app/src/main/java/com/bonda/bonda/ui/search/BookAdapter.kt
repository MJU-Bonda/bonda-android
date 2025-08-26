package com.bonda.bonda.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bonda.bonda.databinding.ViewBookVerticalBinding

class BookAdapter(private val onItemClicked: (Book) -> Unit) :

    ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback) {

    inner class BookViewHolder(private val binding: ViewBookVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.coverImage.load(book.imageUrl)
            binding.title.text = book.title
            binding.author.text = book.subtitle
            binding.category.root.text = book.category
            binding.root.setOnClickListener {
                onItemClicked(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ViewBookVerticalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }

}