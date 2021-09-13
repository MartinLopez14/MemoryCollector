package com.example.memorycollector

import android.app.Application
import com.example.memorycollector.models.MemoryDatabase
import com.example.memorycollector.models.MemoryRepository

class MemoryCollectorApplication : Application() {
    val database by lazy { MemoryDatabase.getDatabase(this) }
    val repository by lazy {
        MemoryRepository(
            database.memoryDao()
        )
    }
}
