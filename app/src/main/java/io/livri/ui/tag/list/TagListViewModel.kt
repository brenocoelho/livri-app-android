package io.livri.ui.tag.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import io.livri.database.LivriDatabase
import io.livri.domain.Tag
import io.livri.network.Network
import io.livri.repository.TagsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.collections.ArrayList

enum class TagListApiStatus { LOADING, ERROR, DONE }

class TagListViewModel(application: Application) : AndroidViewModel(application) {

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    private val database = LivriDatabase.getInstance(application)
    private val tagsRepository = TagsRepository(database)

    val tags = tagsRepository.tags

    private val _status = MutableLiveData<TagListApiStatus>()

    val status: LiveData<TagListApiStatus>
        get() = _status

    private val _navigateToSelectedTag = MutableLiveData<Tag>()

    val navigateToSelectedTag: LiveData<Tag>
        get() = _navigateToSelectedTag

    /**
     * Call loadTags() on init so we can display status immediately.
     */
    init {

    }

    fun refreshTags() {

        coroutineScope.launch {
            try {
                _status.value = TagListApiStatus.LOADING

                tagsRepository.refreshDataFromNetwork()

                _status.value = TagListApiStatus.DONE
            } catch (e: Exception) {
                _status.value = TagListApiStatus.ERROR
            }

        }
    }

    fun deleteTask(tag: Tag) {
        coroutineScope.launch {
            try {
                tagsRepository.delete(tag.id)
//                tagsRepository.refreshDataFromNetwork()
//                tagsRepository.getDataFromDatabase()
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
     * When the property is clicked, set the [_navigateToSelectedTag] [MutableLiveData]
     * @param tag The [Tag] that was clicked on.
     */
    fun displayTagDetails(tag: Tag) {
        _navigateToSelectedTag.value = tag
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedTag is set to null
     */
    fun displayTagDetailsComplete() {
        _navigateToSelectedTag.value = null
    }

    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TagListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TagListViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
