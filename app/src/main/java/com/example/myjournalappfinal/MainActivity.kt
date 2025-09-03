package com.example.myjournalappfinal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myjournalappfinal.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding?.logoutButton?.setOnClickListener {
//            auth.signOut() // logout from Firebase
//            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
//            // Redirect to Login screen after logout
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish() // close MainActivity so user can’t go back
//        }

        val navHostFragment = binding?.fragmentContainerView?.id?.let {
            supportFragmentManager.findFragmentById(
                it
            )
        } as NavHostFragment
        navController = navHostFragment.navController
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
//                R.id.registerFragment,
                R.id.profileFragment,
//                R.id.journalsFragment,
                -> binding?.bottomNavigation?.visibility = View.VISIBLE
                else -> binding?.bottomNavigation?.visibility = View.GONE
            }
        }

        binding?.bottomNavigation?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home ->
                    navController.navigate(R.id.homeFragment)


                R.id.bottom_insight ->
                    navController.navigate(R.id.insightFragment)
//
//                R.id.bottom_jouranals ->
//                    navController.navigate(R.id.journalsFragment)
//
                R.id.bottom_profile ->
                    navController.navigate(R.id.profileFragment)

            }
            true
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
//                R.id.journalsFragment,
                R.id.insightFragment,
                R.id.profileFragment
                    -> {
                    binding?.bottomNavigation?.visibility = View.VISIBLE
                }
                else -> {
                    binding?.bottomNavigation?.visibility = View.GONE
                }
            }

        }
    }






    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}