package com.example.myjournalappfinal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myjournalappfinal.Auth.LoginActivity
import com.example.myjournalappfinal.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserData()
        setupListeners()
    }

    private fun setupUserData() {
        val user = auth.currentUser
        if (user != null) {
            // ✅ CHANGED: Set the text for the new tvUserName view
            binding?.tvUserName?.text = user.displayName?.takeIf { it.isNotBlank() } ?: "Journalist"
            binding?.tvUserEmail?.text = user.email

            val creationTimestamp = user.metadata?.creationTimestamp
            if (creationTimestamp != null) {
                val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                binding?.tvMemberSince?.text = "Member since ${sdf.format(Date(creationTimestamp))}"
            }
        }
    }

    private fun setupListeners() {
        binding?.btnLogout?.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        // ✅ ADDED: Listener to open the change name dialog
        binding?.txtChangeName?.setOnClickListener {
            showChangeNameDialog()
        }

        // ✅ ADDED: Listener to open the change password dialog
        binding?.txtChangePassword?.setOnClickListener {
            showChangePasswordConfirmationDialog()
        }
    }

    // --- ✅ NEW FUNCTIONS FOR NEW FEATURES ---

    private fun showChangeNameDialog() {
        val user = auth.currentUser ?: return
        val editText = EditText(requireContext()).apply {
            setText(user.displayName)
            hint = "Enter your new name"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Change Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    updateUserName(newName)
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUserName(newName: String) {
        val user = auth.currentUser ?: return
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Name updated successfully.", Toast.LENGTH_SHORT).show()
                    // Refresh the UI with the new name
                    binding?.tvUserName?.text = newName
                } else {
                    Toast.makeText(requireContext(), "Failed to update name: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showChangePasswordConfirmationDialog() {
        val userEmail = auth.currentUser?.email
        if (userEmail == null) {
            Toast.makeText(requireContext(), "Could not find user email.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setMessage("A password reset link will be sent to your email address:\n\n$userEmail")
            .setPositiveButton("Send Email") { _, _ ->
                sendPasswordResetEmail(userEmail)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to send email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}