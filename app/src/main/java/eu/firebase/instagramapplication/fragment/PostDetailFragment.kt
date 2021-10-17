package eu.firebase.instagramapplication.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.adapter.PostAdapter
import eu.firebase.instagramapplication.model.PostData

class PostDetailFragment : Fragment() {

    lateinit var postId: String

    lateinit var postAdapter: PostAdapter
    lateinit var postList: ArrayList<PostData>
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_post_detail, container, false)

        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        postId = preferences?.getString("postId", "none")!!

        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        postList = ArrayList()
        postAdapter = PostAdapter(requireContext(), postList)
        recyclerView.adapter = postAdapter

        readPost()

        return v
    }

    private fun readPost() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                val post = dataSnapshot.getValue(PostData::class.java)!!
                postList.add(post)

                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}