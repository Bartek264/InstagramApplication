package eu.firebase.instagramapplication

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import eu.firebase.instagramapplication.model.UserData
import android.widget.LinearLayout

import android.widget.TextView

import android.widget.ProgressBar
import eu.firebase.instagramapplication.login.googleLogin

open class StartActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var progressDialog: ProgressDialog

    lateinit var reg_username: EditText
    lateinit var reg_password: EditText
    lateinit var reg_email: EditText

    lateinit var google_btn: Button
    lateinit var reg_btn: Button
    lateinit var log_btn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        supportActionBar?.hide()

        reg_email = findViewById(R.id.regis_email)
        reg_password = findViewById(R.id.regis_password)
        reg_username = findViewById(R.id.regis_username)
        reg_btn = findViewById(R.id.regis_btn)
        log_btn = findViewById(R.id.login)

        google_btn = findViewById(R.id.google_button)

        google_btn.setOnClickListener {
            val intent = Intent(this, googleLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
            finish();
        }

        log_btn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        reg_btn.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Loading")
            progressDialog.show()
            if (reg_email.text.toString().isNotEmpty() && reg_password.text.toString().isNotEmpty() && reg_username.text.toString().isNotEmpty()){
                firebaseAuth.createUserWithEmailAndPassword(reg_email.text.toString(), reg_password.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        userLogin(reg_username.text.toString(), reg_email.text.toString())
                    }else{
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun userLogin(username: String, email: String){
        FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.currentUser!!.uid)
            .setValue(UserData(email,username,firebaseAuth.currentUser!!.uid,
                "https://firebasestorage.googleapis.com/v0/b/githubapps-d5c56.appspot.com/o/899048ab0cc455154006fdb9676964b3.jpg?alt=media&token=38e1180f-270d-4bbb-bf70-c29939d20778",""))
        startActivity(Intent(this, MainActivity::class.java))
        progressDialog.dismiss()
        finish()
    }

}