package com.example.edupresence.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.edupresence.R
import com.example.edupresence.model.Class
import kotlinx.android.synthetic.main.item_student_class.view.*

class StudentClassAdapter(
    private val classes: List<Class>,
    private val onClassClick: (Class) -> Unit
) : RecyclerView.Adapter<StudentClassAdapter.StudentClassViewHolder>() {

    class StudentClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_class, parent, false)
        return StudentClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentClassViewHolder, position: Int) {
        val classItem = classes[position]

        holder.itemView.apply {
            tvClassName.text = classItem.name
            tvTeacherName.text = "Pengajar: ${classItem.teacherName}"

            val dayName = getDayName(classItem.schedule.dayOfWeek)
            tvScheduleInfo.text = "$dayName, ${classItem.schedule.startTime} - ${classItem.schedule.endTime}"
            tvLocationInfo.text = classItem.schedule.location

            setOnClickListener {
                onClassClick(classItem)
            }
        }
    }

    override fun getItemCount() = classes.size

    private fun getDayName(dayOfWeek: Int): String {
        return when(dayOfWeek) {
            1 -> "Senin"
            2 -> "Selasa"
            3 -> "Rabu"
            4 -> "Kamis"
            5 -> "Jumat"
            6 -> "Sabtu"
            7 -> "Minggu"
            else -> "Unknown"
        }
    }
}