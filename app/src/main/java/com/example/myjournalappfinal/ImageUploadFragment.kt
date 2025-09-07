package com.example.myjournalappfinal

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.myjournalappfinal.Models.SharedViewModel
import com.example.myjournalappfinal.databinding.DialogJournalStyleBinding
import com.example.myjournalappfinal.databinding.FragmentImageUploadBinding

class ImageUploadFragment : Fragment() {

    private var binding: FragmentImageUploadBinding? = null
    private lateinit var sharedViewModel: SharedViewModel
    // Note: questionsBundle is removed as it's no longer needed here.

    private val imagePickerLauncher1 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleImageSelection(it, binding!!.ivPhoto1) }
    }
    private val imagePickerLauncher2 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleImageSelection(it, binding!!.ivPhoto2) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageUploadBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        // arguments are no longer needed for questions here

        binding?.ivPhoto1?.setOnClickListener { imagePickerLauncher1.launch("image/*") }
        binding?.ivPhoto2?.setOnClickListener { imagePickerLauncher2.launch("image/*") }

        binding?.btnNext?.setOnClickListener {
            showJournalStyleChoiceDialog()
        }
    }

    private fun showJournalStyleChoiceDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DialogJournalStyleBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // ✅ CHANGED: This button now navigates to the question *preference* screen first.
        dialogBinding.btnAiGuided.setOnClickListener {
            // Make sure this action is defined in your nav_graph.xml
            findNavController().navigate(R.id.questionsPreferenceFragment)
            dialog.dismiss()
        }

        // This button's navigation remains the same.
        dialogBinding.btnFreestyle.setOnClickListener {
            findNavController().navigate(R.id.freestyleFragment)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }


    private fun handleImageSelection(uri: Uri, imageView: ImageView) {
        imageView.setImageURI(uri)
        val imageNumber = if (imageView.id == R.id.ivPhoto1) 1 else 2
        uploadToCloudinary(uri, imageNumber)
    }

    private fun setUploadingState(isUploading: Boolean) {
        binding?.progressBar?.isVisible = isUploading
        binding?.ivPhoto1?.isEnabled = !isUploading
        binding?.ivPhoto2?.isEnabled = !isUploading
        binding?.ivPhoto1?.alpha = if (isUploading) 0.5f else 1.0f
        binding?.ivPhoto2?.alpha = if (isUploading) 0.5f else 1.0f
    }

    private fun uploadToCloudinary(uri: Uri, imageNumber: Int) {
        setUploadingState(true)
        MediaManager.get().upload(uri).callback(object : UploadCallback {
            override fun onStart(requestId: String) {}
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                setUploadingState(false)
                val secureUrl = resultData["secure_url"] as? String
                if (imageNumber == 1) {
                    sharedViewModel.imageUrl1 = secureUrl
                } else {
                    sharedViewModel.imageUrl2 = secureUrl
                }
                Toast.makeText(requireContext(), "Photo $imageNumber uploaded", Toast.LENGTH_SHORT).show()
            }
            override fun onError(requestId: String, error: ErrorInfo) {
                setUploadingState(false)
                Toast.makeText(requireContext(), "Upload failed: ${error.description}", Toast.LENGTH_LONG).show()
            }
            override fun onReschedule(requestId: String, error: ErrorInfo) {}
        }).dispatch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}