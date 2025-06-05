package com.example.edupresence.ui.teacher

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.edupresence.R
import com.example.edupresence.databinding.FragmentTeacherDashboardBinding
import com.example.edupresence.model.AttendanceSession
import com.example.edupresence.model.Class
import com.example.edupresence.repository.UserRepository
import com.example.edupresence.ui.LoginActivity
import com.example.edupresence.viewmodel.TeacherViewModel
import com.example.edupresence.viewmodel.TeacherViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TeacherDashboardFragment : Fragment() {

    private var _binding: FragmentTeacherDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val sessionAdapter = AttendanceSessionAdapter()
    private val classAdapter = ClassAdapter()

    private val viewModel: TeacherViewModel by viewModels {
        TeacherViewModelFactory(
            requireActivity().application,
            UserRepository(),
            auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView untuk attendance sessions
        binding.attendanceSessions.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = sessionAdapter
        }

        // Set up RecyclerView untuk classes (opsional, jika ingin ditampilkan)
        binding.classesRecyclerView?.apply { // Pastikan ID ini ada di layout
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = classAdapter
        }

        // Observasi loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi data sesi
        viewModel.attendanceSessions.observe(viewLifecycleOwner) { sessions ->
            sessionAdapter.submitList(sessions)
        }

        // Observasi data kelas
        viewModel.classes.observe(viewLifecycleOwner) { classes ->
            classAdapter.submitList(classes)
        }

        // Observasi pesan error
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }

        // Muat data
        viewModel.loadTeacherDashboard()
        viewModel.loadClassesFromSupabase() // Jika menggunakan Supabase
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class AttendanceSessionAdapter :
        ListAdapter<AttendanceSession, AttendanceSessionAdapter.SessionViewHolder>(DiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_attendance_session, parent, false)
            return SessionViewHolder(view)
        }

        override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val sessionDate: TextView = itemView.findViewById(R.id.sessionDate)
            private val sessionName: TextView = itemView.findViewById(R.id.sessionName)

            fun bind(session: AttendanceSession) {
                sessionDate.text = "Session on ${session.date}"
                sessionName.text = session.name ?: "Unnamed Session"
            }
        }

        companion object DiffCallback : DiffUtil.ItemCallback<AttendanceSession>() {
            override fun areItemsTheSame(
                oldItem: AttendanceSession,
                newItem: AttendanceSession
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: AttendanceSession,
                newItem: AttendanceSession
            ): Boolean = oldItem == newItem
        }
    }

    class ClassAdapter :
        ListAdapter<Class, ClassAdapter.ClassViewHolder>(ClassDiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_class, parent, false)
            return ClassViewHolder(view)
        }

        override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val className: TextView = itemView.findViewById(R.id.className)
            private val teacherName: TextView = itemView.findViewById(R.id.teacherName)

            fun bind(classItem: Class) {
                className.text = classItem.name
                teacherName.text = classItem.teacherName
            }
        }

        companion object ClassDiffCallback : DiffUtil.ItemCallback<Class>() {
            override fun areItemsTheSame(oldItem: Class, newItem: Class): Boolean =
                oldItem.teacherId == newItem.teacherId

            override fun areContentsTheSame(oldItem: Class, newItem: Class): Boolean =
                oldItem == newItem
        }
    }
}