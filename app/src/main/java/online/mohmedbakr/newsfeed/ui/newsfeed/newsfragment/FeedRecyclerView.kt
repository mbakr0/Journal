package online.mohmedbakr.newsfeed.ui.newsfeed.newsfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.databinding.MainListItemBinding

class FeedRecyclerView(
    private val progressColor: Int,
    private val itemClick: ((link: String) -> Unit),
    private val bookmarkClick: ((article: ArticleDTO) -> Unit),
) : ListAdapter<ArticleDTO, FeedRecyclerView.ViewHolder>(ArticleDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, progressColor, bookmarkClick, itemClick)
    }


    class ViewHolder private constructor(private val binding: MainListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ArticleDTO,
            progressColor: Int,
            bookmarkClick: (article: ArticleDTO) -> Unit,
            itemClick: (link: String) -> Unit,
        ) {
            binding.article = item
            binding.progressColor = progressColor
            val url = item.link
            binding.singleItem.setOnClickListener {
                itemClick(url)
            }
            binding.bookmarkIcon.setOnClickListener {
                bookmarkClick(item)
            }
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val binding = MainListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ArticleDiffUtilCallback : DiffUtil.ItemCallback<ArticleDTO>() {
    override fun areItemsTheSame(oldItem: ArticleDTO, newItem: ArticleDTO): Boolean {
        return oldItem == newItem
    }


    override fun areContentsTheSame(oldItem: ArticleDTO, newItem: ArticleDTO): Boolean {
        return oldItem.link == newItem.link
    }


}