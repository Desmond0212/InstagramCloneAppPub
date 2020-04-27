package com.example.instagramcloneapp

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.instagramcloneapp.Fragments.HomeFragment
import com.example.instagramcloneapp.Fragments.NotificationFragment
import com.example.instagramcloneapp.Fragments.ProfileFragment
import com.example.instagramcloneapp.Fragments.SearchFragment

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        routeToFragment(HomeFragment())
    }

    /*Desmond Stop: Finished EP5, Start EP6*/

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId)
        {
            R.id.nav_home ->
            {
                routeToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.nav_search ->
            {
                routeToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.nav_add_post ->
            {
                return@OnNavigationItemSelectedListener true
            }

            R.id.nav_notifications ->
            {
                routeToFragment(NotificationFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.nav_profile ->
            {
                routeToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    private fun routeToFragment(fragment: Fragment)
    {
        val routeFrag = supportFragmentManager.beginTransaction()
        routeFrag.replace(R.id.fragment_container, fragment)
        routeFrag.commit()
    }
}
