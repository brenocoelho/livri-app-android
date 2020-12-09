package io.livri.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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

    val tags: LiveData<List<Tag>> =
        Transformations.map(database.tagDao.getAll()) {
            it.asDomainModel()
        }

    suspend fun refreshDataFromNetwork() {
        withContext(Dispatchers.IO) {
            val tagNetworkContainer = Network.retrofitService.getTags()
            database.tagDao.clear()
            database.tagDao.insert(*tagNetworkContainer.asDatabaseModel())
        }
    }

    suspend fun create(tag: Tag) {
        withContext(Dispatchers.IO) {
            val request = tag.asNetworkModel().asRequest()
            val tagNetworkResponse = Network.retrofitService.createTag(request)
            database.tagDao.insert(tagNetworkResponse.asDatabaseModel())
        }
    }

    suspend fun update(id: String, tag: Tag) {
        withContext(Dispatchers.IO) {
            val request = tag.asNetworkModel().asRequest()
            val tagNetworkResponse = Network.retrofitService.updateTag(id, request)
            database.tagDao.insert(tagNetworkResponse.asDatabaseModel())
        }
    }

    suspend fun delete(id: String) {
        withContext(Dispatchers.IO) {
            Network.retrofitService.deleteTag(id)
            database.tagDao.delete(id)
        }
    }

}