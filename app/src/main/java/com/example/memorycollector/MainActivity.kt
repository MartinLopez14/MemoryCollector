package com.example.memorycollector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.memorycollector.screens.CameraFragment
import com.example.memorycollector.screens.ViewMemoryListFragment
import com.example.memorycollector.screens.ViewMemoryMapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toolbar: ActionBar
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomNavigation.menu.getItem(1).isChecked = true
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_map -> {
                navController.navigate(R.id.viewMemoryMapFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_camera -> {
                navController.navigate(R.id.cameraFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                navController.navigate(R.id.viewMemoryListFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}