package com.example.myjournalappfinal

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
import com.example.myjournalappfinal.Models.JournalEntry
import com.example.myjournalappfinal.Models.QuestionsEntities
import com.example.myjournalappfinal.Models.SharedViewModel
import com.example.myjournalappfinal.databinding.FragmentQuestionsBinding
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuestionsFragment : Fragment() {

    private var binding: FragmentQuestionsBinding? = null
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var questionsList: List<QuestionsEntities>
    private var currentQuestionIndex = 0
    private val questionAnswers = mutableMapOf<String, String>()

    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-1.5-flash")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val questionsJson = arguments?.getString("questions")
        questionsList = if (questionsJson != null) {
            Gson().fromJson(questionsJson, QuestionsList::class.java).questionsList ?: emptyList()
        } else {
            emptyList()
        }

        if (questionsList.isNotEmpty()) {
            // Show the initial question UI
            binding?.apply {
                tvQuestionCounter.isVisible = true
                tvQuestionText.isVisible = true
                etAnswer.isVisible = true
                nextSkip.isVisible = true
                // Hide review UI just in case
                tvReviewTitle.isVisible = false
                etStoryPreview.isVisible = false
                llReviewButtons.isVisible = false
            }
            displayCurrentQuestion()
        } else {
            Toast.makeText(requireContext(), "No questions found.", Toast.LENGTH_LONG).show()
        }

        binding?.btnNextFinish?.setOnClickListener { handleNextOrFinishClick() }
        binding?.btnSkip?.setOnClickListener { handleSkipClick() }
        binding?.btnRegenerateStory?.setOnClickListener { generateStoryWithGemini() }
        binding?.btnSaveStory?.setOnClickListener {
            val finalStory = binding?.etStoryPreview?.text.toString().trim()
            if (finalStory.isNotEmpty()) {
                saveJournalEntryToFirestore(finalStory)
            } else {
                Toast.makeText(requireContext(), "Story cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayCurrentQuestion() {
        val currentQuestion = questionsList[currentQuestionIndex]
        binding?.apply {
            tvQuestionCounter.text = "Question ${currentQuestionIndex + 1} of ${questionsList.size}"
            tvQuestionText.text = currentQuestion.question
            etAnswer.text.clear()
            btnNextFinish.text = if (currentQuestionIndex == questionsList.size - 1) "Finish" else "Next"
        }
    }

    private fun handleSkipClick() {
        currentQuestionIndex++
        if (currentQuestionIndex < questionsList.size) {
            displayCurrentQuestion()
        } else {
            generateStoryWithGemini()
        }
    }

    private fun handleNextOrFinishClick() {
        val answer = binding?.etAnswer?.text?.toString()?.trim()
        if (answer.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please provide an answer.", Toast.LENGTH_SHORT).show()
            return
        }
        val currentQuestion = questionsList[currentQuestionIndex]
        questionAnswers[currentQuestion.question] = answer
        handleSkipClick()
    }

    private fun generateStoryWithGemini() {
        // Show loading UI
        binding?.apply {
            progressBar.isVisible = true
            // Hide everything else
            tvQuestionCounter.isVisible = false
            tvQuestionText.isVisible = false
            etAnswer.isVisible = false
            nextSkip.isVisible = false
            tvReviewTitle.isVisible = false
            etStoryPreview.isVisible = false
            llReviewButtons.isVisible = false
        }

        val prompt = buildString {
            append("You are a helpful and creative journaling assistant. ")
            append("Based on the following questions and answers, write a cohesive and reflective journal entry in a first-person narrative style. ")
            append("The tone should be thoughtful and personal.\n\n")
            append("Journal Title: ${sharedViewModel.title ?: "My Day"}\n\n")
            if (questionAnswers.isEmpty()) {
                append("The user skipped all questions. Write a brief, reflective entry about the importance of taking a moment to think.\n\n")
            } else {
                questionAnswers.forEach { (question, answer) ->
                    append("Question: $question\n")
                    append("Answer: $answer\n\n")
                }
            }
            append("Now, combine these points into a flowing journal entry:")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                val story = response.text ?: "Could not generate a story. Please try again."

                // Show the review UI with the generated story
                binding?.apply {
                    progressBar.isVisible = false
                    tvReviewTitle.isVisible = true
                    etStoryPreview.isVisible = true
                    llReviewButtons.isVisible = true
                    etStoryPreview.setText(story)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error generating story: ${e.message}", Toast.LENGTH_LONG).show()
                // On error, go back to the question UI
                binding?.apply {
                    progressBar.isVisible = false
                    tvQuestionCounter.isVisible = true
                    tvQuestionText.isVisible = true
                    etAnswer.isVisible = true
                    nextSkip.isVisible = true
                }
            }
        }
    }

    private fun saveJournalEntryToFirestore(story: String) {
        binding?.progressBar?.isVisible = true
        binding?.llReviewButtons?.isVisible = false // Hide buttons during save

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "Error: You must be logged in.", Toast.LENGTH_LONG).show()
            binding?.progressBar?.isVisible = false
            binding?.llReviewButtons?.isVisible = true // Re-show buttons on failure
            return
        }

        val db = FirebaseFirestore.getInstance()
        val entryDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val entryTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val journalEntry = JournalEntry(
            userId = userId,
            title = sharedViewModel.title,
            storyContent = story,
            entryDate = entryDate,
            entryTime = entryTime,
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
                binding?.llReviewButtons?.isVisible = true // Re-show buttons on failure
                Toast.makeText(requireContext(), "Failed to save entry: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}