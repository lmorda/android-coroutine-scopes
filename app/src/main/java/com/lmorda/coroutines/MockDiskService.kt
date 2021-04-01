package com.lmorda.coroutines

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MockDiskService(
    private val appContext: Context
) {

    private val _readComplete = MutableLiveData<Boolean>()
    var readComplete: LiveData<Boolean> = _readComplete

    fun longDiskOperation(): Boolean {
        for (i in 1..200)
            appContext.resources.openRawResource(R.raw.file).bufferedReader().use { it.readText() }
        return true
    }

    // Note: Suspend functions should be main thread safe
    // https://developer.android.com/kotlin/coroutines/coroutines-best-practices
    // https://play.kotlinlang.org/hands-on/Introduction%20to%20Coroutines%20and%20Channels/04_Suspend
    suspend fun suspendingLongDiskOperation() {
        // withContext is a coroutine builder that is basically an async call followed by an await
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val read = longDiskOperation()
            _readComplete.postValue(read)
        }
    }
}