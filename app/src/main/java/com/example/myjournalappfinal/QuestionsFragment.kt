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

    // --- State Management Variables ---
    private lateinit var questionsList: List<QuestionsEntities>
    private var currentQuestionIndex = 0
    private val questionAnswers = mutableMapOf<String, String>()

    // --- (CHANGE 1) Initialize the model as a class property using the new syntax ---
    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

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
        if (questionsJson != null) {
            val questionsListArray = Gson().fromJson(questionsJson, QuestionsList::class.java)
            questionsList = questionsListArray.questionsList ?: emptyList()
        } else {
            questionsList = emptyList()
        }

        if (questionsList.isNotEmpty()) {
            displayCurrentQuestion()
        } else {
            Toast.makeText(requireContext(), "No questions found.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        binding?.btnNextFinish?.setOnClickListener {
            handleNextOrFinishClick()
        }
    }

    private fun displayCurrentQuestion() {
        val currentQuestion = questionsList[currentQuestionIndex]
        binding?.apply {
            tvQuestionCounter.text = "Question ${currentQuestionIndex + 1} of ${questionsList.size}"
            tvQuestionText.text = currentQuestion.question
            etAnswer.text.clear()
            if (currentQuestionIndex == questionsList.size - 1) {
                btnNextFinish.text = "Finish"
            } else {
                btnNextFinish.text = "Next"
            }
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

        currentQuestionIndex++
        if (currentQuestionIndex < questionsList.size) {
            displayCurrentQuestion()
        } else {
            generateStoryWithGemini()
        }
    }

    /**
     * Generates a journal story using the REFACTORED Gemini AI logic.
     */
    private fun generateStoryWithGemini() {
        binding?.apply {
            progressBar.isVisible = true
            tvQuestionCounter.isVisible = false
            tvQuestionText.text = "Crafting your story, please wait..."
            etAnswer.isVisible = false
            btnNextFinish.isEnabled = false
        }

        val prompt = buildString {
            append("You are a helpful and creative journaling assistant. ")
            append("Based on the following questions and answers, write a cohesive and reflective journal entry in a first-person narrative style. ")
            append("The tone should be thoughtful and personal.\n\n")
            append("Journal Title: ${sharedViewModel.title}\n\n")
            questionAnswers.forEach { (question, answer) ->
                append("Question: $question\n")
                append("Answer: $answer\n\n")
            }
            append("Now, combine these points into a flowing journal entry:")
        }

        // --- (CHANGE 2) Use viewLifecycleOwner.lifecycleScope for safety in fragments ---
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                val story = response.text ?: "Could not generate a story. Please try again."

                // --- (CHANGE 3) No more withContext needed, code is much simpler ---
                saveJournalEntryToFirestore(story)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error generating story: ${e.message}", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun saveJournalEntryToFirestore(story: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "Error: You must be logged in to save an entry.", Toast.LENGTH_LONG).show()
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
                Toast.makeText(requireContext(), "Journal entry saved successfully!", Toast.LENGTH_LONG).show()
                sharedViewModel.imageUrl1 = null
                sharedViewModel.imageUrl2 = null
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            .addOnFailureListener { e ->
                binding?.progressBar?.isVisible = false
                Toast.makeText(requireContext(), "Failed to save entry: ${e.message}", Toast.LENGTH_LONG).show()
                binding?.btnNextFinish?.isEnabled = true
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}