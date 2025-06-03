package com.example.edupresence.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.edupresence.R
import com.example.edupresence.model.Attendance
import kotlinx.android.synthetic.main.item_student_attendance.view.*
import java.text.SimpleDateFormat
import java.util.*

class StudentAttendanceAdapter(
    private val attendances: List<Attendance>
) : RecyclerView.Adapter<StudentAttendanceAdapter.StudentAttendanceViewHolder>() {

    class StudentAttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_attendance, parent, false)
        return StudentAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentAttendanceViewHolder, position: Int) {
        val attendance = attendances[position]

        holder.itemView.apply {
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            tvAttendanceDate.text = dateFormat.format(Date(attendance.timestamp))
            tvAttendanceTime.text = "Pukul ${timeFormat.format(Date(attendance.timestamp))}"

            tvAttendanceStatus.text = when(attendance.status) {
                "present" -> "Hadir"
                "late" -> "Terlambat"
                "absent" -> "Tidak Hadir"
                else -> attendance.status
            }

            // Set status color and icon
            when(attendance.status) {
                "present" -> {
                    tvAttendanceStatus.setTextColor(context.getColor(android.R.color.holo_green_dark))
                    ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
                }
                "late" -> {
                    tvAttendanceStatus.setTextColor(context.getColor(android.R.color.holo_orange_dark))
                    ivStatusIcon.setImageResource(R.drawable.ic_warning)
                }
                "absent" -> {
                    tvAttendanceStatus.setTextColor(context.getColor(android.R.color.holo_red_dark))
                    ivStatusIcon.setImageResource(R.drawable.ic_cancel)
                }
            }
        }
    }

    override fun getItemCount() = attendances.size
}