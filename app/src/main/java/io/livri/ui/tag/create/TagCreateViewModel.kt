package io.livri.ui.tag.create

import android.app.Application
import androidx.lifecycle.*
import io.livri.database.LivriDatabase
import io.livri.domain.Tag
import io.livri.repository.TagsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

enum class TagCreateApiStatus { SAVING, ERROR, DONE }

class TagCreateViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LivriDatabase.getInstance(application)
    private val tagsRepository = TagsRepository(database)

    private val _tag = MutableLiveData<Tag>()

    // The external LiveData for the tag
    val tag: LiveData<Tag>
        get() = _tag

    private val _status = MutableLiveData<TagCreateApiStatus>()

    val status: LiveData<TagCreateApiStatus>
        get() = _status

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    // Initialize the _selectedTag MutableLiveData
    init {
        _tag.value = Tag(
            "",
            "",
            "#000000",
            1
        )
    }

    fun setColor(color: String) {
        var tag = _tag.value
        tag?.color = color
        _tag.value = tag
    }

    fun createTag() {
        val tag = _tag.value
        coroutineScope.launch {
            try {
                _status.value = TagCreateApiStatus.SAVING
                tagsRepository.create(tag!!)
                _status.value = TagCreateApiStatus.DONE
            } catch (e: Exception) {
                Timber.e(e.toString())
                _status.value = TagCreateApiStatus.ERROR
            }
        }
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TagCreateViewModel::class.java)) {
                return TagCreateViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
