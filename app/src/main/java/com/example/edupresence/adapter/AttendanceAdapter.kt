package com.example.edupresence.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.edupresence.databinding.ItemAttendanceBinding
import com.example.edupresence.model.AttendanceRecord
import com.example.edupresence.model.AttendanceSession

class AttendanceAdapter : ListAdapter<Any, AttendanceAdapter.ViewHolder>(AttendanceDiffCallback()) {
    class ViewHolder(private val binding: ItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any) {
            when (item) {
                is AttendanceRecord -> {
                    binding.tvSubject.text = "Session: ${item.sessionId}"
                    binding.tvDate.text = item.timestamp.toString()
                    binding.tvStatus.text = item.status
                }
                is AttendanceSession -> {
                    binding.tvSubject.text = item.subjectName
                    binding.tvDate.text = item.date.toString()
                    binding.tvStatus.text = if (item.isActive) "Active" else "Closed"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class AttendanceDiffCallback : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is AttendanceRecord && newItem is AttendanceRecord ->
                oldItem.recordId == newItem.recordId
            oldItem is AttendanceSession && newItem is AttendanceSession ->
                oldItem.sessionId == newItem.sessionId
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }
}