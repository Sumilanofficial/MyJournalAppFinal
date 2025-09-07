package com.example.myjournalappfinal

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.myjournalappfinal.Models.JournalEntry
import com.example.myjournalappfinal.databinding.FragmentUpdateJournalBinding
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UpdateJournalFragment : Fragment() {

    private var binding: FragmentUpdateJournalBinding? = null
    private val args: UpdateJournalFragmentArgs by navArgs()
    private lateinit var journalEntry: JournalEntry

    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-1.5-flash")

    private var newImageUrl1: String? = null
    private var newImageUrl2: String? = null

    private val imagePickerLauncher1 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleImageSelection(it, binding!!.ivUpdateImage1, 1) }
    }
    private val imagePickerLauncher2 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleImageSelection(it, binding!!.ivUpdateImage2, 2) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUpdateJournalBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        journalEntry = args.journalEntry
        newImageUrl1 = journalEntry.imageUrl1
        newImageUrl2 = journalEntry.imageUrl2

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding?.etUpdateTitle?.setText(journalEntry.title)
        binding?.etUpdateStory?.setText(journalEntry.storyContent)
        Glide.with(this).load(journalEntry.imageUrl1).placeholder(R.drawable.baseline_add_24).into(binding!!.ivUpdateImage1)
        Glide.with(this).load(journalEntry.imageUrl2).placeholder(R.drawable.baseline_add_24).into(binding!!.ivUpdateImage2)
    }

    private fun setupListeners() {
        // ✅ REMOVED: All listeners for the toolbar are gone.

        binding?.btnUpdate?.setOnClickListener { updateJournalEntry() }

        // ✅ ADDED: Click listener for the new delete button from the layout.
        binding?.btnDelete?.setOnClickListener { showDeleteConfirmationDialog() }

        // Listeners for image selection and regeneration
        binding?.ivUpdateImage1?.setOnClickListener { imagePickerLauncher1.launch("image/*") }
        binding?.ivUpdateImage2?.setOnClickListener { imagePickerLauncher2.launch("image/*") }
        binding?.btnRegenerateStory?.setOnClickListener { regenerateStory() }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this journal entry? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteJournalEntry() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteJournalEntry() {
        binding?.progressBar?.isVisible = true
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null || journalEntry.id.isEmpty()) return

        FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("journals").document(journalEntry.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Entry deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                binding?.progressBar?.isVisible = false
                Toast.makeText(requireContext(), "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleImageSelection(uri: Uri, imageView: ImageView, imageNumber: Int) {
        imageView.setImageURI(uri)
        uploadToCloudinary(uri, imageNumber)
    }

    private fun uploadToCloudinary(uri: Uri, imageNumber: Int) {
        binding?.progressBar?.isVisible = true
        MediaManager.get().upload(uri).callback(object : UploadCallback {
            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                binding?.progressBar?.isVisible = false
                val secureUrl = resultData["secure_url"] as? String
                if (imageNumber == 1) { newImageUrl1 = secureUrl } else { newImageUrl2 = secureUrl }
                Toast.makeText(requireContext(), "Photo $imageNumber updated", Toast.LENGTH_SHORT).show()
            }
            override fun onError(requestId: String, error: ErrorInfo) {
                binding?.progressBar?.isVisible = false
                Toast.makeText(requireContext(), "Upload failed: ${error.description}", Toast.LENGTH_LONG).show()
            }
            override fun onStart(requestId: String) {}
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
            override fun onReschedule(requestId: String, error: ErrorInfo) {}
        }).dispatch()
    }

    private fun regenerateStory() {
        binding?.progressBar?.isVisible = true
        val originalStory = binding?.etUpdateStory?.text.toString()
        if (originalStory.isEmpty()) {
            Toast.makeText(requireContext(), "Cannot regenerate an empty story.", Toast.LENGTH_SHORT).show()
            binding?.progressBar?.isVisible = false
            return
        }
        val prompt = "You are a creative writing assistant. Rewrite the following journal entry to be more reflective and descriptive, but keep the core meaning the same. Here is the original entry:\n\n\"$originalStory\""

        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                binding?.etUpdateStory?.setText(response.text)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding?.progressBar?.isVisible = false
            }
        }
    }

    private fun updateJournalEntry() {
        val newTitle = binding?.etUpdateTitle?.text.toString().trim()
        val newStory = binding?.etUpdateStory?.text.toString().trim()

        if (newTitle.isEmpty() || newStory.isEmpty()) {
            Toast.makeText(requireContext(), "Title and story cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        binding?.progressBar?.isVisible = true
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null || journalEntry.id.isEmpty()) {
            Toast.makeText(requireContext(), "Error updating. User/Entry not found.", Toast.LENGTH_SHORT).show()
            binding?.progressBar?.isVisible = false
            return
        }

        val updates = mapOf(
            "title" to newTitle,
            "storyContent" to newStory,
            "imageUrl1" to newImageUrl1,
            "imageUrl2" to newImageUrl2
        )

        FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("journals").document(journalEntry.id)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Entry updated!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                binding?.progressBar?.isVisible = false
                Toast.makeText(requireContext(), "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}