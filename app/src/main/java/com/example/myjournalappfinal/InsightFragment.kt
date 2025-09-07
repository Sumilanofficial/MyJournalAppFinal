package com.example.myjournalappfinal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.myjournalappfinal.Models.JournalEntry
import com.example.myjournalappfinal.databinding.FragmentInsightBinding

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class InsightFragment : Fragment() {

    private var binding: FragmentInsightBinding? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-1.5-flash")

    private var allEntries = listOf<JournalEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsightBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAllEntries()

        binding?.btnGenerateSummary?.setOnClickListener {
            generateWeeklySummary()
        }
    }



    private fun fetchAllEntries() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("journals")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    allEntries = documents.toObjects(JournalEntry::class.java)
                    // Once all entries are fetched, find the "On This Day" memory
//                    findOnThisDayEntry()
                }
            }
            .addOnFailureListener { e ->
                Log.w("InsightsFragment", "Error getting documents: ", e)
            }
    }

    private fun findOnThisDayEntry() {
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH)
        val currentMonth = today.get(Calendar.MONTH)
        val currentYear = today.get(Calendar.YEAR)

        val memory = allEntries.find { entry ->
            // Safely handle nullable timestamp
            entry.timestamp?.let {
                val entryDate = Calendar.getInstance().apply { time = it }
                val entryDay = entryDate.get(Calendar.DAY_OF_MONTH)
                val entryMonth = entryDate.get(Calendar.MONTH)
                val entryYear = entryDate.get(Calendar.YEAR)

                // Check if it's the same day and month, but from a past year
                entryDay == currentDay && entryMonth == currentMonth && entryYear < currentYear
            } ?: false // If timestamp is null, it's not a match
        }

//        // Use apply for cleaner access to binding properties
//        binding?.apply {
//            if (memory != null) {
//                // --- Case 1: Memory Found ---
//                // Set the details from the found memory
//                tvOnThisDayTitle.text = memory.title
//                tvOnThisDayDate.text = memory.entryDate
//                tvOnThisDaySnippet.text = memory.storyContent
//
//                // Show the detail views and hide the empty message view
//                tvOnThisDayTitle.isVisible = true
//                tvOnThisDayDate.isVisible = true
//                tvOnThisDaySnippet.isVisible = true
//                tvOnThisDayEmpty.isVisible = false
//            } else {
//
//            }
//        }
    }
    private fun generateWeeklySummary() {
        val weekAgo = Calendar.getInstance()
        weekAgo.add(Calendar.DAY_OF_YEAR, -7)

        val recentEntries = allEntries.filter { it.timestamp != null && it.timestamp.after(weekAgo.time) }

        if (recentEntries.isEmpty()) {
            binding?.tvAiSummary?.text = "You haven't written any entries in the last 7 days. Write something to get a reflection!"
            return
        }

        binding?.progressAiSummary?.isVisible = true
        binding?.btnGenerateSummary?.isEnabled = false
        binding?.tvAiSummary?.text = ""

        val entriesText = recentEntries.joinToString("\n---\n") { "Title: ${it.title}\nEntry: ${it.storyContent}" }

        val prompt = "You are a compassionate mindfulness coach. Analyze the following journal entries from the past week and provide a short, encouraging, and insightful summary of the user's recurring thoughts and feelings. Speak in the second person ('You seemed to focus on...'). Do not just list the topics; find the underlying emotional themes. Keep it to one paragraph. Entries:\n\n$entriesText"

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                binding?.tvAiSummary?.text = response.text
            } catch (e: Exception) {
                binding?.tvAiSummary?.text = "Could not generate summary. Please try again."
                Log.e("InsightsFragment", "AI Summary Error", e)
            } finally {
                binding?.progressAiSummary?.isVisible = false
                binding?.btnGenerateSummary?.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
