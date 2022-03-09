package eu.firebase.instagramapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import eu.firebase.instagramapplication.fragment.HomeFragment
import eu.firebase.instagramapplication.fragment.NotificationFragment
import eu.firebase.instagramapplication.fragment.ProfileFragment
import eu.firebase.instagramapplication.fragment.SearchFragment

class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var selectedFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        bottomNavigationView = findViewById(R.id.bottom_nav_bar)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

        val intent = intent.extras
        if (intent!=null){
            val publisher = intent.getString("publisherId")

            val editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("publisherId", publisher)
            editor.apply()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
        }

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.nav_search -> {
                    selectedFragment = SearchFragment()
                }
                R.id.nav_add -> {
                    startActivity(Intent(this,PostActivity::class.java))
                }
                R.id.nav_heart -> {
                    selectedFragment = NotificationFragment()
                }
                R.id.nav_profile -> {
                    val editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit()
                    editor.putString("profileid", FirebaseAuth.getInstance().currentUser?.uid)
                    editor.apply()
                    selectedFragment = ProfileFragment()
                }
            }
            if (selectedFragment != null){
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
            }
            true
        }
    }
    override fun onBackPressed() {
        val text = "Are you sure you want to exit?"
        val textYes = "Yes"
        val textNo ="No"
        val builder = AlertDialog.Builder(this)
        builder.setMessage(text)
            .setCancelable(false)
            .setPositiveButton(textYes,
                DialogInterface.OnClickListener { dialog, id -> this.finish() })
            .setNegativeButton(textNo,
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}