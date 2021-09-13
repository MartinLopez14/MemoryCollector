package com.example.memorycollector.models

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MemoryViewModel(private val memoryRepository: MemoryRepository): ViewModel() {

    val memory: LiveData<List<Memory>> = memoryRepository.memories.asLiveData()
    val numMemories: LiveData<Int> = memoryRepository.numMemories.asLiveData()

    fun addMemory(memory: Memory) = viewModelScope.launch {
        memoryRepository.insertMemory(memory)
    }

    fun deleteMemory(memory: Memory) = viewModelScope.launch {
        memoryRepository.deleteMemory(memory)
    }

    class MemoryViewModelFactory(private val repository: MemoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MemoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MemoryViewModel(
                    repository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


