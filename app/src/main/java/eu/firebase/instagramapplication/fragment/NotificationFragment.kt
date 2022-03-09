package eu.firebase.instagramapplication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.adapter.NotificationAdapter
import eu.firebase.instagramapplication.model.NotificationData
import kotlin.collections.ArrayList

class NotificationFragment : Fragment() {

    lateinit var recyclerView: ListView
    lateinit var notificationAdapter: NotificationAdapter
    lateinit var notificationList: ArrayList<NotificationData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerView = v.findViewById(R.id.list_view)

        notificationList = ArrayList()
        notificationAdapter = NotificationAdapter(requireContext(), notificationList)
        recyclerView.adapter = notificationAdapter

        readNotification()

        return v
    }

    private fun readNotification() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Notifications")
            .child(firebaseUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                notificationList.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children){
                    val notification = snapshot.getValue(NotificationData::class.java)!!
                    notificationList.add(notification)
                }


                notificationList.reverse()
                notificationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}