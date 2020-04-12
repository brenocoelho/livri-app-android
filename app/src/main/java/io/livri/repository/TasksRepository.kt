package io.livri.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.livri.database.LivriDatabase
import io.livri.database.task.asDomainModel
import io.livri.domain.Task
import io.livri.domain.asNetworkModel
import io.livri.network.Network
import io.livri.network.asDatabaseModel
import io.livri.network.asRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksRepository(private val database: LivriDatabase) {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _tasks = MutableLiveData<List<Task>>()

    // The external immutable LiveData for the request status String
    val tasks: LiveData<List<Task>>
        get() = _tasks

    suspend fun getDataFromDatabase(tag: String? = null) {
        withContext(Dispatchers.IO) {
            if (tag.isNullOrBlank()) {
                _tasks.postValue(database.taskDao.getAllTasks().asDomainModel())
            } else {
                _tasks.postValue(database.taskDao.getTasksByTag(tag).asDomainModel())
            }
        }
    }

    suspend fun refreshDataFromNetwork() {
        withContext(Dispatchers.IO) {
            val taskNetworkContainer = Network.retrofitService.getTasks()
            database.taskDao.clear()
            database.taskDao.insertAll(*taskNetworkContainer.asDatabaseModel())
        }
    }

    suspend fun create(task: Task) {
        withContext(Dispatchers.IO) {
            val request = task.asNetworkModel().asRequest()
            Network.retrofitService.createTask(request)
        }
    }

    suspend fun update(id: String, task: Task) {
        withContext(Dispatchers.IO) {
            val request = task.asNetworkModel().asRequest()
            Network.retrofitService.updateTask(id, request)
        }
    }

    suspend fun delete(id: String) {
        withContext(Dispatchers.IO) {
            Network.retrofitService.deleteTask(id)
            database.taskDao.delete(id)
        }
    }

}