package eu.firebase.instagramapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.core.UserData
import eu.firebase.instagramapplication.adapter.UserAdapter

class FollowersActivity : AppCompatActivity() {

    lateinit var id: String
    lateinit var title: String

    lateinit var toolbarTitle: TextView

    lateinit var idList: ArrayList<String>

    lateinit var backBtn: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var userList: ArrayList<eu.firebase.instagramapplication.model.UserData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers)

        toolbarTitle = findViewById(R.id.title)
        backBtn = findViewById(R.id.close)

        val intent = intent
        id = intent.getStringExtra("id")!!
        title = intent.getStringExtra("title")!!

        toolbarTitle.text = title

        backBtn.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter = UserAdapter(this, userList, false)
        recyclerView.adapter = userAdapter

        idList = ArrayList()
        when (title) {
            "likes" -> {
                getLikes()
            }
            "following" -> {
                getFollowing()
            }
            "followers" -> {
                getFollowers()
            }
        }
    }

    private fun getFollowers() {
        val reference = FirebaseDatabase.getInstance().getReference("Follow")
            .child(id).child("followers")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList.clear()
                for (snapshot : DataSnapshot in dataSnapshot.children){
                    idList.add(snapshot.key!!)
                }
                showUsers()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getFollowing() {
        val reference = FirebaseDatabase.getInstance().getReference("Follow")
            .child(id).child("following")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList.clear()
                for (snapshot : DataSnapshot in dataSnapshot.children){
                    idList.add(snapshot.key!!)
                }
                showUsers()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getLikes() {
        val reference = FirebaseDatabase.getInstance().getReference("Likes")
            .child(id)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList.clear()

                for (snapshot : DataSnapshot in dataSnapshot.children){
                    idList.add(snapshot.key!!)
                }
                showUsers()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun showUsers(){
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(eu.firebase.instagramapplication.model.UserData::class.java)!!
                    for (id in idList) {
                        //Adds only specific values to the array
                        if (user.id == id) {
                            userList.add(user)
                        }
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}