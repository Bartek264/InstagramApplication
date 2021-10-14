package eu.firebase.instagramapplication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.adapter.PostAdapter
import eu.firebase.instagramapplication.model.PostData
import eu.firebase.instagramapplication.model.UserData

class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var postAdapter: PostAdapter
    lateinit var postList: ArrayList<PostData>

    lateinit var followingList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        postList = ArrayList()
        postAdapter = PostAdapter(requireContext(), postList)
        recyclerView.adapter = postAdapter

        checkFollowing()

        return v
    }

    fun checkFollowing(){
        followingList = ArrayList()

        val reference = FirebaseDatabase.getInstance().getReference("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("following")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followingList.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    followingList.add(snapshot.key!!)
                }

                readPosts()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun readPosts(){
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    val post = snapshot.getValue(PostData::class.java)
                    for (id: String in followingList){
                        if (post?.publisher == id){
                            postList.add(post)
                        }
                    }
                }
                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}