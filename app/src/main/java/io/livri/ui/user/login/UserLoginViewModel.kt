package io.livri.ui.user.login

import android.app.Application
import androidx.lifecycle.*
import io.livri.database.LivriDatabase
import io.livri.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

enum class UserLoginApiStatus { SAVING, ERROR, DONE }

class UserLoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LivriDatabase.getInstance(application)
    private val userRepository = UserRepository(database)

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _status = MutableLiveData<UserLoginApiStatus>()

    val status: LiveData<UserLoginApiStatus>
        get() = _status

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    // Initialize the _selectedTag MutableLiveData
    init {
//        _tag.value = Tag(
//            "",
//            "",
//            "#000000",
//            1
//        )
    }
//
//    fun setColor(color: String) {
//        var tag = _tag.value
//        tag?.color = color
//        _tag.value = tag
//    }

//    fun createTag() {
//        val tag = _tag.value
//        coroutineScope.launch {
//            try {
//                _status.value = UserLoginApiStatus.SAVING
//                tagsRepository.create(tag!!)
//                _status.value = UserLoginApiStatus.DONE
//            } catch (e: Exception) {
//                Timber.e(e.toString())
//                _status.value = UserLoginApiStatus.ERROR
//            }
//        }
//    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserLoginViewModel::class.java)) {
                return UserLoginViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
