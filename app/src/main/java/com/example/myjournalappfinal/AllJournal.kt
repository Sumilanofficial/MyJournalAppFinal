package com.example.myjournalappfinal

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myjournalappfinal.Adapters.JournalAdapter
import com.example.myjournalappfinal.Interfaces.JournalInteractionListener
import com.example.myjournalappfinal.Models.JournalEntry
import com.example.myjournalappfinal.databinding.DeleteDialogBindingBinding
import com.example.myjournalappfinal.databinding.FragmentAllJournalBinding
import com.example.myjournalappfinal.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllJournal.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllJournal : Fragment(), JournalInteractionListener {
    private var binding: FragmentAllJournalBinding? = null
    private lateinit var journalAdapter: JournalAdapter
    private val journalList = ArrayList<JournalEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllJournalBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchJournalEntries()


    }

    private fun setupRecyclerView() {
        journalAdapter = JournalAdapter(journalList, this)
        binding?.rvJournalEntries?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = journalAdapter
        }
    }

    // ✅ This is correct. You are using directions from your current location.
    override fun onItemClick(journalEntry: JournalEntry) {
        val action = AllJournalDirections.actionAllJournalToUpdateJournalFragment(journalEntry)
        findNavController().navigate(action)
    }

    // --- THIS IS THE CORRECTED METHOD ---
    override fun onItemLongClick(journalEntry: JournalEntry) {
        // This method now correctly calls the one below without extra code.
        showDeleteConfirmationDialog(journalEntry)
    }

    private fun showDeleteConfirmationDialog(journalEntry: JournalEntry) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DeleteDialogBindingBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnYes.setOnClickListener {
            deleteJournalEntry(journalEntry)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // --- THIS IS THE CORRECTED PLACEMENT ---
        // Set the dialog size to match the parent's width and wrap content height
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun deleteJournalEntry(journalEntry: JournalEntry) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null || journalEntry.id.isEmpty()) {
            Toast.makeText(requireContext(), "Error: Could not delete entry.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("journals").document(journalEntry.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Entry deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to delete entry: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchJournalEntries() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            binding?.tvNoEntries?.isVisible = true
            binding?.rvJournalEntries?.isVisible = false
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .collection("journals")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("HomeFragment", "Listen failed.", error)
                    Toast.makeText(requireContext(), "Failed to load entries.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    binding?.tvNoEntries?.isVisible = false
                    binding?.rvJournalEntries?.isVisible = true

                    val newEntries = snapshots.toObjects(JournalEntry::class.java)
                    journalAdapter.updateData(newEntries)
                } else {
                    binding?.tvNoEntries?.isVisible = true
                    binding?.rvJournalEntries?.isVisible = false
                    journalAdapter.updateData(emptyList())
                    Log.d("HomeFragment", "Current data: null or empty")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}