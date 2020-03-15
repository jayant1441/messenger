package com.jayantx.messenger

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        btn_sign_up.setOnClickListener {

            NoEmptyFields()

            val progress_dialog = ProgressDialog(this)
            if (!et_signup_name.text.isEmpty() && !et_signup_email.text.isEmpty()&&!et_signup_password.text.isEmpty()&&!et_signup_username.text.isEmpty()  && et_signup_password.text.length >= 8){
                if (Patterns.EMAIL_ADDRESS.matcher(et_signup_email.text.toString()).matches()){
                    showProgressDialog()
                }
            }
            else{
                Toast.makeText(this,"vella", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(et_signup_email.text.toString(), et_signup_password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progress_dialog.dismiss()
                        val user = auth.currentUser
                        SaveImageToStorage()
                        val intent = Intent(this,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        this.finish()
                    } else {
                        Log.d("taging", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        progress_dialog.dismiss()
                    }

                }
        }




        iv_select_photo.setOnClickListener {
            val intent_to_gallery = Intent(Intent.ACTION_PICK)
            intent_to_gallery.type = "image/*"
            startActivityForResult(intent_to_gallery,0)
        }

        tv_already_have_an_account_login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }

    var SelectedImageUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==0 && resultCode == Activity.RESULT_OK && data != null){
            SelectedImageUri = data.data
            val selected_image_bitmap  = MediaStore.Images.Media.getBitmap(contentResolver,SelectedImageUri)
            iv_profile_image.setImageBitmap(selected_image_bitmap)
            imageView.alpha = 0f
            textView.alpha = 0f

        }
    }


    private fun NoEmptyFields(){

        if (et_signup_name.text.isEmpty()) {
            et_signup_name.error = "Please enter your name"
            et_signup_name.requestFocus()
            return
        }
        if (et_signup_email.text.isEmpty()){
            et_signup_email.error = "Please enter your email"
            et_signup_email.requestFocus()
            return
        }
        if (et_signup_password.text.isEmpty()){
            et_signup_password.error = "Please enter password"
            et_signup_password.requestFocus()
            return
        }
        if (et_signup_password.text.length < 8){
            et_signup_password.error = "Please enter 8 character long Password"
            et_signup_password.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(et_signup_email.text.toString()).matches()){
            et_signup_email.error = "Please enter correct email"
            et_signup_email.requestFocus()
            return
        }
        if (et_signup_username.text.isEmpty()){
            et_signup_username.error = "Please enter username"
            et_signup_username.requestFocus()
            return
        }
    }


    private fun showProgressDialog(){
        var progress_dialog = ProgressDialog(this)
        progress_dialog.setTitle("SigningUp")
        progress_dialog.setMessage("Please wait this may take a moment")
        progress_dialog.setCancelable(false)
        progress_dialog.show()
    }






    private fun SaveImageToStorage(){
        val currentUserUUID = FirebaseAuth.getInstance().currentUser!!.uid

        if (SelectedImageUri == null) return
        val storageRef = storage.getReference("/images/$currentUserUUID")
        storageRef.putFile(SelectedImageUri!!).addOnSuccessListener {
           storageRef.downloadUrl.addOnSuccessListener {
               Log.d("yoyohoney",it.toString())
               UploadToFirebase(it.toString())
           }
        }



//        image_download_link = imagesRef?.child("images")?.child(FirebaseAuth.getInstance().currentUser!!.uid)?.downloadUrl.toString()
    }





    class FirebaseDatabaseDataClass(var name : String, var Email: String , var password:String, var uuid : String, var username: String , var profile_pic : String ){
        constructor(): this("","","","","","")
    }

    private fun UploadToFirebase( image_download_link: String){

        database = FirebaseDatabase.getInstance().reference
        val database_child = database.child("users")
        val uuid = FirebaseAuth.getInstance().currentUser!!.uid
        val user = FirebaseDatabaseDataClass(et_signup_name.text.toString().toLowerCase(), et_signup_email.text.toString().toLowerCase() , et_signup_password.text.toString(), uuid!!, et_signup_username.text.toString().toLowerCase(), image_download_link )
        database_child.child(uuid).setValue(user)
    }





}