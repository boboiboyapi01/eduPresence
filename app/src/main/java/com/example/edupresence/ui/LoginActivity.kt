package com.example.edupresence.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.example.edupresence.databinding.ActivityLoginBinding
import com.example.edupresence.model.User
import com.example.edupresence.ui.student.StudentMainActivity
import com.example.edupresence.ui.teacher.TeacherMainActivity
import com.example.edupresence.utils.Constants
import com.example.edupresence.viewmodel.LoginViewModel
import com.example.edupresence.viewmodel.LoginViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(auth)
    }

    private var role: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Cek apakah pengguna sudah login
        if (auth.currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val savedRole = getUserRoleFromFirestore(auth.currentUser!!.uid)
                if (savedRole != null) {
                    saveUserRole(savedRole)
                    navigateToMain(savedRole)
                }
            }
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                viewModel.login(email, password)
            }
        }

        // Observasi hasil login
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { user ->
                CoroutineScope(Dispatchers.IO).launch {
                    val userRole = getUserRoleFromFirestore(user.uid)
                    if (userRole != null) {
                        saveUserRole(userRole)
                        navigateToMain(userRole)
                    } else {
                        Snackbar.make(binding.root, "Role not found in Firestore", Snackbar.LENGTH_LONG).show()
                    }
                }
            }.onFailure { exception ->
                val errorMessage = when (exception.message) {
                    "The email address is badly formatted." -> "Invalid email format"
                    "There is no user record corresponding to this identifier." -> "Email not registered"
                    "The password is invalid or the user does not have a password." -> "Invalid password"
                    else -> "Login failed: ${exception.message}"
                }
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }

        // Observasi loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                false
            }
            !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() -> {
                Snackbar.make(binding.root, "Please enter a valid email", Snackbar.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                Snackbar.make(binding.root, "Password must be at least 6 characters", Snackbar.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private suspend fun getUserRoleFromFirestore(uid: String): String? {
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            userDoc.toObject(User::class.java)?.role
        } catch (e: Exception) {
            null
        }
    }

    private fun saveUserRole(role: String) {
        val sharedPref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(Constants.KEY_ROLE, role)
            apply()
        }
    }

    private fun navigateToMain(role: String) {
        val targetActivity = if (role == Constants.ROLE_TEACHER) {
            TeacherMainActivity::class.java
        } else {
            StudentMainActivity::class.java
        }
        startActivity(Intent(this@LoginActivity, targetActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}