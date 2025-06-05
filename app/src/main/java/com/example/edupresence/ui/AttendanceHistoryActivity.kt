package com.example.edupresence.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.edupresence.databinding.ActivityAttendanceHistoryBinding
import com.example.edupresence.ui.adapter.AttendanceAdapter
import com.example.edupresence.viewmodel.AttendanceHistoryViewModel

class AttendanceHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceHistoryBinding
    private val viewModel: AttendanceHistoryViewModel by viewModels()
    private lateinit var adapter: AttendanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AttendanceAdapter()
        binding.rvAttendance.adapter = adapter
        binding.rvAttendance.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        viewModel.attendanceRecords.observe(this) { records ->
            adapter.submitList(records)
        }

        viewModel.loadAttendanceHistory()
    }
}