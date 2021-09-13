package com.example.memorycollector.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.memorycollector.MemoryCollectorApplication
import com.example.memorycollector.R
import com.example.memorycollector.models.Memory
import com.example.memorycollector.models.MemoryViewModel
import com.example.memorycollector.models.MyItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import java.text.DateFormat
import java.util.*
import androidx.lifecycle.Observer

class ViewMemoryMapFragment : Fragment(), GoogleMap.OnMarkerClickListener, ClusterManager.OnClusterClickListener<MyItem>, ClusterManager.OnClusterItemClickListener<MyItem>, OnMapReadyCallback {


    lateinit var currentLocation : Location
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    val LOCATION_REQUEST_CODE = 101
    private val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    lateinit var mapFragment: SupportMapFragment
    private lateinit var clusterManager: ClusterManager<MyItem>
    
    private val model: MemoryViewModel by activityViewModels() {
        MemoryViewModel.MemoryViewModelFactory((activity?.application as MemoryCollectorApplication).repository)
    }

    private var memories: List<Memory>? = null


    override fun onMapReady(googleMap: GoogleMap) {
        var currLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        //googleMap.animateCamera(CameraUpdateFactory.newLatLng(currLatLng))
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, 5.0F))

//        if (model.memory.value != null) {
//            for (memory in model.memory.value!!) {
//                val memoryLatLng = LatLng(memory.latitude, memory.longitude)
//                googleMap.addMarker(MarkerOptions().position(memoryLatLng).title(memory.name))
//            }
//        }

        setUpClusterer(googleMap)

    }

    private fun setUpClusterer(map: GoogleMap) {
        // Position the map.
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 10f)
        )

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(context, map)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.setOnClusterClickListener(this)

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        // Add cluster items (markers) to the cluster manager.
        addItems()
    }

    private fun addItems() {

        // Set some lat/lng coordinates to start with.
        var lat = currentLocation.latitude
        var lng = currentLocation.longitude

        var date = Date()

        // Add ten cluster items in close proximity, for purposes of this example.
        for (i in 0..9) {
            val offset = i / 60.0
            lat += offset
            lng += offset
            val offsetItem =
                    MyItem(lat, lng, "Title $i", Memory("name", "desc", date, lat, lng, "mediaString"))
            clusterManager.addItem(offsetItem)
        }

        if (memories != null) {
            for (memory in memories!!) {
                val item = MyItem(memory.latitude, memory.longitude, memory.name, memory)
                clusterManager.addItem(item)
            }
        }

    }

//    private val callback = OnMapReadyCallback { googleMap ->
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//        val sydney = LatLng(-34.0, 151.0)
//        val christchurch = LatLng(-43.532, 172.6306)
//
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.addMarker(MarkerOptions().position(christchurch).title("Marker in Christchurch"))
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(christchurch))
//
//        val centerOfMap = googleMap.projection.visibleRegion.latLngBounds.center
//
//        googleMap.setOnMarkerClickListener(this)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )
        fetchLastLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.fragment_view_memory_map, container, false)
    }

    private fun fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                LOCATION_PERMISSIONS,
                LOCATION_REQUEST_CODE
            )

            return
        }
        var task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLocation = location

                mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.size > 0 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation()
            }
        fetchLastLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        model.memory.observe(
                viewLifecycleOwner,
                Observer<List<Memory>> { memoryList ->
                    memories = memoryList

                }
        )   
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // here we need to show the memroy info so title desc and date and image somehow
        // returns false so it does default behaviour also like centre map and open info window

        Toast.makeText(requireContext(), "marker.title", Toast.LENGTH_LONG).show()
        return false
    }

    override fun onClusterClick(cluster: Cluster<MyItem>?): Boolean {
        val memoryIds : MutableList<Int> = mutableListOf()
        for (item in cluster!!.items) {
            memoryIds.add(model.memory.value?.indexOf(item?.getMemory())!!)
        }

        val action = ViewMemoryMapFragmentDirections.actionViewMemoryMapFragmentToViewMemoryListFragment(memoryIds.toIntArray())
        this.view?.findNavController()?.navigate(action)

        return false
    }

    override fun onClusterItemClick(item: MyItem?): Boolean {
        val memoryPosition = model.memory.value?.indexOf(item?.getMemory())
        val action = ViewMemoryMapFragmentDirections.actionViewMemoryMapFragmentToViewMemoryFragment(memoryPosition!!)
        this.view?.findNavController()?.navigate(action)
        return false
    }


}