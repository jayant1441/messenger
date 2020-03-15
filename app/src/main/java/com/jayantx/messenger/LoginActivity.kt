package com.jayantx.messenger

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var progress_dialog = ProgressDialog(this)
        auth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener {

            NoEmptyFields()

            showProgressDialog()

            auth.signInWithEmailAndPassword(et_login_email.text.toString(), et_login_password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progress_dialog.dismiss()
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        progress_dialog.dismiss()
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }

        tv_dont_have_an_account.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }


        tv_visit_developer_github.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://github.com/jayant1441/")
            startActivity(intent)

        }
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser!=null){
            updateUI(currentUser)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user!=null){
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
        else{
            Toast.makeText(baseContext, "Authentication failed error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun NoEmptyFields(){


        if (et_login_email.text.isEmpty()){
            et_login_email.error = "Please enter your email"
            et_login_email.requestFocus()
            return
        }
        if (et_login_email.text.isEmpty()){
            et_login_email.error = "Please enter password"
            et_login_email.requestFocus()
            return
        }

    }

    private fun showProgressDialog(){
        var progress_dialog = ProgressDialog(this)
        progress_dialog.setTitle("Login")
        progress_dialog.setMessage("Please wait this may take a moment")
        progress_dialog.setCancelable(false)
        progress_dialog.show()
    }
}

