package com.example.finalproject.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentContentsBinding
import com.example.finalproject.utils.TaskAdapter
import com.example.finalproject.utils.TaskData
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class ContentsFragment : Fragment(), PopUpFragment.TaskCreateBtnClickListener,
    TaskAdapter.TaskAdapterClickInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentContentsBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<TaskData>
    private var popUpFragment: PopUpFragment? = null
    private var selectedImageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentContentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getData()
        registerEvents()
    }

    private fun registerEvents(){
        binding.addBtn.setOnClickListener{
            if(popUpFragment != null)
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = PopUpFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(childFragmentManager, PopUpFragment.TAG)
        }

        binding.logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        taskList = mutableListOf()
        taskAdapter = TaskAdapter(taskList)
        taskAdapter.setListener(this)
        binding.recyclerView.adapter = taskAdapter
    }

    private fun getData(){
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for(taskSnapshot in snapshot.children){
                    val taskId = taskSnapshot.key
                    val task = taskSnapshot.child("task").getValue(String::class.java) ?: ""
                    val date = taskSnapshot.child("date").getValue(String::class.java) ?: ""
                    val time = taskSnapshot.child("time").getValue(String::class.java) ?: ""
                    val imageUri = taskSnapshot.child("imageUri").getValue(String::class.java)
                    val isChecked = taskSnapshot.child("isChecked").getValue(Boolean::class.java) ?: false
                    val description = taskSnapshot.child("description").getValue(String::class.java) ?: ""


                    val taskData = taskId?.let { TaskData(it, task, date, time, imageUri, description, isChecked) }

                    if (taskData != null) {
                        taskList.add(taskData)
                    }
                }
                taskAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                context?.let {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.action_contentsFragment_to_mainFragment)
    }

    override fun onSaveTask(taskInfo: String, date: String, time: String, imageUri: Uri?, description: String, taskInput: TextInputEditText) {
        val newTask = TaskData(UUID.randomUUID().toString(), taskInfo, date, time, imageUri?.toString(), description)
        databaseReference.child(newTask.taskId).setValue(newTask).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Task created", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            taskInput.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(taskData: TaskData, taskInput: TextInputEditText) {
        val map = mapOf(
            "task" to taskData.task,
            "date" to taskData.date,
            "time" to taskData.time,
            "imageUri" to taskData.imageUri,
            "description" to taskData.description
        )
        databaseReference.child(taskData.taskId).updateChildren(map).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Task edited", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            taskInput.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClick(taskData: TaskData) {
        databaseReference.child(taskData.taskId).removeValue().addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClick(taskData: TaskData) {
        if(popUpFragment != null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()

        popUpFragment = PopUpFragment.newInstance(taskData.taskId, taskData.task)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager, PopUpFragment.TAG)

    }
}