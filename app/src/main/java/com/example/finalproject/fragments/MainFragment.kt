package com.example.finalproject.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {
    private lateinit var  auth: FirebaseAuth
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        view.findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_signInFragment)
        }

        view.findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_signUpFragment)
        }

        Handler(Looper.myLooper()!!).postDelayed(Runnable{
            if(auth.currentUser != null){
                navController.navigate(R.id.action_mainFragment_to_contentsFragment)
            }
        },2000)
    }
}