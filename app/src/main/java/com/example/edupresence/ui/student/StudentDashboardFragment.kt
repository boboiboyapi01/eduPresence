package com.example.edupresence.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.edupresence.databinding.FragmentDashboardBinding
import com.example.edupresence.ui.adapter.AttendanceAdapter
import com.example.edupresence.viewmodel.StudentViewModel

class StudentDashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by activityViewModels()
    private lateinit var adapter: AttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AttendanceAdapter()
        binding.rvSchedule.adapter = adapter
        binding.rvSchedule.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        viewModel.schedule.observe(viewLifecycleOwner) { schedule ->
            adapter.submitList(schedule)
        }

        viewModel.loadStudentDashboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}