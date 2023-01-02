package com.example.kotlinflow

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.kotlinflow.databinding.FragmentMyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MyFragment : Fragment(R.layout.fragment_my) {

    private var binding: FragmentMyBinding? = null

    private val viewModel: MyViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyBinding.bind(view)

        lifecycleScope.launch {
            flow {
                for (i in 0..20) {
                    delay(500)
                    emit(i)
                }
            }.collect {
                delay(1000)
                Log.e("Flow_test", "unbuffered flow $it")
            }
        }

        lifecycleScope.launch {
            flow {
                for (i in 0..20) {
                    delay(500)
                    emit(i)
                }
            }.buffer(2).collect {
                delay(1000)
                Log.e("Flow_test", "buffered flow $it")
            }
        }

        viewModel.liveData.observe(viewLifecycleOwner) {
            Log.e("Flow_test", "liveData $it")
        }

        lifecycleScope.launch {
            viewModel.sharedFlow.collect {
                Log.e("Flow_test", "launch sharedFlow $it")
//                    if (it == 20) {
//                        Toast.makeText(requireContext(), "sharedFlow", Toast.LENGTH_SHORT).show()
//                    }
            }
        }

        lifecycleScope.launch {
            viewModel.stateFlow.collect {
                Log.e("Flow_test", "launch stateFlow $it")
//                    if (it == 20) {
//                        Toast.makeText(requireContext(), "stateFlow", Toast.LENGTH_SHORT).show()
//                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.sharedFlow.collect {
                Log.e("Flow_test", "launchWhenStarted sharedFlow $it")
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect {
                Log.e("Flow_test", "launchWhenStarted stateFlow $it")
            }
        }

        lifecycleScope.launch {
            viewModel.sharedFlow.onEach {
                Log.e("Flow_test", "onEach sharedFlow $it")
            }.stateIn(lifecycleScope)
        }

        lifecycleScope.launch {
            viewModel.stateFlow.onEach {
                Log.e("Flow_test", "onEach stateFlow $it")
                if (it == 5) {
                    throw Exception()
                }
            }.catch {
                Log.e("Flow_test", "catch block")
            }.stateIn(lifecycleScope)
        }

        viewModel.sharedFlow.filter {
            Log.e("Flow_test", "filter sharedFlow $it")
            true
        }

        viewModel.stateFlow.filter {
            Log.e("Flow_test", "filter stateFlow $it")
            true
        }

        lifecycleScope.launchWhenStarted {
            viewModel.hybridStateFlow.collect {
                Log.e("Flow_test", "launchWhenStarted  hybridStateFlow from viewModel $it")
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.sharedFlow.stateIn(
                lifecycleScope + Dispatchers.IO,
                SharingStarted.Lazily,
                -1
            ).collect {
                Log.e("Flow_test", "launchWhenStarted hybridStateFlow from fragment $it")
            }
        }

        lifecycleScope.launch {
            channelFlow {
                for (i in 0..20) {
                    delay(500)
                    send(i)
                    if (i == 5) {
                        awaitClose()
                    }
                }
            }.collect {
                delay(1000)
                Log.e("Flow_test", "channelFlow $it")
            }
        }

        lifecycleScope.launch {
            callbackFlow {
                for (i in 0..20) {
                    delay(500)
                    send(i)
                    if (i == 5) {
                        awaitClose()
                    }
                }
            }.collect {
                delay(1000)
                Log.e("Flow_test", "callbackFlow $it")
            }
        }

        lifecycleScope.launch {
            viewModel.sharedFlow.flowWithLifecycle(lifecycle).collect {
                Log.e("Flow_test", "flowWithLifecycle $it")
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
