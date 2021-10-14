package eu.firebase.instagramapplication.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.adapter.UserAdapter
import eu.firebase.instagramapplication.model.UserData
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var mUser: ArrayList<UserData>

    lateinit var search_bar : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v= inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        search_bar = v.findViewById(R.id.search_bar)
        userAdapter = UserAdapter(requireContext(), mUser)
        recyclerView.adapter = userAdapter

        readUsers()
        search_bar.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()){
                    readUsers()
                }else{
                    searchUser(s.toString().lowercase(Locale.getDefault()))
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        return v
    }

    private fun searchUser(s: String){
        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(s)
            .endAt(s+"\uf8ff")

        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(databaseSnapshot: DataSnapshot) {
                mUser.clear()
                for (snapshot:DataSnapshot in databaseSnapshot.children) {
                    val user = snapshot.getValue(UserData::class.java)!!

                    mUser.add(user)
                }

                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun readUsers(){
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (search_bar.text.toString().equals("")){
                    mUser.clear()
                    for (snapshot:DataSnapshot in dataSnapshot.children){
                        val user = snapshot.getValue(UserData::class.java)!!
                        mUser.add(user)
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}