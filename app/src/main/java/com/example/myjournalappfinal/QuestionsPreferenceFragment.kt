package com.example.myjournalappfinal

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myjournalappfinal.Adapters.QuestionsAdapter
import com.example.myjournalappfinal.Interfaces.QuestionClickInterface
import com.example.myjournalappfinal.Models.QuestionsEntities
import com.example.myjournalappfinal.Models.SharedViewModel
import com.example.myjournalappfinal.databinding.CustomAddquestionDialogbindingBinding
import com.example.myjournalappfinal.databinding.FragmentQuestionsPreferenceBinding
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.gson.Gson
import kotlinx.coroutines.launch

class QuestionsPreferenceFragment : Fragment(), QuestionClickInterface {
    private var binding: FragmentQuestionsPreferenceBinding? = null
    private lateinit var questionsList: ArrayList<QuestionsEntities>
    private var questionsAdapter: QuestionsAdapter? = null
    private lateinit var sharedViewModel: SharedViewModel

    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionsPreferenceBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        questionsList = ArrayList()
        questionsAdapter = QuestionsAdapter(requireContext(), questionsList, this)
        binding?.recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recyclerView?.adapter = questionsAdapter

        generateQuestionsWithAI()

        binding?.next?.setOnClickListener {
            if (questionsList.isEmpty()) {
                Toast.makeText(requireContext(), "Please wait for questions to generate.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val txttitle = binding?.etTitle?.text.toString().trim()
            if (txttitle.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a title for your journal.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sharedViewModel.title = txttitle
            val questionsListArray = QuestionsList()
            questionsListArray.questionsList?.addAll(questionsList)
            val bundle = Bundle()
            bundle.putString("questions", Gson().toJson(questionsListArray))

            // ✅ CHANGED: Navigate to the final question answering screen.
            // Make sure this action is defined in your nav_graph.xml
            findNavController().navigate(R.id.questionsFragment, bundle)
        }

        binding?.addQuestion?.setOnClickListener {
            // ... (Your code for adding a manual question)
        }

        binding?.btnRegenerate?.setOnClickListener {
            generateQuestionsWithAI()
        }
    }

    private fun generateQuestionsWithAI() {
        binding?.progressBar?.isVisible = true
        binding?.next?.isEnabled = false
        binding?.btnRegenerate?.isEnabled = false

        val prompt = "Generate 5 short and simple one-sentence questions for a daily journal. The questions should be easy to answer. Provide them as a simple numbered list."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                val generatedQuestions = parseQuestions(response.text ?: "")
                questionsList.clear()
                questionsList.addAll(generatedQuestions)
                questionsAdapter?.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error generating questions: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding?.progressBar?.isVisible = false
                binding?.next?.isEnabled = true
                binding?.btnRegenerate?.isEnabled = true
            }
        }
    }

    private fun parseQuestions(responseText: String): ArrayList<QuestionsEntities> {
        val parsedList = ArrayList<QuestionsEntities>()
        val lines = responseText.split("\n")
        for (line in lines) {
            val questionText = line.replaceFirst(Regex("^\\d+\\.\\s*"), "").trim()
            if (questionText.isNotEmpty()) {
                parsedList.add(QuestionsEntities(questionText))
            }
        }
        return parsedList
    }

    override fun showDelete(position: Int) {
        questionsList.removeAt(position)
        questionsAdapter?.notifyItemRemoved(position)
    }

    override fun showEdit(position: Int) {
        // Handle edit functionality
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}