package com.example.myjournalappfinal

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myjournalappfinal.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        handleWindowInsets()
        setupUserData()
        fetchUserStats()
        setupListeners()
    }

//    private fun handleWindowInsets() {
//        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { _, windowInsets ->
//            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//            // Apply top padding to the collapsing toolbar to account for the status bar
////            binding?.appBarLayout?.updatePadding(top = insets.top)
//            // Apply bottom padding to the scroll view to account for the navigation bar
//            binding?.nestedScrollView?.updatePadding(bottom = insets.bottom)
//            WindowInsetsCompat.CONSUMED
//        }
//    }

    private fun setupUserData() {
        val user = auth.currentUser
        if (user != null) {
            binding?.tvUserName?.text = user.displayName ?: "Journalist"
            binding?.tvUserEmail?.text = user.email

            // Set "Member Since" text by formatting the user's creation timestamp
            val creationTimestamp = user.metadata?.creationTimestamp
            if (creationTimestamp != null) {
                val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                binding?.tvMemberSince?.text = "Member since ${sdf.format(Date(creationTimestamp))}"
            }

            Glide.with(this)
                .load(user.photoUrl)
                .placeholder(R.drawable.profile_circle_svgrepo_com) // A default avatar
                .into(binding!!.ivProfilePicture)
        }
    }

    private fun fetchUserStats() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            binding?.tvJournalCount?.text = "0"
            binding?.tvDaysStreak?.text = "0" // Default streak
            return
        }

        db.collection("users").document(userId)
            .collection("journals")
            .get()
            .addOnSuccessListener { documents ->
                binding?.tvJournalCount?.text = documents.size().toString()
                // You can add more complex logic here for day streaks, etc.
                binding?.tvDaysStreak?.text = "3" // Placeholder for streak
            }
            .addOnFailureListener {
                binding?.tvJournalCount?.text = "N/A"
                binding?.tvDaysStreak?.text = "N/A"
            }
    }

    private fun setupListeners() {
        // --- LOGOUT BUTTON ---
        binding?.btnLogout?.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            // Navigate to login screen and clear back stack
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        // --- NOTIFICATIONS ---
        binding?.tvNotifications?.setOnClickListener {
            Toast.makeText(requireContext(), "Notification settings coming soon!", Toast.LENGTH_SHORT).show()
        }

        // --- THEME SWITCHER LOGIC ---
        // Set initial state of the switch based on current app theme
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> binding?.switchDarkMode?.isChecked = true
            Configuration.UI_MODE_NIGHT_NO -> binding?.switchDarkMode?.isChecked = false
            else -> binding?.switchDarkMode?.isChecked = false // Default to light if undefined
        }

        // Add a listener to handle theme changes when the switch is toggled
        binding?.switchDarkMode?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch to Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Switch to Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

