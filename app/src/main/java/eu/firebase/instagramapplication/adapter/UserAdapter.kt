package eu.firebase.instagramapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.ContentInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import eu.firebase.instagramapplication.MainActivity
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.fragment.ProfileFragment
import eu.firebase.instagramapplication.model.NotificationData
import eu.firebase.instagramapplication.model.UserData
import java.util.ArrayList

class UserAdapter(val mContext: Context, val mUser: ArrayList<UserData>, var isFragment: Boolean):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    lateinit var firebaseUser: FirebaseUser

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username : TextView
        var fullname : TextView
        var image_profile : CircleImageView
        var btn_follow : Button

        init{

            username = itemView.findViewById(R.id.username)
            fullname = itemView.findViewById(R.id.fullname)
            image_profile = itemView.findViewById(R.id.image_profile)
            btn_follow = itemView.findViewById(R.id.btn_follow)
        }
    }

    /** Followers database
     * @param userId Current user ID taken from Firebase
     * @param btn "Follow" action button
     * @return Assigning who is following to a specific user and saving it in Firebase
     */

    fun following(userId: String, btn: Button){
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser.uid).child("following")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(userId).exists()){
                    btn.text = "following"
                }else{
                    btn.text = "follow"
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val user = mUser[position]

        holder.btn_follow.visibility = View.VISIBLE
        holder.username.setText(user.username)
        holder.fullname.setText(user.email)
        Glide.with(mContext).load(user.imageUrl).into(holder.image_profile)
        following(user.id, holder.btn_follow)

        //Blocking the possibility of self-follower
        if (user.id == firebaseUser.uid){
            holder.btn_follow.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (isFragment){
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileid", user.id)
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    ProfileFragment()
                ).commit()
            }else{
                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherid", user.id)
                mContext.startActivity(intent)
            }

        }


        holder.btn_follow.setOnClickListener {
            if (holder.btn_follow.text == "follow"){
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following").child(user.id).setValue(true)
                FirebaseDatabase.getInstance().reference.child("Follow").child(user.id)
                    .child("followers").child(firebaseUser.uid).setValue(true)

                addNotification(user.id)
            }else{
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following").child(user.id).removeValue()
                FirebaseDatabase.getInstance().reference.child("Follow").child(user.id)
                    .child("followers").child(firebaseUser.uid).removeValue()
            }
        }

    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    private fun addNotification(userId: String){

        FirebaseDatabase.getInstance().getReference("Notifications").child(userId).push()
            .setValue(NotificationData(userId = firebaseUser.uid,"started following you","", "yes"))

    }
}