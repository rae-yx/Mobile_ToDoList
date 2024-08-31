package com.example.finalproject.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentPopUpBinding
import com.example.finalproject.utils.TaskData
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class PopUpFragment : DialogFragment() {
    private lateinit var binding: FragmentPopUpBinding
    private lateinit var listener: TaskCreateBtnClickListener
    private var taskData: TaskData?= null
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null


    fun setListener(listener: TaskCreateBtnClickListener){
        this.listener = listener
    }

    companion object{
        const val TAG = "PopUpFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) = PopUpFragment().apply{
            arguments = Bundle().apply{
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null){
            taskData = TaskData(arguments?.getString("taskId").toString(), arguments?.getString("task").toString())
            binding.taskInput.setText(taskData?.task)
        }
        registerEvents()
        binding.uploadImageBtn.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.taskImageView.setImageURI(selectedImageUri)
        }
    }

    private fun registerEvents(){
        binding.taskCreateBtn.setOnClickListener(){
            val task = binding.taskInput.text.toString()
            val description = binding.taskDescInput.text.toString()
            if(task.isNotEmpty()){
                if(taskData == null){
                    listener.onSaveTask(task, selectedDate, selectedTime, selectedImageUri, description, binding.taskInput)
                }else{
                    taskData?.task = task
                    taskData?.date = selectedDate
                    taskData?.time = selectedTime
                    taskData?.imageUri = selectedImageUri?.toString()
                    taskData?.description = description
                    listener.onUpdateTask(taskData!!, binding.taskInput)
                }
            }else{
                Toast.makeText(context,"Task is empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.closeBtn.setOnClickListener{
            dismiss()
        }

        binding.pickDateBtn.setOnClickListener{
            showDatePickerDialog()
        }

        binding.pickTimeBtn.setOnClickListener{
            showTimePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            Toast.makeText(context, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            selectedTime = "$selectedHour:$selectedMinute"
            Toast.makeText(context, "Selected time: $selectedTime", Toast.LENGTH_SHORT).show()
        }, hour, minute, true)
        timePickerDialog.show()
    }

    interface TaskCreateBtnClickListener{
        fun onSaveTask(taskInfo: String, date: String, time: String, imageUri: Uri?, description:String, taskInput: TextInputEditText)
        fun onUpdateTask(taskData: TaskData, taskInput: TextInputEditText)
    }
}