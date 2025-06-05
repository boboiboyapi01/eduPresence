package com.example.edupresence.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.edupresence.databinding.ActivityAttendanceHistoryBinding
import com.example.edupresence.ui.adapter.AttendanceAdapter
import com.example.edupresence.viewmodel.AttendanceHistoryViewModel

class AttendanceHistoryFragment : Fragment() {
    private var _binding: ActivityAttendanceHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AttendanceHistoryViewModel by activityViewModels()
    private lateinit var adapter: AttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityAttendanceHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AttendanceAdapter()
        binding.rvAttendance.adapter = adapter
        binding.rvAttendance.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        viewModel.attendanceRecords.observe(viewLifecycleOwner) { records ->
            adapter.submitList(records)
        }

        viewModel.loadAttendanceHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}