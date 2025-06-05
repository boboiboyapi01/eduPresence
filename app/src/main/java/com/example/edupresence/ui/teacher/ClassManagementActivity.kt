package com.example.edupresence.ui.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.edupresence.databinding.ActivityClassManagementBinding
import com.example.edupresence.model.Class

class ClassManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassManagementBinding
    private val classAdapter = ClassAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewClasses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewClasses.adapter = classAdapter

        val classList = listOf(
            Class(id = 1, name = "Math 101", teacherId = "teacher1"),
            Class(id = 2, name = "Science 102", teacherId = "teacher1")
        )

        classAdapter.submitList(classList)
    }

    class ClassAdapter :
        ListAdapter<Class, ClassAdapter.ClassViewHolder>(DIFF_CALLBACK) {

        companion object {
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Class>() {
                override fun areItemsTheSame(oldItem: Class, newItem: Class): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Class, newItem: Class): Boolean =
                    oldItem == newItem
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ClassViewHolder(view)
        }

        override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(classItem: Class) {
                (itemView as TextView).text = classItem.name
            }
        }
    }
}
