package com.example.finalproject

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.databinding.TasksDetailBinding
import com.bumptech.glide.Glide

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var binding: TasksDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TasksDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskId = intent.getStringExtra("taskId") ?: ""
        val task = intent.getStringExtra("task") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val imageUri = intent.getStringExtra("imageUri") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        if (imageUri.isNotEmpty()) {
            val uri = Uri.parse(imageUri)
            Glide.with(this)
                .load(imageUri)
                .into(binding.taskImageView)
        }

        binding.taskDetailTv.text = task
        binding.dateDetailTv.text = date
        binding.timeDetailTv.text = time
        binding.taskDescTv.text = description

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}