package com.example.finalproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private lateinit var auth:FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun init(view:View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents(){
        binding.SignInTv.setOnClickListener{
            navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.nextBtn.setOnClickListener{
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passInput.text.toString().trim()
            if(email.isNotEmpty() && password.isNotEmpty()){
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context, "Account Registered", Toast.LENGTH_SHORT).show()
                            navControl.navigate(R.id.action_signUpFragment_to_contentsFragment)
                        }else{
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }else{
                Toast.makeText(context,"Email or Password is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}