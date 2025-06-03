package com.example.edupresence.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edupresence.R
import com.example.edupresence.student.StudentMainActivity
import com.example.edupresence.utils.FaceDetectionUtils
import com.example.edupresence.utils.FirebaseUtils
import kotlinx.android.synthetic.main.activity_face_verification.*

class FaceVerificationActivity : AppCompatActivity() {

    private lateinit var faceDetectionUtils: FaceDetectionUtils
    private lateinit var firebaseUtils: FirebaseUtils
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_verification)

        userId = intent.getStringExtra("user_id") ?: ""

        faceDetectionUtils = FaceDetectionUtils(this)
        firebaseUtils = FirebaseUtils()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        btnVerifyFace.setOnClickListener {
            startFaceVerification()
        }

        btnSkip.setOnClickListener {
            // For development/testing purposes
            navigateToStudentMain()
        }
    }

    private fun startFaceVerification() {
        faceDetectionUtils.startFaceDetection { faceEmbedding ->
            verifyFaceWithDatabase(faceEmbedding)
        }
    }

    private fun verifyFaceWithDatabase(capturedFaceEmbedding: String) {
        firebaseUtils.getUser(userId) { user ->
            if (user != null && user.faceEmbedding.isNotEmpty()) {
                val similarity = faceDetectionUtils.compareFaces(
                    user.faceEmbedding,
                    capturedFaceEmbedding
                )

                if (similarity > 0.8) { // 80% similarity threshold
                    Toast.makeText(this, "Verifikasi wajah berhasil!", Toast.LENGTH_SHORT).show()
                    navigateToStudentMain()
                } else {
                    Toast.makeText(this, "Verifikasi wajah gagal. Coba lagi.",
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Data wajah tidak ditemukan. Hubungi admin.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToStudentMain() {
        startActivity(Intent(this, StudentMainActivity::class.java))
        finish()
    }
}