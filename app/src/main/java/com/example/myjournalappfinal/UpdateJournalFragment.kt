package com.example.myjournalappfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myjournalappfinal.databinding.FragmentUpdateJournalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateJournalFragment : Fragment() {

    private var binding: FragmentUpdateJournalBinding? = null
    private val args: UpdateJournalFragmentArgs by navArgs()
    private lateinit var journalEntry: JournalEntry

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateJournalBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        journalEntry = args.journalEntry

        // This function enables the edge-to-edge display
        handleWindowInsets()

        setupUI()
        setupListeners()
    }

    /**
     * Applies window insets to handle the system bars (status bar, navigation bar)
     * for a seamless, full-screen edge-to-edge display.
     */
    private fun handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply the insets as padding to the root view. This will push the content
            // away from the system bars, preventing them from overlapping.
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom)

            // Return the insets so they are consumed and not applied to child views
            windowInsets
        }
    }

    private fun setupUI() {
        binding?.etUpdateTitle?.setText(journalEntry.title)
        binding?.etUpdateStory?.setText(journalEntry.storyContent)

        // Load images using Glide
        Glide.with(this).load(journalEntry.imageUrl1).placeholder(R.drawable.baseline_add_24)
            .into(binding!!.ivUpdateImage1)
        Glide.with(this).load(journalEntry.imageUrl2).placeholder(R.drawable.baseline_add_24)
            .into(binding!!.ivUpdateImage2)
    }

    private fun setupListeners() {
        binding?.etUpdateTitle?.setOnClickListener {
            // Use findNavController to go back
            findNavController().popBackStack()
        }

        binding?.btnUpdate?.setOnClickListener {
            updateJournalEntry()
        }

        // TODO: Add logic to re-upload images if the user taps them
    }

    private fun updateJournalEntry() {
        val newTitle = binding?.etUpdateTitle?.text.toString().trim()
        val newStory = binding?.etUpdateStory?.text.toString().trim()

        if (newTitle.isEmpty() || newStory.isEmpty()) {
            Toast.makeText(requireContext(), "Title and story cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null || journalEntry.id.isEmpty()) {
            Toast.makeText(requireContext(), "Error updating entry. User not found.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a map of the fields to update
        val updates = mapOf(
            "title" to newTitle,
            "storyContent" to newStory
            // You can add "imageUrl1" and "imageUrl2" here if you implement re-uploading
        )

        FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("journals").document(journalEntry.id)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Entry updated successfully!", Toast.LENGTH_SHORT).show()
                // Use findNavController to go back after success
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

