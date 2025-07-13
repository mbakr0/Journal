package online.mohmedbakr.newsfeed.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import online.mohmedbakr.newsfeed.data.model.Article
import online.mohmedbakr.newsfeed.databinding.BookmarkListItemBinding

class BookmarkRecyclerView(
    private val itemClick: ((link: String) -> Unit),
    private val bookmarkClick: (article: Article) -> Unit,
) : ListAdapter<Article, BookmarkRecyclerView.ViewHolder>(
    ArticleDiffUtilCallback()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, bookmarkClick)
        holder.itemView.setOnClickListener {
            itemClick(item.url)
        }
    }

    class ViewHolder private constructor(private val binding: BookmarkListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Article, bookmarkClick: (article: Article) -> Unit) {
            binding.article = item
            binding.bookmarkIcon.setOnClickListener {
                bookmarkClick(item)
            }
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val binding = BookmarkListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class ArticleDiffUtilCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem === newItem
        }


        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }


    }

}