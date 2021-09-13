package com.example.memorycollector.models

import androidx.annotation.WorkerThread
import com.example.memorycollector.models.Memory
import com.example.memorycollector.models.MemoryDao
import kotlinx.coroutines.flow.Flow

class MemoryRepository(private val memoryDao: MemoryDao) {
    val memories: Flow<List<Memory>> = memoryDao.getAll()

    val numMemories: Flow<Int> = memoryDao.getCount()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertMemory(memory: Memory): Long {
        return memoryDao.insert(memory)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(memories: List<Memory>): List<Long> {
        return memoryDao.insertAll(memories)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteMemory(memory: Memory) {
        memoryDao.delete(memory)
    }
}