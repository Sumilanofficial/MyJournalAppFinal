package com.example.myjournalappfinal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myjournalappfinal.Auth.LoginActivity
import com.example.myjournalappfinal.Auth.SignUpActivity
import com.example.myjournalappfinal.databinding.ActivityWelcomeScreenBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    // Declare the binding variable
    private lateinit var binding: ActivityWelcomeScreenBinding
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (and verified) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            // If user is already logged in and verified, go to MainActivity
            navigateToMainActivity()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()        // Inflate the layout using View Binding
        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- OnClickListener for Login Button ---
        binding.btnLogin.setOnClickListener {
            // Create an Intent to navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // --- OnClickListener for Register/Sign Up Button ---
        binding.btnRegister.setOnClickListener {
            // Create an Intent to navigate to SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // Clear the back stack so the user can't press back to return to the login screen
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}