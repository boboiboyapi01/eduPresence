package com.example.edupresence.teacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.edupresence.R
import com.example.edupresence.model.Class
import kotlinx.android.synthetic.main.item_class.view.*

class ClassAdapter(
    private val classes: List<Class>,
    private val onClassClick: (Class) -> Unit
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classItem = classes[position]

        holder.itemView.apply {
            tvClassName.text = classItem.name
            tvStudentCount.text = "${classItem.students.size} siswa"
            tvSchedule.text = "${classItem.schedule.startTime} - ${classItem.schedule.endTime}"

            setOnClickListener {
                onClassClick(classItem)
            }
        }
    }

    override fun getItemCount() = classes.size
}