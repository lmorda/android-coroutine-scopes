package com.lmorda.coroutines

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val diskService = MockDiskService(application)

    val readComplete = Transformations.switchMap(diskService.readComplete) {
        complete ->
            liveData {
                emit(complete)
            }
        }

    fun readFile() {
        viewModelScope.launch {
            diskService.suspendingLongDiskOperation()
        }
    }

}