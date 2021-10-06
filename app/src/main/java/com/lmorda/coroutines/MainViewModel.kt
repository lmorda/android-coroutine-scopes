package com.lmorda.coroutines

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch

// ViewModels are ideal to launch coroutines with viewModelScope because the androidx.lifecycle
// library automatically cancels them when the activity/fragment is destroyed.
// Adding a ViewModel layer between legacy Java code and new Kotlin code is a great way to
// enable launching a coroutine, for example a Java Activity/Fragment calling a coroutine
// in a Kotlin repository or service layer.
class MainViewModel: ViewModel() {

    private val _readComplete = MutableLiveData<Boolean>()
    var readCompleteLiveData: LiveData<Boolean> = _readComplete

    private var readComplete: Boolean = false


    fun readFile(context: Context, lifecycleOwner: LifecycleOwner) {
        val diskService = MockDiskService(context)

        // launch is a coroutine builder that dispatches onto the main thread
        viewModelScope.launch {

            // This would block the main thread since viewModelScope dispatches onto the main thread
            // and this is not a suspending function!
            // diskService.longDiskOperation()

            // This does not block the main thread since we are calling the suspending function,
            // so even though launch dispatches onto the main thread this will not block the main thread
            _readComplete.postValue(diskService.suspendingLongDiskOperation())

        }
    }

    fun readFile(context: Context): LiveData<Boolean> {
        val diskService = MockDiskService(context)
        // liveData is a coroutine builder that dispatches onto the main thread
        return liveData {
            // Same as above, does not block main thread
            diskService.suspendingLongDiskOperation()
            emit(readComplete)
        }
    }
}