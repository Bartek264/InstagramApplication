package eu.firebase.instagramapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.firebase.instagramapplication.MainActivity
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.model.Comment
import eu.firebase.instagramapplication.model.UserData
import org.w3c.dom.Text

class CommentAdapter(
    var mContext: Context,
    var mComment: ArrayList<Comment>
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    lateinit var firebaseUser: FirebaseUser

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image_profile: ImageView
        var username : TextView
        var comment: TextView

        init {
            super.itemView

            image_profile = itemView.findViewById(R.id.image_profile)
            username = itemView.findViewById(R.id.username)
            comment = itemView.findViewById(R.id.comment)

        }
    }

    private fun getUserInfo(imageView: ImageView, username: TextView, publisherId: String){

        val reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherId)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserData::class.java)!!
                Glide.with(mContext).load(user.imageUrl).into(imageView)
                username.text = user.username

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val comment = mComment[position]
        holder.comment.setText(comment.comment)
        getUserInfo(holder.image_profile, holder.username, comment.publisherId)

        holder.comment.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherId", comment.publisherId)
            mContext.startActivity(intent)
        }
        holder.image_profile.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherId", comment.publisherId)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mComment.size
    }
}