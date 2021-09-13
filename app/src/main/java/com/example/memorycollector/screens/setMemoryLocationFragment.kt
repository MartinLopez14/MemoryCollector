package com.example.memorycollector.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.memorycollector.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [setMemoryLocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class setMemoryLocationFragment : Fragment(), OnMapReadyCallback {

    lateinit var currentLocation: Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val LOCATION_REQUEST_CODE = 101
    private val LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    lateinit var mapFragment: SupportMapFragment
    lateinit var markerLocation: LatLng
    lateinit var mediaOutputUri: String
    lateinit var name: String
    lateinit var description: String
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var dateString: String
    lateinit var timeString: String


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
//
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

    override fun onMapReady(googleMap: GoogleMap) {
        var currLatLng: LatLng
        currLatLng = if (this::markerLocation.isInitialized) {
            LatLng(markerLocation.latitude, markerLocation.longitude)
        } else {
            LatLng(currentLocation.latitude, currentLocation.longitude)
        }
        if (googleMap == null) {
            Log.d("TAG", "map is null when OnMapReady is called")
        }

        googleMap.addMarker(MarkerOptions().position(currLatLng).title("Current Location"))
//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currLatLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, 5.0F))


        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            markerLocation = LatLng(latLng.latitude, latLng.longitude)
            googleMap.addMarker(MarkerOptions().position(markerLocation).title(markerLocation.toString()))
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        loadFragmentArguments()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchLastLocation()

        return inflater.inflate(R.layout.fragment_set_memory_location, container, false)
    }

    private fun loadFragmentArguments() {
        mediaOutputUri = arguments?.getString("mediaOutputUri")!!
        name = arguments?.getString("name")!!
        description = arguments?.getString("description")!!

        latitude = arguments?.getString("latitude")!!
        longitude = arguments?.getString("longitude")!!
        if (latitude != "" && longitude != "") {
            markerLocation = LatLng(latitude.toDouble(), longitude.toDouble())
        }

        dateString = arguments?.getString("dateString")!!
        timeString = arguments?.getString("timeString")!!
    }


    private fun fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), LOCATION_PERMISSIONS, LOCATION_REQUEST_CODE)

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.size > 0 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
            fetchLastLocation()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.saveLocation)?.setOnClickListener {
            val mediaOutputUri = arguments?.getString("mediaOutputUri")!!
            val name = arguments?.getString("name")!!
            val description = arguments?.getString("description")!!

            var latitude: String
            var longitude: String
            if (this::markerLocation.isInitialized) {
                latitude = markerLocation.latitude.toString()
                longitude = markerLocation.longitude.toString()
            } else {
                latitude = currentLocation.latitude.toString()
                longitude = currentLocation.longitude.toString()
            }

            val action = setMemoryLocationFragmentDirections.actionSetMemoryLocationFragmentToCreateMemoryFragment(
                    mediaOutputUri, name, description, latitude, longitude, timeString, dateString
            )
            view.findNavController()
                    .navigate(action)
        }

    }


}