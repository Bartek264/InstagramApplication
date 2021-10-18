package eu.firebase.instagramapplication.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.service.autofill.UserData
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.firebase.instagramapplication.EditProfileActivity
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.adapter.FotoAdapter
import eu.firebase.instagramapplication.model.PostData
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {

    lateinit var image_profile: ImageView
    lateinit var options: ImageView

    lateinit var posts: TextView
    lateinit var followers: TextView
    lateinit var following: TextView
    lateinit var fullname: TextView
    lateinit var bio: TextView
    lateinit var username: TextView
    lateinit var edit_profile: TextView

    lateinit var recyclerView: RecyclerView
    lateinit var fotoAdapter: FotoAdapter
    lateinit var postList: ArrayList<PostData>

    lateinit var my_fotos: ImageButton
    lateinit var saved_fotos: ImageButton

    lateinit var firebaseUser: FirebaseUser
    lateinit var profileId: String

    lateinit var mySaves: ArrayList<String>
    lateinit var recyclerViewSaves: RecyclerView
    lateinit var fotoAdapterSaves: FotoAdapter
    lateinit var postListSaves: ArrayList<PostData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        val prefs: SharedPreferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)!!

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        //Getting information from the UserAdapter
        profileId = prefs.getString("profileid", "none")!!

        image_profile = v.findViewById(R.id.image_profile)
        options = v.findViewById(R.id.options)
        posts = v.findViewById(R.id.posts)
        followers = v.findViewById(R.id.followers)
        following = v.findViewById(R.id.following)
        fullname = v.findViewById(R.id.fullname)
        bio = v.findViewById(R.id.bio)
        username = v.findViewById(R.id.username)
        edit_profile = v.findViewById(R.id.edit_profile)
        my_fotos = v.findViewById(R.id.my_fotos)
        saved_fotos = v.findViewById(R.id.saved_fotos)

        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = linearLayoutManager
        postList = ArrayList()
        fotoAdapter = FotoAdapter(requireContext(), postList)
        recyclerView.adapter = fotoAdapter

        recyclerViewSaves = v.findViewById(R.id.recycler_view_save)
        recyclerViewSaves.setHasFixedSize(true)
        val linearLayoutManagerSaves = GridLayoutManager(context, 3)
        recyclerViewSaves.layoutManager = linearLayoutManagerSaves
        postListSaves = ArrayList()
        fotoAdapterSaves = FotoAdapter(requireContext(), postListSaves)
        recyclerViewSaves.adapter = fotoAdapterSaves

        recyclerView.visibility = View.VISIBLE
        recyclerViewSaves.visibility = View.GONE

        userInfo()
        getFollowers()
        nrPosts()
        myFotos()
        mySaves()

        if (profileId == firebaseUser.uid){
            edit_profile.text = "Edit profile"
        }else{
            checkFollow()
            saved_fotos.visibility = View.GONE
        }

        edit_profile.setOnClickListener {
            if (edit_profile.text.toString() == "Edit profile"){
                val intent = Intent(context, EditProfileActivity::class.java)
                startActivity(intent)
            }else if (edit_profile.text.toString() == "follow"){
                //After pressing, add a record to the database
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following").child(profileId).setValue(true)
                FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                    .child("followers").child(firebaseUser.uid).setValue(true)
            }else if (edit_profile.text.toString() == "following"){
                //After pressing, delete the record from the database
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following").child(profileId).removeValue()
                FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                    .child("followers").child(firebaseUser.uid).removeValue()
            }
        }

        my_fotos.setOnClickListener {
            recyclerView.visibility = View.VISIBLE
            recyclerViewSaves.visibility = View.GONE
        }
        saved_fotos.setOnClickListener {
            recyclerView.visibility = View.GONE
            recyclerViewSaves.visibility = View.VISIBLE
        }
        return v
    }

    /**Retrieving user information
     * @return username, fullname, image_profile
     */
    private fun userInfo(){
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(eu.firebase.instagramapplication.model.UserData::class.java)
                username.text = user?.username
                fullname.text = user?.fullname
                bio.text = user?.bio
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    Glide.with(context!!).load(user?.imageUrl).into(image_profile)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /**Checking whether the user has already liked the profile
     * @return sets the text for the edit_profile button
     */
    private fun checkFollow(){
        val reference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser.uid).child("following")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(profileId).exists()){
                    edit_profile.text = "following"
                }else{
                    edit_profile.text = "follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /**Data on followers
     * @return setting the following counter and followers
     */
    private fun getFollowers(){
        val reference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId).child("followers")
        val references = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId).child("following")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followers.text = dataSnapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        references.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                following.text = dataSnapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /**Counter of the number of posts
     * @return Number of published posts
     */
    private fun nrPosts(){
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i = 0
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    val post = snapshot.getValue(PostData::class.java)
                    if (post?.publisher == profileId){
                        i++
                    }
                }
                posts.text = i.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /**Checking how many posts a given user has
     * @return number of posts by a given user
     */
    private fun myFotos(){
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    val post = snapshot.getValue(PostData::class.java)!!
                    if (post.publisher == profileId){
                        postList.add(post)
                    }
                }
                postList.reverse()
                fotoAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /**Create a list of saved posts
     * @return reads postId from Saves -> UserId
     */
    private fun mySaves(){
        mySaves = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Saves")
            .child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    mySaves.add(snapshot.key!!)
                }
                readSaves()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun readSaves() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postListSaves.clear()
                for (snapshot:DataSnapshot in dataSnapshot.children){
                    val post = snapshot.getValue(PostData::class.java)!!
                    for (id: String in mySaves){
                        if (post.postId == id){
                            postListSaves.add(post)
                        }
                    }
                }
                fotoAdapterSaves.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}