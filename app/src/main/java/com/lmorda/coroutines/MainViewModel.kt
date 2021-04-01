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
    var readComplete: LiveData<Boolean> = _readComplete

    fun readFile(context: Context, lifecycleOwner: LifecycleOwner) {
        val diskService = MockDiskService(context)

        // launch is a coroutine builder that runs on the main thread
        viewModelScope.launch {

            // This would block the main thread since viewModelScope runs on the main thread!
            // diskService.longDiskOperation()

            // This does not block the main thread since we are calling the suspending function,
            // so even though launch runs on the main thread this will not block the main thread
            diskService.suspendingLongDiskOperation()

        }
        diskService.readComplete.observe(lifecycleOwner, Observer {
            _readComplete.postValue(it)
        })
    }
}