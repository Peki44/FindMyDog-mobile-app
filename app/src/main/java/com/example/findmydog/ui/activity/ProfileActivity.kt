package com.example.findmydog.ui.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import coil.load
import com.example.findmydog.R
import com.example.findmydog.model.data.Dog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference:DatabaseReference
    private lateinit var addBtn:Button
    private lateinit var etDogsName:EditText
    private lateinit var etBreed:EditText
    private lateinit var etOwner:EditText
    private lateinit var etPhoneNumber:EditText
    private lateinit var etAddress:EditText
    private lateinit var etEmail:EditText
    private lateinit var storageReference:StorageReference
    private lateinit var imageUri:Uri
    private lateinit var qrCodeUri:Uri

    private lateinit var imageQrCode: ImageView
    private lateinit var btnGenerateQrCode: Button

    private lateinit var imageDog: ImageView
    private lateinit var btnOpenCamera: Button
    private lateinit var btnOpenGallery: Button
    var clicked = false
    var clickedImage=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        firebaseAuth=FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance().getReference("Users")

        btnOpenCamera = findViewById(R.id.camera)
        btnOpenGallery = findViewById(R.id.gallery)
        imageDog=findViewById(R.id.circleDogImage)

        addBtn=findViewById(R.id.addDog)
        etDogsName=findViewById(R.id.dogsname)
        etBreed=findViewById(R.id.breed)
        etOwner=findViewById(R.id.owner)
        etPhoneNumber=findViewById(R.id.phoneNumber)
        etAddress=findViewById(R.id.address)
        etEmail=findViewById(R.id.email)

        addBtn.setOnClickListener {
            if(etDogsName.text.isEmpty() || etBreed.text.isEmpty() || etOwner.text.isEmpty() || etPhoneNumber.text.isEmpty()
                || etAddress.text.isEmpty() || etEmail.text.isEmpty() || clickedImage.not()){
                Toast.makeText(this,"Enter all information", Toast.LENGTH_SHORT).show()
            }else {
                clicked=true
                uploadProfilePicture()
            }
        }

        btnOpenCamera.setOnClickListener {
            cameraCheckPermission()
            clickedImage=true
        }
        btnOpenGallery.setOnClickListener {
            galleryCheckPermission()
            clickedImage=true
        }

        imageQrCode = findViewById(R.id.qrcode)
        btnGenerateQrCode = findViewById(R.id.generatebtn)


        val itemList: ArrayList<String> = arrayListOf()
        btnGenerateQrCode.setOnClickListener {
            if(clicked){
                val data1=etDogsName.text.toString().trim()
                val data2=etBreed.text.toString().trim()
                val data3=etOwner.text.toString().trim()
                val data4=etPhoneNumber.text.toString().trim()
                val data5=etEmail.text.toString().trim()
                val data6=etAddress.text.toString().trim()
                val data7=imageString
                val writer = QRCodeWriter()
                try {
                    itemList.add(data1)
                    itemList.add(data2)
                    itemList.add(data3)
                    itemList.add(data4)
                    itemList.add(data5)
                    itemList.add(data6)
                    itemList.add(data7)
                    val bitMatrix = writer.encode(itemList.toString(),BarcodeFormat.QR_CODE,512,512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp=Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565)
                    for(x in 0 until width){
                        for(y in 0 until height){
                            bmp.setPixel(x,y,if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    imageQrCode.setImageBitmap(bmp)
                    qrCodeUri=getImageUri(bmp)
                    uploadQrCode()
                }catch (e:WriterException){
                    e.printStackTrace()
                }
            }else{
                Toast.makeText(this,"Please add dog first", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            this.getContentResolver(),
            inImage,
            UUID.randomUUID().toString() + ".png",
            "drawing"
        )
        return Uri.parse(path)
    }
    fun uploadQrCode(){
        val filename=UUID.randomUUID().toString()
        val uid=firebaseAuth.currentUser?.uid
        storageReference=FirebaseStorage.getInstance().getReference("Users" +firebaseAuth.currentUser?.uid+ filename)
        if (uid != null) {
            storageReference.child(uid).putFile(qrCodeUri).addOnSuccessListener {
                storageReference.child(uid).downloadUrl.addOnSuccessListener {
                    saveQrCodeInDatabase(it.toString())
                }
            }.addOnFailureListener{
                Toast.makeText(this,"Failed to upload the image",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun saveQrCodeInDatabase(qrCodeImage:String){
        val uid=firebaseAuth.currentUser?.uid
        if (uid != null) {
            databaseReference.child(uid).child("Dogs").child(dogKey).child("qrCodeImage").setValue(qrCodeImage).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "QR code successfully updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update QR code", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {  }
        }
    }

    lateinit var imageString:String
    private fun uploadProfilePicture() {
        val filename=UUID.randomUUID().toString()
        val uid=firebaseAuth.currentUser?.uid
        storageReference=FirebaseStorage.getInstance().getReference("Users" +firebaseAuth.currentUser?.uid+ filename)
        if (uid != null) {
            storageReference.child(uid).putFile(imageUri).addOnSuccessListener {
                storageReference.child(uid).downloadUrl.addOnSuccessListener {
                    saveProfileInDatabase(it.toString())
                    imageString=it.toString()
                }
            }.addOnFailureListener{
                Toast.makeText(this,"Failed to upload the image",Toast.LENGTH_SHORT).show()
            }
        }
    }
    lateinit var emptyReference: DatabaseReference
    lateinit var dogKey:String

    private fun saveProfileInDatabase(profileImage:String) {
        val dogName=etDogsName.text.toString()
        val breed=etBreed.text.toString()
        val owner=etOwner.text.toString()
        val phoneNumber=etPhoneNumber.text.toString()
        val address=etAddress.text.toString()
        val email=etEmail.text.toString()
        val uid=firebaseAuth.currentUser?.uid

        val dog= Dog(profileImage,dogName,breed,owner,phoneNumber,address,email)
            if (uid != null) {
                emptyReference=databaseReference.child(uid).child("Dogs").push()
                dogKey= emptyReference.key.toString()
                emptyReference.setValue(dog).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Dog successfully added", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to add dog", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {  }
            }
    }

    private fun galleryCheckPermission() {
        Dexter.withContext(this).withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE).withListener(
            object :PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    gallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@ProfileActivity, "You have denied the storage permission to select image", Toast.LENGTH_SHORT
                    ).show()
                    showRotationalDialogForPermission()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?, p1: PermissionToken?
                ) {
                    showRotationalDialogForPermission()
                }

            }).onSameThread().check()
    }

    private fun gallery() {
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent,GALLERY_REQUEST_CODE)
    }

    private fun cameraCheckPermission() {
        Dexter.withContext(this).withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA).withListener(
            object:MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            camera()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRotationalDialogForPermission()
                }

            }
        ).onSameThread().check()
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions"
                    + "required for this feature. It can be enable under App settings!!!")

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.also {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.example.findmydog.android.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }
    }
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    lateinit var filePhoto:File
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    CAMERA_REQUEST_CODE -> {
                        filePhoto=File(currentPhotoPath)
                        imageDog.setImageURI(Uri.fromFile(filePhoto))
                        imageUri=Uri.fromFile(filePhoto)
                    }
                    GALLERY_REQUEST_CODE -> {
                        imageDog.load(data?.data)
                        imageUri = data?.data!!
                    }
                }
            }
        }
}

