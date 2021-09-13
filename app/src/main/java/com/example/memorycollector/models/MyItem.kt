package com.example.memorycollector.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyItem(
        lat: Double,
        lng: Double,
        title: String,
        memory: Memory
) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val memory: Memory

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return " "
    }

    fun getMemory() : Memory {
        return memory
    }

    init {
        position = LatLng(lat, lng)
        this.title = title
        this.memory = memory
    }
}