package io.livri.ui.task.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.livri.domain.Tag

class TaskListTagSharedViewModel : ViewModel() {
    val selected = MutableLiveData<Tag>()

    fun select(tag: Tag) {
        selected.value = tag
    }

    fun selected() : Boolean {
        return (selected.value != null)
    }

    fun clean() {
        selected.value = null
    }
}