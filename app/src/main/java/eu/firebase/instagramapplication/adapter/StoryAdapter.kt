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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.model.StoryData
import eu.firebase.instagramapplication.model.UserData

class StoryAdapter(
    var mContext: Context,
    var mStory: ArrayList<StoryData>
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var story_photo: ImageView
        var story_plus: ImageView
        var story_photo_seen: ImageView

        var story_username: TextView
        var addstory_text: TextView

        init {
            super.itemView

            story_photo = itemView.findViewById(R.id.story_photo)
            story_username = itemView.findViewById(R.id.story_username)
            story_plus = itemView.findViewById(R.id.story_plus)
            addstory_text = itemView.findViewById(R.id.addstory_text)
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 0){
            val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent, false)
            return StoryAdapter.ViewHolder(view)
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false)
        return StoryAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = mStory[position]

        userInfo(holder, story.userId, position)

        if (holder.adapterPosition != 0){
            seenStory(holder, story.userId)
        }
        if (holder.adapterPosition == 0){
            myStory(holder.addstory_text, holder.story_plus, false)
        }

        holder.itemView.setOnClickListener {
            if (holder.adapterPosition == 0){
                myStory(holder.addstory_text, holder.story_plus, true)
            }else{

            }
        }
    }

    override fun getItemCount(): Int {
        return mStory.size
    }

    private fun userInfo(viewHolder: ViewHolder, userId: String, pos: Int){
        val reference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserData::class.java)
                Glide.with(mContext).load(user?.imageUrl).into(viewHolder.story_photo_seen)
                if (pos != 0){
                    Glide.with(mContext).load(user?.imageUrl).into(viewHolder.story_photo)
                    viewHolder.story_username.text = user?.username
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun myStory(textView: TextView, imageView: ImageView, click: Boolean){
        val reference = FirebaseDatabase.getInstance().getReference("Story")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var count :Int = 0
                val timeCurrent = System.currentTimeMillis()
                for (snapshot in dataSnapshot.children){
                    val story = snapshot.getValue(StoryData::class.java)!!
                    if (timeCurrent > story.timeStart && timeCurrent < story.timeEnd){
                        count++
                    }
                }
                if (click){
                    TODO("Alert dialog")
                }else{
                    if (count > 0){
                        textView.text = "My story"
                        imageView.visibility = View.GONE
                    }else{
                        textView.text = "Add story"
                        imageView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun seenStory(viewHolder: ViewHolder, userId: String){
        val reference = FirebaseDatabase.getInstance().getReference("Story")
            .child(userId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i = 0
                for (snapshot in dataSnapshot.children){
                    if (!snapshot.child("views")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .exists() && System.currentTimeMillis() < snapshot.getValue(StoryData::class.java)
                            !!.timeEnd
                    ) {
                        i++
                    }
                }
                if (i > 0){
                    viewHolder.story_photo.visibility = View.VISIBLE
                    viewHolder.story_photo_seen.visibility = View.GONE
                }else{
                    viewHolder.story_photo.visibility = View.GONE
                    viewHolder.story_photo_seen.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0){
            return 0
        }
        return 1
    }

}