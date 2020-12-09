package io.livri.ui.task.detail

import android.app.Application
import androidx.lifecycle.*
import io.livri.database.LivriDatabase
import io.livri.domain.Task
import io.livri.repository.TasksRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*


enum class TaskDetailUpdateApiStatus { SAVING, ERROR, DONE }
enum class TaskDetailDeleteApiStatus { SAVING, ERROR, DONE }

enum class FrequencyStatus {}

class TaskDetailViewModel(task: Task, application: Application) : AndroidViewModel(application) {

    private val database = LivriDatabase.getInstance(application)
    private val tasksRepository = TasksRepository(database)

    private val _statusUpdate = MutableLiveData<TaskDetailUpdateApiStatus>()

    val statusUpdate: LiveData<TaskDetailUpdateApiStatus>
        get() = _statusUpdate

    private val _statusDelete = MutableLiveData<TaskDetailDeleteApiStatus>()

    val statusDelete: LiveData<TaskDetailDeleteApiStatus>
        get() = _statusDelete

    private val _selectedTask = MutableLiveData<Task>()

    // The external LiveData for the SelectedTask
    val selectedTask: LiveData<Task>
        get() = _selectedTask

    fun setTask(task: Task) {
        _selectedTask.value = task
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    // Initialize the _selectedTask MutableLiveData
    init {
        _selectedTask.value = task
    }

    fun setDueDate(dueDate: Date) {
        var task = _selectedTask.value
        task?.dueDate = dueDate
        _selectedTask.value = task
    }

    fun setTag(tag: String) {
        var task = _selectedTask.value
        task?.tags = tag
        _selectedTask.value = task
    }

    fun onFrequencyRadioButtonClick(frequency: String) {
        var task = _selectedTask.value
        task?.frequency = frequency
        _selectedTask.value = task
    }

    fun updateTask() {
        val task = _selectedTask.value

        coroutineScope.launch {
            try {
                _statusUpdate.value = TaskDetailUpdateApiStatus.SAVING

                tasksRepository.update(task!!.id, task)

                _statusUpdate.value = TaskDetailUpdateApiStatus.DONE
            } catch (e: Exception) {
                _statusUpdate.value = TaskDetailUpdateApiStatus.ERROR
            }
        }
    }

    fun deleteTask() {

        val task = _selectedTask.value

        coroutineScope.launch {
            try {
                _statusDelete.value = TaskDetailDeleteApiStatus.SAVING

                tasksRepository.delete(task!!.id)

                _statusDelete.value = TaskDetailDeleteApiStatus.DONE
            } catch (e: Exception) {
                _statusDelete.value = TaskDetailDeleteApiStatus.ERROR
            }
        }
    }

    fun doneTask() {

        val task = _selectedTask.value

        coroutineScope.launch {
            try {
                _statusUpdate.value = TaskDetailUpdateApiStatus.SAVING

                var cal: Calendar = Calendar.getInstance()
                cal.time  = task?.dueDate
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

                tasksRepository.delete(task!!.id)

                _statusUpdate.value = TaskDetailUpdateApiStatus.DONE
            } catch (e: Exception) {
                _statusUpdate.value = TaskDetailUpdateApiStatus.ERROR
            }

        }
    }


//    val displayDueDate = Transformations.map(selectedTask) {
//        app.applicationContext.getString(it.dueDate, it.dueDate)
//    }

    // The displayPropertyPrice formatted Transformation Map LiveData, which displays the sale
    // or rental price.
//    val displayPropertyPrice = Transformations.map(selectedTask) {
//        app.applicationContext.getString(
//            when (it.isRental) {
//                true -> R.string.display_price_monthly_rental
//                false -> R.string.display_price
//            }, it.price)
//    }

    // The displayPropertyType formatted Transformation Map LiveData, which displays the
    // "For Rent/Sale" String
//    val displayPropertyType = Transformations.map(selectedTask) {
//        app.applicationContext.getString(R.string.display_type,
//            app.applicationContext.getString(
//                when(it.isRental) {
//                    true -> R.string.type_rent
//                    false -> R.string.type_sale
//                }))
//    }

    class Factory(
        private val task: Task,
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
                return TaskDetailViewModel(task, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}