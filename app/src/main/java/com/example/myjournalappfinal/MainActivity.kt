package com.example.myjournalappfinal

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

        // 1. Enable edge-to-edge display for a modern, full-screen look.
        enableEdgeToEdge()

        // 2. Inflate the layout using View Binding and set the content view.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Initialize Firebase Authentication.
        auth = FirebaseAuth.getInstance()

        // 4. Set up window insets and navigation logic.
        setupWindowInsets()
        setupNavigation()
    }

    /**
     * Configures the window insets to prevent the BottomNavigationView from being
     * obscured by the system's navigation bar in edge-to-edge mode.
     */
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply the bottom system bar's height as padding to the bottom of the navigation view.
            binding.bottomNavigation.updatePadding(bottom = insets.bottom)

            // Return the insets so other views in the hierarchy can consume them.
            windowInsets
        }
    }

    /**
     * Initializes the NavController and connects it to the BottomNavigationView.
     * Also controls the visibility of the bottom navigation bar based on the current fragment.
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // ✅ MODIFIED: Replaced `setupWithNavController` with your requested manual listener.
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // NOTE: The R.id names here must match your `bottom_menu.xml`
                R.id.bottom_home ->
                    navController.navigate(R.id.homeFragment)

                R.id.bottom_insight ->
                    navController.navigate(R.id.insightFragment)

                R.id.bottom_jouranals -> // Make sure this ID is correct in your menu file
                    navController.navigate(R.id.allJournal)

                R.id.bottom_profile ->
                    navController.navigate(R.id.profileFragment)
            }
            true // Return true to display the item as the selected item
        }

        // Add a listener to show or hide the bottom navigation bar on specific fragments.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // List all top-level fragments where the bar should be visible
                R.id.homeFragment,
                R.id.allJournal,
                R.id.insightFragment,
                R.id.profileFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                // Hide the bar on all other fragments (e.g., detail, login, settings screens)
                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Ensures the Up button in the action bar properly navigates back in the NavController's back stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

