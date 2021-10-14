package eu.firebase.instagramapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var login_email: EditText
    lateinit var login_password: EditText
    lateinit var login_btn: Button

    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        login_email = findViewById(R.id.login_email)
        login_password = findViewById(R.id.login_password)
        login_btn = findViewById(R.id.login_btn)

        firebaseAuth = FirebaseAuth.getInstance()

        login_btn.setOnClickListener {
            if (login_email.text.toString().isNotEmpty() && login_password.text.toString().isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(login_email.text.toString(), login_password.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                    }else{
                        Toast.makeText(this, "Wrong email/password", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}