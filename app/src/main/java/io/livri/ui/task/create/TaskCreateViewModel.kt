package io.livri.ui.task.create

import android.app.Application
import androidx.lifecycle.*
import io.livri.database.LivriDatabase
import io.livri.domain.Task
import io.livri.repository.TasksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

enum class TaskCreateApiStatus { SAVING, ERROR, DONE }

class TaskCreateViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LivriDatabase.getInstance(application)
    private val tasksRepository = TasksRepository(database)

    private val _task = MutableLiveData<Task>()

    // The external LiveData for the task
    val task: LiveData<Task>
        get() = _task

    private val _status = MutableLiveData<TaskCreateApiStatus>()

    val status: LiveData<TaskCreateApiStatus>
        get() = _status

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    // Initialize the _selectedTask MutableLiveData
    init {
        var cal = Calendar.getInstance()

        _task.value = Task(
            "",
            "",
            cal.time,
            "",
            "p",
            "o"
        )
    }

    fun setDueDate(dueDate: Date) {
        var task = _task.value
        task?.dueDate = dueDate
        _task.value = task
    }

    fun setTag(tag: String) {
        var task = _task.value
        task?.tags = tag
        _task.value = task
    }

    fun onFrequencyRadioButtonClick(frequency: String) {
        var task = _task.value
        task?.frequency = frequency
        _task.value = task
    }

    fun createTask() {
        val task = _task.value

        coroutineScope.launch {
            try {
                _status.value = TaskCreateApiStatus.SAVING

                tasksRepository.create(task!!)

                _status.value = TaskCreateApiStatus.DONE
            } catch (e: Exception) {
                _status.value = TaskCreateApiStatus.ERROR
            }
        }
    }


    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskCreateViewModel::class.java)) {
                return TaskCreateViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
