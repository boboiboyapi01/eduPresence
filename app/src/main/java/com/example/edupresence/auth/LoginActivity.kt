package com.example.edupresence.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edupresence.R
import com.example.edupresence.model.User
import com.example.edupresence.student.StudentMainActivity
import com.example.edupresence.teacher.TeacherMainActivity
import com.example.edupresence.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUtils: FirebaseUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firebaseUtils = FirebaseUtils()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            checkUserRoleAndNavigate(auth.currentUser!!.uid)
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            return false
        }
        return true
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        checkUserRoleAndNavigate(it.uid)
                    }
                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRoleAndNavigate(userId: String) {
        firebaseUtils.getUser(userId) { user ->
            if (user != null) {
                if (user.role == "teacher") {
                    startActivity(Intent(this, TeacherMainActivity::class.java))
                } else if (user.role == "student") {
                    // For students, go to face verification first
                    val intent = Intent(this, FaceVerificationActivity::class.java)
                    intent.putExtra("user_id", userId)
                    startActivity(intent)
                }
                finish()
            } else {
                Toast.makeText(this, "User data tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}