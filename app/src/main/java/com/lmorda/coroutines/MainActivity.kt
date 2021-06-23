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
        btn_viewmodel_scope.setOnClickListener {
            showToast("viewModelScope.launch")
            viewModel.readFile()
        }
        viewModel.readComplete.observe(this, {
            showToast("End of file read")
        })
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