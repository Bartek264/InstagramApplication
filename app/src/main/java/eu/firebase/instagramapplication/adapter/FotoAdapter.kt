package eu.firebase.instagramapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.firebase.instagramapplication.R
import eu.firebase.instagramapplication.fragment.PostDetailFragment
import eu.firebase.instagramapplication.model.PostData

class FotoAdapter(
    var context: Context,
    var mPost: ArrayList<PostData>
) : RecyclerView.Adapter<FotoAdapter.ViewHolder>() {



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var post_image: ImageView
        init {
            super.itemView
            post_image = itemView.findViewById(R.id.post_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.foto_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPost[position]

        Glide.with(context).load(post.postImage).into(holder.post_image)

        holder.post_image.setOnClickListener {
            val editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.postId)
            editor.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                PostDetailFragment()
            ).commit()
        }

    }

    override fun getItemCount(): Int {
        return mPost.size
    }

}