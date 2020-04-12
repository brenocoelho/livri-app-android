package io.livri.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.livri.database.LivriDatabase
import io.livri.database.tag.asDomainModel
import io.livri.domain.Tag
import io.livri.domain.asNetworkModel
import io.livri.network.Network
import io.livri.network.asDatabaseModel
import io.livri.network.asRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TagsRepository(private val database: LivriDatabase) {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _tags = MutableLiveData<List<Tag>>()

    // The external immutable LiveData for the request status String
    val tags: LiveData<List<Tag>>
        get() = _tags

    suspend fun getDataFromDatabase() {
        withContext(Dispatchers.IO) {
            _tags.postValue(database.tagDao.getAll().asDomainModel())
        }
    }

    suspend fun refreshDataFromNetwork() {
        withContext(Dispatchers.IO) {
            val tagNetworkContainer = Network.retrofitService.getTags()
            database.tagDao.clear()
            database.tagDao.insertAll(*tagNetworkContainer.asDatabaseModel())
        }
    }

    suspend fun create(tag: Tag) {
        withContext(Dispatchers.IO) {
            val request = tag.asNetworkModel().asRequest()
            Network.retrofitService.createTag(request)
        }
    }

    suspend fun update(id: String, tag: Tag) {
        withContext(Dispatchers.IO) {
            val request = tag.asNetworkModel().asRequest()
            Network.retrofitService.updateTag(id, request)
        }
    }

    suspend fun delete(id: String) {
        withContext(Dispatchers.IO) {
            Network.retrofitService.deleteTag(id)
            database.tagDao.delete(id)
        }
    }

}