package io.livri.util

import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.livri.R
import io.livri.domain.Tag
import io.livri.domain.Task
import io.livri.ui.tag.create.TagCreateApiStatus
import io.livri.ui.tag.list.TagListApiStatus
import io.livri.ui.tag.list.TagGridAdapter
import io.livri.ui.task.create.TaskCreateApiStatus
import io.livri.ui.task.detail.TaskDetailDeleteApiStatus
import io.livri.ui.task.detail.TaskDetailUpdateApiStatus
import io.livri.ui.task.list.TaskListApiStatus
import io.livri.ui.task.list.TaskGridAdapter
import java.text.SimpleDateFormat
import java.util.Date

/**
 * When there is no Mars property data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("app:taskListData")
fun bindTaskRecyclerView(recyclerView: RecyclerView, data: List<Task>?) {
    val adapter = recyclerView.adapter as TaskGridAdapter
    adapter.submitList(data)
}

@BindingAdapter("tagListData")
fun bindTagRecyclerView(recyclerView: RecyclerView, data: List<Tag>?) {
    val adapter = recyclerView.adapter as TagGridAdapter
    adapter.submitList(data)
}

@BindingAdapter("TaskListSwipeApiStatus")
fun bindTaskListStatus(statusSwipeRefreshLayout: SwipeRefreshLayout, status: TaskListApiStatus?) {
    when (status) {
        TaskListApiStatus.LOADING -> {
            statusSwipeRefreshLayout.setRefreshing(true)
        }
        TaskListApiStatus.ERROR -> {
            statusSwipeRefreshLayout.setRefreshing(false)
        }
        TaskListApiStatus.DONE -> {
            statusSwipeRefreshLayout.setRefreshing(false)
        }
    }
}

@BindingAdapter("TagListSwipeApiStatus")
fun bindTagListStatus(statusSwipeRefreshLayout: SwipeRefreshLayout, status: TagListApiStatus?) {
    when (status) {
        TagListApiStatus.LOADING -> {
            statusSwipeRefreshLayout.setRefreshing(true)
        }
        TagListApiStatus.ERROR -> {
            statusSwipeRefreshLayout.setRefreshing(false)
        }
        TagListApiStatus.DONE -> {
            statusSwipeRefreshLayout.setRefreshing(false)
        }
    }
}

@BindingAdapter("TaskListImageApiStatus")
fun bindTaskListStatus(statusImageView: ImageView, status: TaskListApiStatus?) {
    when (status) {
        TaskListApiStatus.LOADING -> {
            statusImageView.visibility = View.GONE
        }
        TaskListApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        TaskListApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}


@BindingAdapter("TagListImageApiStatus")
fun bindTagListStatus(statusImageView: ImageView, status: TagListApiStatus?) {
    when (status) {
        TagListApiStatus.LOADING -> {
            statusImageView.visibility = View.GONE
        }
        TagListApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        TagListApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("TaskCreateApiStatus")
fun bindTaskCreateStatus(statusButton: Button, status: TaskCreateApiStatus?) {
    when (status) {
        TaskCreateApiStatus.SAVING -> {
            statusButton.visibility = View.VISIBLE
            statusButton.text = "Creating..."
        }
        TaskCreateApiStatus.ERROR -> {
            statusButton.visibility = View.VISIBLE
            statusButton.text = "Error..."
        }
        TaskCreateApiStatus.DONE -> {
            statusButton.visibility = View.GONE
        }
    }
}

@BindingAdapter("TagCreateApiStatus")
fun bindTagCreateStatus(statusButton: Button, status: TagCreateApiStatus?) {
    when (status) {
        TagCreateApiStatus.SAVING -> {
            statusButton.visibility = View.VISIBLE
            statusButton.text = "Creating..."
        }
        TagCreateApiStatus.ERROR -> {
            statusButton.visibility = View.VISIBLE
            statusButton.text = "Error..."
        }
        TagCreateApiStatus.DONE -> {
            statusButton.visibility = View.GONE
        }
    }
}

@BindingAdapter("TaskDetailUpdateApiStatus")
fun bindTaskDetailUpdateStatus(statusButton: Button, status: TaskDetailUpdateApiStatus?) {
    when (status) {
        TaskDetailUpdateApiStatus.SAVING -> {
            statusButton.visibility = View.VISIBLE
            statusButton.text = "Updating..."
        }
        TaskDetailUpdateApiStatus.ERROR -> {
            statusButton.visibility = View.VISIBLE
            statusButton.text = "Error..."
        }
        TaskDetailUpdateApiStatus.DONE -> {
            statusButton.visibility = View.GONE
        }
    }
}

@BindingAdapter("TaskDetailDeleteApiStatus")
fun bindTaskDetailDeleteStatus(statusButton: Button, status: TaskDetailDeleteApiStatus?) {
    when (status) {
        TaskDetailDeleteApiStatus.SAVING -> {
            statusButton.visibility = View.VISIBLE
            statusButton.text = "Deleting..."
        }
        TaskDetailDeleteApiStatus.ERROR -> {
            statusButton.visibility = View.VISIBLE
            statusButton.text = "Error..."
        }
        TaskDetailDeleteApiStatus.DONE -> {
            statusButton.visibility = View.GONE
        }
    }
}

val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
@BindingAdapter("DateFormat")
fun bindDateFormat(view: TextView, dueDate: Date?) {
    view.text = simpleDateFormat.format(dueDate)
}

@BindingAdapter("FrequencyAutoRenew")
fun bindFrequencyAutoRenew(view: View, frequency: String) {
    view.visibility = if (frequency == "o") {
        View.GONE
    }
    else {
        View.VISIBLE
    }
}

@BindingAdapter("TagPickColor")
fun bindTagPickColor(imageButton: ImageButton, match: Boolean) {
    if (match) {
        imageButton.setImageResource(R.drawable.ic_check_white_24dp)
    } else {
        imageButton.setImageResource(0)
    }
}

@BindingAdapter("TagTextView")
fun bindTagTextView(textView: TextView, tag: String?) {

    if (!tag.isNullOrBlank()) {
        val background = getTagColor(tag.substring(0,6))
        textView.setBackgroundColor(background)
        val name: String = " " + tag.substring(6) + " "
        textView.text = name
    }
    else {
        textView.setBackgroundColor(Color.WHITE)
        textView.text = ""
    }
}
@BindingAdapter("backgroundColorCardView")
fun bindTagTextView(cardview: CardView, color: String?) {
    val background = getTagColor(color!!)
    cardview.setBackgroundColor(background)
}

fun getTagColor(color: String): Int {
    return if (!color.isBlank() && color[0] != '#') {
        Color.parseColor("#$color")
    }
    else {
        Color.WHITE
    }
}