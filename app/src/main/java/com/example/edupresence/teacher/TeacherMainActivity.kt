package com.example.edupresence.teacher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edupresence.R
import com.example.edupresence.model.Class
import com.example.edupresence.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_teacher_main.*

class TeacherMainActivity : AppCompatActivity() {

    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var classAdapter: ClassAdapter
    private val classList = mutableListOf<Class>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)

        firebaseUtils = FirebaseUtils()
        setupRecyclerView()
        loadTeacherClasses()
    }

    private fun setupRecyclerView() {
        classAdapter = ClassAdapter(classList) { selectedClass ->
            val intent = Intent(this, ClassManagementActivity::class.java)
            intent.putExtra("class_id", selectedClass.id)
            startActivity(intent)
        }

        rvClasses.apply {
            layoutManager = LinearLayoutManager(this@TeacherMainActivity)
            adapter = classAdapter
        }
    }

    private fun loadTeacherClasses() {
        val teacherId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firebaseUtils.getTeacherClasses(teacherId) { classes ->
            classList.clear()
            classList.addAll(classes)
            classAdapter.notifyDataSetChanged()
        }
    }
}