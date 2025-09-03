package com.example.myjournalappfinal

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.myjournalappfinal.databinding.FragmentImageUploadBinding

class ImageUploadFragment : Fragment() {

    private var binding: FragmentImageUploadBinding? = null
    private lateinit var sharedViewModel: SharedViewModel
    private var questionsBundle: Bundle? = null

    // ActivityResultLaunchers for picking images
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
        questionsBundle = arguments // Keep the bundle to pass it forward

        binding?.ivPhoto1?.setOnClickListener { imagePickerLauncher1.launch("image/*") }
        binding?.ivPhoto2?.setOnClickListener { imagePickerLauncher2.launch("image/*") }

        binding?.btnNext?.setOnClickListener {
            // Simply navigate. URLs are already saved in the ViewModel.
            findNavController().navigate(R.id.questionsFragment, questionsBundle)
        }
    }

    private fun handleImageSelection(uri: Uri, imageView: ImageView) {
        imageView.setImageURI(uri)
        uploadToCloudinary(uri, if (imageView.id == R.id.ivPhoto1) 1 else 2)
    }

    private fun uploadToCloudinary(uri: Uri, imageNumber: Int) {
        binding?.progressBar?.isVisible = true
        MediaManager.get().upload(uri).callback(object : UploadCallback {
            override fun onStart(requestId: String) {}
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                binding?.progressBar?.isVisible = false
                val secureUrl = resultData["secure_url"] as? String
                if (imageNumber == 1) {
                    sharedViewModel.imageUrl1 = secureUrl
                } else {
                    sharedViewModel.imageUrl2 = secureUrl
                }
                Toast.makeText(requireContext(), "Photo $imageNumber uploaded", Toast.LENGTH_SHORT).show()
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                binding?.progressBar?.isVisible = false
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