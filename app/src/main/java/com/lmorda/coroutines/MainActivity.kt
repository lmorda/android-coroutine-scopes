package com.lmorda.coroutines

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mockDiskService = MockDiskService(this)

        btn_global_scope.setOnClickListener {
            showToast("GlobalScope.launch")
            // Does not block main thread since this is dispatched on background thread
            GlobalScope.launch {
                // Potential memory leak since coroutine won't be cancelled
                mockDiskService.longDiskOperation()
                // Crashes because show toast is called on non-main thread!
                showToast("End of file read")
            }
        }

        btn_coroutine_scope_io_dispatcher.setOnClickListener {
            showToast("CoroutineScope(Dispatchers.IO).launch")
            // Does not block main thread since this is dispatched on background thread
            CoroutineScope(Dispatchers.IO).launch {
                // Potential memory leak since coroutine won't be cancelled
                mockDiskService.longDiskOperation()
                // Crashes because show toast is called on non-main thread!
                showToast("End of file read")
            }
        }

        btn_lifecycle_scope_blocking.setOnClickListener {
            showToast("lifecycleScope.launch")
            // Blocks main thread because lifecycle scope dispatches onto main thread!
            lifecycleScope.launch {
                // Not a memory leak, lifecycleScope handles cancel for us
                mockDiskService.longDiskOperation()
                // Does not crash app since dispatched on main thread
                showToast("End of file read")
            }
        }

        btn_lifecycle_scope.setOnClickListener {
            showToast("lifecycleScope.launch")
            // Does not block main thread because the service's function is suspending
            lifecycleScope.launch {
                mockDiskService.suspendingLongDiskOperation()
                // Does not crash app since dispatched on main thread
                showToast("End of file read")
            }
        }

        btn_viewmodel_scope.setOnClickListener {
            showToast("viewModelScope.launch")
            // Will not block main thread since the ViewModel calls the service's suspending function
            viewModel.readFile(this, this)
        }
        viewModel.readCompleteLiveData.observe(this, {
            // Does not crash app since this is lifecycle aware and runs on main thread
            showToast("End of file read")
        })

        btn_livedata_scope.setOnClickListener {
            showToast("liveData + emit")
            // Will not block main thread since the ViewModel calls the service's suspending function
            viewModel.readFile(this).observe(this, {
                // Does not crash app since this is lifecycle aware and runs on main thread
                showToast("End of file read")
            })
        }
    }

    private fun showToast(message: String) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        } catch (ex: Exception) {
            lifecycleScope.launch {
                Toast.makeText(applicationContext, ex.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}