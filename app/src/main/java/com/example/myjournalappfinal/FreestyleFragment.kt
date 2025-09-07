package com.example.myjournalappfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myjournalappfinal.Models.JournalEntry
import com.example.myjournalappfinal.Models.SharedViewModel
import com.example.myjournalappfinal.databinding.FragmentFreestyleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FreestyleFragment : Fragment() {

    private var binding: FragmentFreestyleBinding? = null
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFreestyleBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding?.btnSave?.setOnClickListener {
            val content = binding?.etFreestyleContent?.text?.toString()?.trim()
            if (content.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Your journal entry cannot be empty.", Toast.LENGTH_SHORT).show()
            } else {
                saveJournalEntryToFirestore(content)
            }
        }
    }

    private fun saveJournalEntryToFirestore(story: String) {
        binding?.progressBar?.isVisible = true
        binding?.btnSave?.isEnabled = false

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "You must be logged in to save.", Toast.LENGTH_SHORT).show()
            binding?.progressBar?.isVisible = false
            binding?.btnSave?.isEnabled = true
            return
        }

        val db = FirebaseFirestore.getInstance()
        val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val journalEntry = JournalEntry(
            userId = userId,
            title = sharedViewModel.title,
            storyContent = story,
            entryDate = currentDate,
            entryTime = currentTime,
            imageUrl1 = sharedViewModel.imageUrl1,
            imageUrl2 = sharedViewModel.imageUrl2,
            timestamp = Date()
        )

        db.collection("users").document(userId).collection("journals")
            .add(journalEntry)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Journal entry saved!", Toast.LENGTH_LONG).show()
                sharedViewModel.imageUrl1 = null
                sharedViewModel.imageUrl2 = null
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            .addOnFailureListener { e ->
                binding?.progressBar?.isVisible = false
                binding?.btnSave?.isEnabled = true
                Toast.makeText(requireContext(), "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}