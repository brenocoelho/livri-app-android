package io.livri.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.livri.database.LivriDatabase
import io.livri.database.user.asDomainModel
import io.livri.domain.User
import io.livri.domain.asNetworkModel
import io.livri.network.Network
import io.livri.network.asDatabaseModel
import io.livri.network.asRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsersRepository(private val database: LivriDatabase) {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _user = MutableLiveData<User>()

    // The external immutable LiveData for the request status String
    val user: LiveData<User>
        get() = _user

    suspend fun getDataFromDatabase() {
        withContext(Dispatchers.IO) {
            var userDatabase = database.userDao.get()
            userDatabase?.let { _user.postValue(userDatabase.asDomainModel()) }
        }
    }

    suspend fun refreshDataFromNetwork() {
        withContext(Dispatchers.IO) {
            val userId : String = "17528b7f-7837-4e25-bb76-666678f51c98"

            val userNetworkResponse = Network.retrofitService.getUser(userId)
            database.userDao.insert(userNetworkResponse.asDatabaseModel())
        }
    }

}