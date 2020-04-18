package io.livri.ui.task.list

import android.app.Application
import androidx.lifecycle.*
import io.livri.database.LivriDatabase
import io.livri.domain.Task
import io.livri.repository.TasksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

enum class TaskListApiStatus { LOADING, ERROR, DONE }

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    private val database = LivriDatabase.getInstance(application)
    private val tasksRepository = TasksRepository(database)

    private val _searchTag = MutableLiveData("all")

    val searchTag: LiveData<String>
        get() = _searchTag

    val tasks: LiveData<List<Task>> = Transformations.switchMap(
        _searchTag,
        ::tasksFilter
    )
    private fun tasksFilter(tag: String) = tasksRepository.searchTasks(tag)

    private val _status = MutableLiveData<TaskListApiStatus>()

    val status: LiveData<TaskListApiStatus>
        get() = _status

    private val _navigateToSelectedTask = MutableLiveData<Task>()

    val navigateToSelectedTask: LiveData<Task>
        get() = _navigateToSelectedTask

    private val _selectedTask = MutableLiveData<Task>()

    val selectedTask: LiveData<Task>
        get() = _selectedTask


    /**
     * Call refreshTasks() on init so we can display status immediately.
     */
    init {
    }

    fun refreshTasks() {
        Timber.i("refreshTasks")
        coroutineScope.launch {
            try {
                _status.value = TaskListApiStatus.LOADING

                tasksRepository.refreshDataFromNetwork()

                _status.value = TaskListApiStatus.DONE
            } catch (e: Exception) {
                _status.value = TaskListApiStatus.ERROR
                Timber.e(e.toString())
            }
        }
        _status.value = TaskListApiStatus.DONE
    }

    fun updateFilter(tag: String) {
        Timber.i("updateFilter: $tag")
        coroutineScope.launch {
           apply { _searchTag.postValue(tag)  }
        }
    }

    fun setTaskDate(task: Task, field: Int, amount: Int) {
        coroutineScope.launch {
            try {
                val calendar = Calendar.getInstance()
                if (task.dueDate > calendar.time) {
                    calendar.time = task.dueDate
                }
                calendar.add(field, amount)
                task.dueDate = calendar.time

                tasksRepository.update(task.id, task)

            } catch (e: Exception) {
                Timber.e(e.toString())
            }
        }
    }

    fun doneTask(task: Task) {
        coroutineScope.launch {
            try {

                var cal: Calendar = Calendar.getInstance()
                cal.time = task?.dueDate
                val frequency: String? = task?.frequency

                when (frequency) {
                    "d" -> cal.add(Calendar.DATE, 1)
                    "m" -> cal.add(Calendar.MONTH, 1)
                    "a" -> cal.add(Calendar.YEAR, 1)
                }

                when (frequency) {
                    "d", "m", "a" -> {
                        task.dueDate = cal.time
                        tasksRepository.create(task)
                    }
                }

                tasksRepository.delete(task.id)
            } catch (e: Exception) {
                Timber.e(e.toString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedTask] [MutableLiveData]
     * @param task The [Task] that was clicked on.
     */
    fun displayTaskDetails(task: Task) {
        _navigateToSelectedTask.value = task
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedTask is set to null
     */
    fun displayTaskDetailsComplete() {
        _navigateToSelectedTask.value = null
    }

    fun setSelectedTask(task: Task) {
        _selectedTask.value = task
    }

    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TaskListViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
