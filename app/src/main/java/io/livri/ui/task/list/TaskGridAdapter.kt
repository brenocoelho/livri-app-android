package io.livri.ui.task.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.livri.databinding.TaskListGridViewItemBinding
import io.livri.domain.Task

class TaskGridAdapter(val onClickListener: OnClickListener): ListAdapter<Task, TaskGridAdapter.TaskViewHolder>(DiffCallback) {

    class TaskViewHolder(private var binding: TaskListGridViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.task = task
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(TaskListGridViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(task)
        }
        holder.bind(task)
    }

    fun getTaskAt(position: Int): Task {
        return getItem(position)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Task]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Task]
     */
    class OnClickListener(val clickListener: (task: Task) -> Unit) {
        fun onClick(task: Task) = clickListener(task)
    }

}