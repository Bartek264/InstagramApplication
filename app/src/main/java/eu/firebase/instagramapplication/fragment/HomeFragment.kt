package eu.firebase.instagramapplication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import eu.firebase.instagramapplication.adapter.StoryAdapter
import eu.firebase.instagramapplication.model.PostData
import eu.firebase.instagramapplication.model.StoryData
import eu.firebase.instagramapplication.model.UserData

class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var postAdapter: PostAdapter
    lateinit var postList: ArrayList<PostData>

    lateinit var recyclerViewStory: RecyclerView
    lateinit var storyAdapter: StoryAdapter
    lateinit var storyList: ArrayList<StoryData>

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
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        postList = ArrayList()
        postAdapter = PostAdapter(requireContext(), postList)
        recyclerView.adapter = postAdapter

        recyclerViewStory = v.findViewById(R.id.recycler_view_story)
        recyclerViewStory.setHasFixedSize(true)
        val linearLayoutManagerStory = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewStory.layoutManager = linearLayoutManagerStory
        storyList = ArrayList()
        storyAdapter =StoryAdapter(requireContext(), storyList)
        recyclerViewStory.adapter = storyAdapter

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
                //readStory()
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

    private fun readStory(){
        val reference = FirebaseDatabase.getInstance().getReference("Story")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val timeCurrent = System.currentTimeMillis()
                storyList.clear()
                storyList.add(StoryData("",0,0,"", FirebaseAuth.getInstance().currentUser!!.uid))
                for (id in followingList){
                    var countStory = 0
                    var story : StoryData? = null
                    for (snapshot in dataSnapshot.child(id).children){
                        story = snapshot.getValue(StoryData::class.java)!!
                        if (timeCurrent > story.timeStart && timeCurrent < story.timeEnd){
                            countStory++
                        }
                    }
                    if (countStory > 0){
                        storyList.add(story!!)
                    }
                }
                storyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}