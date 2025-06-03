package com.example.edupresence.teacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.edupresence.R
import com.example.edupresence.model.Attendance
import kotlinx.android.synthetic.main.item_attendance.view.*
import java.text.SimpleDateFormat
import java.util.*

class AttendanceAdapter(
    private val attendances: List<Attendance>
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendance = attendances[position]

        holder.itemView.apply {
            tvStudentName.text = attendance.studentName

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            tvDate.text = dateFormat.format(Date(attendance.timestamp))
            tvTime.text = timeFormat.format(Date(attendance.timestamp))

            tvStatus.text = when(attendance.status) {
                "present" -> "Hadir"
                "late" -> "Terlambat"
                "absent" -> "Tidak Hadir"
                else -> attendance.status
            }

            // Set status color
            val statusColor = when(attendance.status) {
                "present" -> android.R.color.holo_green_dark
                "late" -> android.R.color.holo_orange_dark
                "absent" -> android.R.color.holo_red_dark
                else -> android.R.color.black
            }
            tvStatus.setTextColor(context.getColor(statusColor))
        }
    }

    override fun getItemCount() = attendances.size
}