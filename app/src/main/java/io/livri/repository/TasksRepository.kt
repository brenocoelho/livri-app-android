package io.livri.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    fun searchTasks(tag : String = "all"): LiveData<List<Task>> {
        return if (tag == "all") Transformations.map(database.taskDao.getAll()) { it.asDomainModel() }
        else Transformations.map(database.taskDao.getTasksByTag(tag)) { it.asDomainModel() }
    }

    suspend fun refreshDataFromNetwork() {
        withContext(Dispatchers.IO) {
            val taskNetworkContainer = Network.retrofitService.getTasks()
            database.taskDao.clear()
            database.taskDao.insert(*taskNetworkContainer.asDatabaseModel())
        }
    }

    suspend fun create(task: Task) {
        withContext(Dispatchers.IO) {
            val request = task.asNetworkModel().asRequest()
            val taskNetworkResponse = Network.retrofitService.createTask(request)
            database.taskDao.insert(taskNetworkResponse.asDatabaseModel())
        }
    }

    suspend fun update(id: String, task: Task) {
        withContext(Dispatchers.IO) {
            val request = task.asNetworkModel().asRequest()
            val taskNetworkResponse = Network.retrofitService.updateTask(id, request)
            database.taskDao.insert(taskNetworkResponse.asDatabaseModel())
        }
    }

    suspend fun delete(id: String) {
        withContext(Dispatchers.IO) {
            Network.retrofitService.deleteTask(id)
            database.taskDao.delete(id)
        }
    }

}