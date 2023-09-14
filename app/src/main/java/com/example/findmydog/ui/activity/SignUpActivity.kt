package com.example.findmydog.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.findmydog.ui.activity.QrScanActivity
import com.example.findmydog.R
import com.google.firebase.auth.FirebaseAuth


class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signUpBtn: Button
    private lateinit var emailEt: EditText
    private lateinit var passEt:EditText
    private lateinit var scanBtn: Button
    private lateinit var passConfirmEt:EditText
    private lateinit var loginTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)


        firebaseAuth=FirebaseAuth.getInstance()
        signUpBtn=findViewById(R.id.signUp)
        emailEt=findViewById(R.id.emailSignUp)
        scanBtn=findViewById(R.id.scanCode)
        passEt=findViewById(R.id.passwordSignUp)
        passConfirmEt=findViewById(R.id.passwordConfirmSignUp)
        loginTxt=findViewById(R.id.loginText)

        loginTxt.setOnClickListener {
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpBtn.setOnClickListener {
            val email=emailEt.text.toString()
            val pass=passEt.text.toString()
            val confirmPass=passConfirmEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                if(pass == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            val user = firebaseAuth.currentUser
                            if(user != null){
                                val intent= Intent(this, CentralActivity::class.java)
                                startActivity(intent)
                            }
                        }else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Password is not matching",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Some fields are empty",Toast.LENGTH_SHORT).show()
            }
        }
        scanBtn.setOnClickListener {
            val intentScan = Intent(this@SignUpActivity, QrScanActivity::class.java)
            startActivity(intentScan)
        }
    }
}