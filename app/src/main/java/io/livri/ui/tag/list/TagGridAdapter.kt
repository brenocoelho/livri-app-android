package io.livri.ui.tag.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.livri.databinding.TagListGridViewItemBinding
import io.livri.domain.Tag

class TagGridAdapter(val onClickListener: OnClickListener): ListAdapter<Tag, TagGridAdapter.TagViewHolder>(DiffCallback) {

    class TagViewHolder(private var binding: TagListGridViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: Tag) {
            binding.tag = tag
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Tag>() {
        override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(TagListGridViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(tag)
        }
        holder.bind(tag)
    }

    fun getTagAt(position: Int): Tag {
        return getItem(position)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Task]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Task]
     */
    class OnClickListener(val clickListener: (tag: Tag) -> Unit) {
        fun onClick(tag: Tag) = clickListener(tag)
    }
}