package com.thymex.employee

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*



class Login : Fragment() {
private lateinit var mAuth:FirebaseAuth
private lateinit var emails:String
    private lateinit var password:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_login, container!!, false)
        mAuth= FirebaseAuth.getInstance()
        emails=view.email.text.toString().trim()
        password=view.password_login.text.toString().trim()
        view.login_btn.setOnClickListener {

            loginUser()

        }




        return view
    }



    private fun loginUser() {
        emails= email.text.toString().trim()
        password= password_login.text.toString().trim()
        if(emails.isEmpty()){
            email.setError("Phone cannot be Empty, it Must be filled")

        }
        if(password.isEmpty()){
            password_login.setError("Password cannot be Empty,it Must be filled")

        }

        if(emails.isEmpty()||password.isEmpty()){
            Toast.makeText(requireContext(),"Can Not Log in,Please fillup all the fields", Toast.LENGTH_LONG).show()
            return
        }else{
            mAuth.signInWithEmailAndPassword(emails+"@office.com",password).addOnCompleteListener {task->
                if(task.isSuccessful){
                    val i= Intent(requireContext(),MainActivity::class.java)
                    startActivity(i)

                }else{
                    Toast.makeText(requireContext(),"Something Wrong! Please Check your Phone Number and Password ! or Try again after few seconds",
                        Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }
            }
        }
    }





}