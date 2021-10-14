package eu.firebase.instagramapplication.adapter

import android.content.Context
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
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.model.PostData
import eu.firebase.instagramapplication.model.UserData

class PostAdapter(
    var mContext : Context,
    var mPost : ArrayList<PostData>
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    lateinit var firebaseUser: FirebaseUser

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_img: ImageView
        var post_img: ImageView
        var like: ImageView
        var comment: ImageView
        var save: ImageView

        var username: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            super.itemView

            profile_img = itemView.findViewById(R.id.image_profiles)
            post_img = itemView.findViewById(R.id.post_image)
            like = itemView.findViewById(R.id.like)
            comment = itemView.findViewById(R.id.comment)
            save = itemView.findViewById(R.id.save)

            username = itemView.findViewById(R.id.username)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
        return PostAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val post = mPost[position]

        Glide.with(mContext).load(post.postImage).into(holder.post_img)

        if (post.description.isEmpty()){
            holder.description.visibility = View.GONE
        }else{
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.description
        }
        publisherInfo(holder.profile_img, holder.username, holder.publisher,post.postId)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    fun publisherInfo(image_profile: ImageView, username: TextView, publisher: TextView, userId: String){
        val reference = FirebaseDatabase.getInstance()

        reference.getReference("Users").orderByChild(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    val user: UserData? = snapshot.getValue(UserData::class.java)

                    Glide.with(mContext).load(user?.imageUrl).into(image_profile)
                    username.text = user?.username
                    publisher.text = user?.username
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}