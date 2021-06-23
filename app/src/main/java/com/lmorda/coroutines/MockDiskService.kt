package com.lmorda.coroutines

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MockDiskService(private val appContext: Context) {

    private val _readComplete = MutableLiveData<Boolean>()
    var readComplete: LiveData<Boolean> = _readComplete

    suspend fun suspendingLongDiskOperation() {
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val read = longDiskOperation()
            _readComplete.postValue(read)
        }
    }

    private fun longDiskOperation(): Boolean {
        for (i in 1..200)
            appContext.resources.openRawResource(R.raw.file).bufferedReader().use { it.readText() }
        return true
    }

}