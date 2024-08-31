package com.example.finalproject.utils

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.TaskDetailActivity
import com.example.finalproject.databinding.TasksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TaskAdapter (private val list:MutableList<TaskData>): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){
    private var listener: TaskAdapterClickInterface?= null
    fun setListener(listener:TaskAdapterClickInterface){
        this.listener = listener
    }
    inner class TaskViewHolder(val binding:TasksBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TasksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.taskTv.text = this.task
                binding.dateTv.text = this.date
                binding.timeTv.text = this.time
                binding.checkBox.isChecked = this.isChecked

                binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    this.isChecked = isChecked
                    val taskRef = FirebaseDatabase.getInstance().reference
                        .child("Tasks").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .child(this.taskId)
                    taskRef.child("isChecked").setValue(isChecked)
                }

                binding.deleteTask.setOnClickListener{
                    listener?.onDeleteTaskBtnClick(this)
                }
                binding.editTask.setOnClickListener{
                    listener?.onEditTaskBtnClick(this)
                }
                binding.root.setOnClickListener {
                    val context = it.context
                    val intent = Intent(context, TaskDetailActivity::class.java).apply {
                        putExtra("taskId", taskId)
                        putExtra("task", task)
                        putExtra("date", date)
                        putExtra("time", time)
                        putExtra("imageUri", imageUri)
                        putExtra("description", description)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
    interface TaskAdapterClickInterface{
        fun onDeleteTaskBtnClick(taskData: TaskData)
        fun onEditTaskBtnClick(taskData: TaskData)
    }
}