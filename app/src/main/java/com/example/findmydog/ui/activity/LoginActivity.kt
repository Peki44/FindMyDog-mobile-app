package com.example.findmydog.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.findmydog.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loginBtn: Button
    private lateinit var scanBtn: Button
    private lateinit var emailEt: EditText
    private lateinit var passEt:EditText
    private lateinit var signUpTxt: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)

        firebaseAuth=FirebaseAuth.getInstance()
        loginBtn=findViewById(R.id.login)
        scanBtn=findViewById(R.id.scanCode)
        emailEt=findViewById(R.id.emailLogin)
        passEt=findViewById(R.id.passwordLogin)
        signUpTxt=findViewById(R.id.singUpText)

        signUpTxt.setOnClickListener {
            val intent= Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        loginBtn.setOnClickListener {
            val email=emailEt.text.toString()
            val pass=passEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()){

                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            val user = firebaseAuth.currentUser
                            if(user != null){
                                val intent= Intent(this, CentralActivity::class.java)
                                startActivity(intent)
                            }
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(this,"Some fields are empty", Toast.LENGTH_SHORT).show()
            }
        }
        scanBtn.setOnClickListener {
            val intentScan = Intent(this@LoginActivity, QrScanActivity::class.java)
            startActivity(intentScan)
        }

    }
    override fun onStart() {

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if(currentUser != null){
            val i = Intent(this, CentralActivity::class.java)
            startActivity(i)
        }
        super.onStart()
    }
}