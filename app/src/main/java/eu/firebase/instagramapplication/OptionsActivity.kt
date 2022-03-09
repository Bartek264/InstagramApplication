package eu.firebase.instagramapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class OptionsActivity : AppCompatActivity() {

    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var logout: LinearLayout
    lateinit var settings: LinearLayout
    lateinit var reset: LinearLayout

    lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        reset = findViewById(R.id.resetBar)
        logout = findViewById(R.id.logoutBar)
        settings = findViewById(R.id.settingsBar)
        back = findViewById(R.id.back)

        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        reset.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        logout.setOnClickListener {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("884442411398-uhplem5o598asj43p6fr1vffhdgl4qcp.apps.googleusercontent.com")
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->

                        //Firebase Sign-out
                        FirebaseAuth.getInstance().signOut()

                        //Google Sign-out
                        googleSignInClient.signOut()

                        startActivity(Intent(this, StartActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)})

                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        settings.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }
}