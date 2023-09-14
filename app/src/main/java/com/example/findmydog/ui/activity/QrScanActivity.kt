package com.example.findmydog.ui.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.findmydog.R
import com.budiyev.android.codescanner.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private val CAMERA_REQUEST_CODE = 101
private lateinit var databaseReference: DatabaseReference

class QrScanActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scan)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)
        databaseReference= FirebaseDatabase.getInstance().getReference("Users")
        val actionBar = supportActionBar

        actionBar!!.setDisplayHomeAsUpEnabled(true)

        setUpPermissions()
        codeScanner()
    }

    private fun codeScanner() {
        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val str=it.text
                /*val str1=str.split("[")
                val str2=str1[1].split("]")
                val dogList=str2[0].split(",")*/
                val str1=str.split("https://find-my-dog-lyuv.vercel.app/information/")
                val str2=str1[1].split("/")
                val uid=str2[0]
                val dogId=str2[1]
                val uidRef = databaseReference.child(uid).child("Dogs").child(dogId)
                uidRef.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val snapshot = it.result
                        val dogsName = snapshot.child("dogsName").getValue(String::class.java)
                        val breed = snapshot.child("breed").getValue(String::class.java)
                        val owner = snapshot.child("owner").getValue(String::class.java)
                        val phone = snapshot.child("phone").getValue(String::class.java)
                        val email = snapshot.child("email").getValue(String::class.java)
                        val address = snapshot.child("address").getValue(String::class.java)
                        val profileImageUri = snapshot.child("profileImageUri").getValue(String::class.java)
                        val qrCodeImage = snapshot.child("qrCodeImage").getValue(String::class.java)
                        val intent= Intent(this@QrScanActivity, DetailsActivity::class.java)
                        intent.putExtra("dogsName", dogsName)
                        intent.putExtra("breed", breed)
                        intent.putExtra("owner", owner)
                        intent.putExtra("phone", phone)
                        intent.putExtra("email", email)
                        intent.putExtra("address", address)
                        intent.putExtra("profileImageUri", profileImageUri)
                        intent.putExtra("qrCodeImage", qrCodeImage)
                        startActivity(intent)
                    } else {
                        Log.d(TAG, "${it.exception?.message}") //Never ignore potential errors!
                    }
                }
                /*intent.putExtra("dogsName", dogList[0])
                intent.putExtra("breed", dogList[1])
                intent.putExtra("owner", dogList[2])
                intent.putExtra("phone", dogList[3])
                intent.putExtra("email", dogList[4])
                intent.putExtra("address", dogList[5])
                intent.putExtra("profileImageUri", dogList[6].trim())*/
                /*startActivity(intent)*/
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setUpPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "You need camera permission to use app", Toast.LENGTH_SHORT).show()
                else {
                    //successful
                }
            }
        }
    }
}
