package com.example.edupresence.student

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edupresence.R
import com.example.edupresence.model.Class
import com.example.edupresence.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_student_main.*

class StudentMainActivity : AppCompatActivity() {

    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var classAdapter: StudentClassAdapter
    private val classList = mutableListOf<Class>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)

        firebaseUtils = FirebaseUtils()
        setupRecyclerView()
        loadStudentClasses()
    }

    private fun setupRecyclerView() {
        classAdapter = StudentClassAdapter(classList) { selectedClass ->
            val intent = Intent(this, AttendanceActivity::class.java)
            intent.putExtra("class_id", selectedClass.id)
            startActivity(intent)
        }

        rvClasses.apply {
            layoutManager = LinearLayoutManager(this@StudentMainActivity)
            adapter = classAdapter
        }
    }

    private fun loadStudentClasses() {
        val studentId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firebaseUtils.getStudentClasses(studentId) { classes ->
            classList.clear()
            classList.addAll(classes)
            classAdapter.notifyDataSetChanged()
        }
    }
}