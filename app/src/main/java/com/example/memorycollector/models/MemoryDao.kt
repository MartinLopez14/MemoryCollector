package com.example.memorycollector.models

import androidx.room.*
import com.example.memorycollector.models.Memory
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: Memory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(memories: List<Memory>): List<Long>

    @Update
    suspend fun update(memory: Memory)

    @Delete
    suspend fun delete(memory: Memory)

    @Query("SELECT * FROM memory")
    fun getAll(): Flow<List<Memory>>

    @Query("SELECT COUNT(*) FROM memory")
    fun getCount(): Flow<Int>

    //Might need a seperate function for CRUD of images if they are stored seperately from the Room database.
}