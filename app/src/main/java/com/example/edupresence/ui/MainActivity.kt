package com.example.edupresence.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edupresence.databinding.ActivityMainBinding
import com.example.edupresence.ui.student.StudentMainActivity
import com.example.edupresence.ui.teacher.TeacherMainActivity
import com.example.edupresence.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is already logged in
        val sharedPref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val role = sharedPref.getString(Constants.KEY_ROLE, null)
        val user = auth.currentUser

        if (user != null && role != null) {
            if (role == Constants.ROLE_TEACHER) {
                startActivity(Intent(this, TeacherMainActivity::class.java))
            } else {
                startActivity(Intent(this, StudentMainActivity::class.java))
            }
            finish()
            return
        }

        binding.btnTeacher.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(Constants.KEY_ROLE, Constants.ROLE_TEACHER)
            startActivity(intent)
        }

        binding.btnStudent.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(Constants.KEY_ROLE, Constants.ROLE_STUDENT)
            startActivity(intent)
        }
    }
}