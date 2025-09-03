package com.example.myjournalappfinal


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myjournalappfinal.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    // Declare the ViewBinding and FirebaseAuth variables
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using ViewBinding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Set up the click listener for the Sign Up button
        binding.signupButton.setOnClickListener {
            registerUser()
        }

        // Set up the click listener for the "Log In" text
        binding.loginTextView.setOnClickListener {
            finish() // Or simply close this activity
        }

        // Set up the back button listener
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Handles the user registration process.
     */
    private fun registerUser() {
        // 1. Get user input and trim whitespace
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

        // 2. Validate the input (This section remains unchanged)
        if (name.isEmpty()) {
            binding.nameInputLayout.error = "Full Name is required."
            return
        } else {
            binding.nameInputLayout.error = null
        }

        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email is required."
            return
        } else {
            binding.emailInputLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Password is required."
            return
        } else {
            binding.passwordInputLayout.error = null
        }

        if (password.length < 6) {
            binding.passwordInputLayout.error = "Password must be at least 6 characters."
            return
        } else {
            binding.passwordInputLayout.error = null
        }

        if (confirmPassword != password) {
            binding.confirmPasswordInputLayout.error = "Passwords do not match."
            return
        } else {
            binding.confirmPasswordInputLayout.error = null
        }

        // 3. Create user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, now configure and send verification email
                    Log.d("SignUpActivity", "createUserWithEmail:success")
                    val user = auth.currentUser

                    // --- MODIFIED SECTION START ---

                    // 1. Create ActionCodeSettings to configure the email link
                    val actionCodeSettings = ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain must be whitelisted.
                        .setUrl("https://myjournalapp-final.web.app/verify?email=${user?.email}")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                            "com.example.firebaseailogic", // Your app's package name
                            true, // installIfNotAvailable
                            null  // minimumVersion
                        )
                        .build()

                    // 2. Send the verification email with the new settings
                    user?.sendEmailVerification(actionCodeSettings)
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                // Email sent successfully
                                Toast.makeText(
                                    baseContext,
                                    "Registration successful. Please check your email for verification.",
                                    Toast.LENGTH_LONG
                                ).show()

                                auth.signOut()
                                finish()

                            } else {
                                // Failed to send verification email
                                Log.w(
                                    "SignUpActivity",
                                    "sendEmailVerification:failure",
                                    verificationTask.exception
                                )
                                Toast.makeText(
                                    baseContext,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    // --- MODIFIED SECTION END ---

                } else {
                    // If sign up fails, display a message to the user.
                    Log.w("SignUpActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}