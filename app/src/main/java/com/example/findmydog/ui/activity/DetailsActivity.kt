package com.example.findmydog.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.findmydog.R


class DetailsActivity : AppCompatActivity() {

    private lateinit var imageDog: ImageView
    private lateinit var dogName:TextView
    private lateinit var dogsBreed:TextView
    private lateinit var dogOwner:TextView
    private lateinit var ownerNumber:TextView
    private lateinit var ownerEmail:TextView
    private lateinit var ownerAddress:TextView
    private lateinit var qrImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        imageDog=findViewById(R.id.dogImage)
        dogName=findViewById(R.id.dogName)
        dogsBreed=findViewById(R.id.dogsBreed)
        dogOwner=findViewById(R.id.dogOwner)
        ownerNumber=findViewById(R.id.ownerNumber)
        ownerEmail=findViewById(R.id.ownerEmail)
        ownerAddress=findViewById(R.id.ownerAddress)
        qrImage=findViewById(R.id.qrcodeImage)

        setValuesToViews()
    }

    private fun setValuesToViews() {
        dogName.text=intent.getStringExtra("dogsName")
        dogsBreed.text=intent.getStringExtra("breed")
        dogOwner.text=intent.getStringExtra("owner")
        ownerNumber.text=intent.getStringExtra("phone")
        ownerAddress.text=intent.getStringExtra("address")
        ownerEmail.text=intent.getStringExtra("email")
        val imageTarget=intent.getStringExtra("profileImageUri")
        Glide.with(this).load(imageTarget).into(imageDog)
        val imageTarget2=intent.getStringExtra("qrCodeImage")
        Glide.with(this).load(imageTarget2).into(qrImage)
    }
}