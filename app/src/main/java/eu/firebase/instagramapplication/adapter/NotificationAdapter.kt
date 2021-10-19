package eu.firebase.instagramapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.fragment.NotificationFragment
import eu.firebase.instagramapplication.fragment.PostDetailFragment
import eu.firebase.instagramapplication.fragment.ProfileFragment
import eu.firebase.instagramapplication.model.NotificationData
import eu.firebase.instagramapplication.model.PostData
import eu.firebase.instagramapplication.model.UserData

class NotificationAdapter(
    var mContext: Context,
    var mNotification: ArrayList<NotificationData>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var image_profile: ImageView
        var post_image: ImageView
        var username: TextView
        var text: TextView

        init {
            super.itemView

            image_profile = itemView.findViewById(R.id.image_profile)
            post_image = itemView.findViewById(R.id.post_image)
            username = itemView.findViewById(R.id.username)
            text = itemView.findViewById(R.id.comment)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotification[position]
        holder.text.text = notification.text

        getUserInfo(holder.image_profile, holder.username, notification.userId)

        if (notification.post){
            holder.post_image.visibility = View.VISIBLE
            getPostImage(holder.post_image, notification.postId)
        }else{
            holder.image_profile.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (notification.post){
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("postId", notification.postId)
                editor.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    PostDetailFragment()
                ).commit()
            }else{
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileId", notification.userId)
                editor.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    ProfileFragment()
                ).commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    private fun getUserInfo(imageView: ImageView, username: TextView, publisherId: String){
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserData::class.java)
                Glide.with(mContext).load(user?.imageUrl).into(imageView)
                username.text = user?.username
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getPostImage(imageView: ImageView, postId: String){
        val reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(PostData::class.java)
                Glide.with(mContext).load(post?.postImage).into(imageView)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}