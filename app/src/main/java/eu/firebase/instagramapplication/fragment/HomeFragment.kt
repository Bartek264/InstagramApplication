package eu.firebase.instagramapplication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.adapter.PostAdapter
import eu.firebase.instagramapplication.model.PostData

class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var postAdapter: PostAdapter
    lateinit var postList: ArrayList<PostData>

    lateinit var progressBar: ProgressBar

    lateinit var followingList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        progressBar = v.findViewById(R.id.progress_circular)

        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = mLayoutManager
        postList = ArrayList()
        postAdapter = PostAdapter(requireContext(), postList)
        recyclerView.adapter = postAdapter

        checkFollowing()

        return v
    }

    private fun checkFollowing(){
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

    private fun readPosts(){
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()

                for (snapshot: DataSnapshot in dataSnapshot.children){
                    val post = snapshot.getValue(PostData::class.java)!!

                    for (id: String in followingList){
                        if (post.publisher == id){
                            postList.add(post)
                        }
                    }
                }
                postAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}