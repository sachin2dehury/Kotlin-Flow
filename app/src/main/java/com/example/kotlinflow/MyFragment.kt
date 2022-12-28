package com.example.kotlinflow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kotlinflow.databinding.FragmentMyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MyFragment : Fragment(R.layout.fragment_my) {

    private var binding: FragmentMyBinding? = null

    private val viewModel: MyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyBinding.bind(view)

        binding?.run {
            viewModel.liveData.observe(viewLifecycleOwner) {
                Log.e("Flow_test", "liveData $it")
            }

            lifecycleScope.launch {
                viewModel.sharedFlow.collectLatest {
                    Log.e("Flow_test", "launch sharedFlow $it")
//                    if (it == 20) {
//                        Toast.makeText(requireContext(), "sharedFlow", Toast.LENGTH_SHORT).show()
//                    }
                }
            }

            lifecycleScope.launch {
                viewModel.stateFlow.collectLatest {
                    Log.e("Flow_test", "launch stateFlow $it")
//                    if (it == 20) {
//                        Toast.makeText(requireContext(), "stateFlow", Toast.LENGTH_SHORT).show()
//                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                viewModel.sharedFlow.collectLatest {
                    Log.e("Flow_test", "launchWhenStarted sharedFlow $it")
                }
            }

            lifecycleScope.launchWhenStarted {
                viewModel.stateFlow.collectLatest {
                    Log.e("Flow_test", "launchWhenStarted stateFlow $it")
                }
            }

            viewModel.sharedFlow.onEach {
                Log.e("Flow_test", "onEach sharedFlow $it")
            }

            viewModel.stateFlow.onEach {
                Log.e("Flow_test", "onEach stateFlow $it")
            }

            viewModel.sharedFlow.filter {
                Log.e("Flow_test", "onEach sharedFlow $it")
                true
            }

            viewModel.stateFlow.filter {
                Log.e("Flow_test", "onEach stateFlow $it")
                true
            }

            lifecycleScope.launchWhenStarted {
                viewModel.hybridStateFlow.collectLatest {
                    Log.e("Flow_test", "launchWhenStarted  hybridStateFlow from viewModel $it")
                }
            }

            lifecycleScope.launchWhenStarted {
                viewModel.sharedFlow.stateIn(lifecycleScope + Dispatchers.IO, SharingStarted.Lazily, -1).collectLatest {
                    Log.e("Flow_test", "launchWhenStarted hybridStateFlow from fragment $it")
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
