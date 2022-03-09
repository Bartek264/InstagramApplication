package eu.firebase.instagramapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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
    var mNotification: ArrayList<NotificationData>
) : BaseAdapter() {

    private fun getUserInfo(imageView: ImageView, username: TextView, publisherId: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserData::class.java)
                Glide.with(mContext).load(user?.imageUrl).into(imageView)
                username.text = user?.username
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getPostImage(imageView: ImageView, postId: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(PostData::class.java)
                Glide.with(mContext).load(post?.postImage).into(imageView)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getCount(): Int {
        return mNotification.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val notification = mNotification[position]
        var itemView = convertView
        if (itemView == null) {
            itemView =
                LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false)
        }

        val image_profile: ImageView = itemView!!.findViewById(R.id.image_profile)
        val post_image: ImageView = itemView.findViewById(R.id.post_image)
        val username: TextView = itemView.findViewById(R.id.username)
        val texted: TextView = itemView.findViewById(R.id.text)

        texted.text = notification.text

        getUserInfo(image_profile, username, notification.userId)

        if (notification.post == "yes") {
            post_image.visibility = View.VISIBLE
            getPostImage(post_image, notification.postId)
        } else {
            image_profile.visibility = View.VISIBLE
        }



        itemView.setOnClickListener {
            if (notification.postId.isNotEmpty()) {
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("postId", notification.postId)
                editor.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    PostDetailFragment()
                ).commit()
            } else {

            }
        }


        return itemView
    }


}