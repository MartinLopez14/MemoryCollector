package com.example.memorycollector.models

import androidx.room.ColumnInfo
import androidx.room.Entity

import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "memory")
class Memory (
    @ColumnInfo var name: String,
    @ColumnInfo var description: String,
    @ColumnInfo var date: Date,
    @ColumnInfo var latitude: Double,
    @ColumnInfo var longitude: Double,
    @ColumnInfo var mediaString: String) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    override fun toString() = name
}
