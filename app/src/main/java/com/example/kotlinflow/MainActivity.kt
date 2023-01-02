package com.example.kotlinflow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            viewModel.sharedFlow.collect {
//                Log.e("Flow_test", "launch sharedFlow $it activity")
            }
        }

        lifecycleScope.launch {
            viewModel.stateFlow.collect {
//                Log.e("Flow_test", "launch stateFlow $it activity")
            }
        }
    }
}
