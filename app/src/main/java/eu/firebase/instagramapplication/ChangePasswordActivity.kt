package eu.firebase.instagramapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class ChangePasswordActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    lateinit var back: ImageView
    lateinit var email: EditText
    lateinit var sendBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        firebaseAuth = FirebaseAuth.getInstance()

        back = findViewById(R.id.close)
        email = findViewById(R.id.emailText)
        sendBtn = findViewById(R.id.resetConfirm)

        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        sendBtn.setOnClickListener {
            if (email.text.isEmpty()){
                emailError(email)
            }else{
                if (isEmailValid(email)){
                    checkEmail(email)

                }else{
                    emailError(email)
                }
            }
        }
    }

    /**Check if email exist*/
    private fun checkEmail(editText: EditText) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.fetchSignInMethodsForEmail(editText.text.toString())
            .addOnCompleteListener {
                val newUser: Boolean = it.result.signInMethods!!.isEmpty()
                if (newUser) {
                    emailError(editText)
                } else {
                    sendEmail(email)
                }
            }
    }

    /**Send email to reset password*/
    private fun sendEmail(editText: EditText) {
        firebaseAuth = FirebaseAuth.getInstance()
        val contextView = findViewById<View>(android.R.id.content)
        val imm: InputMethodManager = baseContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        firebaseAuth.sendPasswordResetEmail(editText.text.toString())
            .addOnSuccessListener {
                imm.hideSoftInputFromWindow(contextView.windowToken, 0)
                Snackbar.make(contextView, "Email was sent", Snackbar.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                imm.hideSoftInputFromWindow(contextView.windowToken, 0)
                Snackbar.make(contextView, "Oops! Something went wrong", Snackbar.LENGTH_SHORT)
                    .show()
            }
    }

    /**Show error in EditText*/
    private fun emailError(editText: EditText){
        editText.error = "Please check your e-mail address"
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editText.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    /**Check if input text have email format*/
    private fun isEmailValid(editText: EditText): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(editText.text.toString()).matches()
    }
}