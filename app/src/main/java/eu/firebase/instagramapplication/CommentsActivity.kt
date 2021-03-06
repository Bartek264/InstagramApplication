package eu.firebase.instagramapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import eu.firebase.instagramapplication.adapter.CommentAdapter
import eu.firebase.instagramapplication.model.*
import java.util.HashMap

class CommentsActivity() : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var commentAdapter: CommentAdapter
    lateinit var commentList: ArrayList<Comment>

    lateinit var back: ImageView

    lateinit var addcomment: EditText
    lateinit var image_profile: ImageView
    lateinit var post: TextView

    lateinit var postId: String
    lateinit var publishedId: String

    lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        back = findViewById(R.id.back)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.hasFixedSize()
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter

        addcomment = findViewById(R.id.add_comment)
        image_profile = findViewById(R.id.image_profile)
        post = findViewById(R.id.post)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publishedId = intent.getStringExtra("publisherId").toString()

        back.setOnClickListener {
            finish()
        }

        post.setOnClickListener {
            if (addcomment.text.toString() == ""){
                Toast.makeText(this, "You can't send empty comment", Toast.LENGTH_SHORT).show()
            }else{
                addComment(postId, publishedId)
            }
        }
        getImage()
        readComments()
    }

    private fun addComment(post: String, publisher: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Comments").child(post)
        val commentid = reference.push().key

        FirebaseDatabase.getInstance().getReference("Comments").child(post).push()
            .setValue(CommentData(commentid!!,addcomment.text.toString(),firebaseUser.uid))
            .addOnCompleteListener {}

        addNotification()
        addcomment.setText("")
    }

    private fun getImage(){
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserData::class.java)
                Glide.with(applicationContext).load(user?.imageUrl).into(image_profile)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun readComments(){
        val reference = FirebaseDatabase.getInstance().getReference("Comments")
            .child(postId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    val comment = snapshot.getValue(Comment::class.java)!!
                    commentList.add(comment)
                }
                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun addNotification(){
        FirebaseDatabase.getInstance().getReference("Notifications").child(publishedId).push()
            .setValue(NotificationData(userId = firebaseUser.uid,"commented " + addcomment.text.toString()
                ,postId, "yes"))

    }
}