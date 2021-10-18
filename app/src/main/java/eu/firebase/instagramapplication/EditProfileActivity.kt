package eu.firebase.instagramapplication

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.rengwuxian.materialedittext.MaterialEditText
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import eu.firebase.instagramapplication.model.UserData
import org.w3c.dom.Text

class EditProfileActivity : AppCompatActivity() {

    lateinit var close: ImageView
    lateinit var image_profile: ImageView
    lateinit var save: TextView
    lateinit var img_change: TextView

    lateinit var fullname: MaterialEditText
    lateinit var username: MaterialEditText
    lateinit var bio: MaterialEditText

    lateinit var firebaseUser: FirebaseUser

    lateinit var mImageUri: Uri
    lateinit var uploadTask: UploadTask
    lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        close = findViewById(R.id.close)
        image_profile = findViewById(R.id.image_profile)
        save = findViewById(R.id.save)
        img_change = findViewById(R.id.img_change)
        fullname = findViewById(R.id.fullname)
        username = findViewById(R.id.username)
        bio = findViewById(R.id.bio)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageReference = FirebaseStorage.getInstance().getReference("Uploads")

        val reference = FirebaseDatabase.getInstance().getReference("Users")
            .child(firebaseUser.uid)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserData::class.java)
                fullname.setText(user?.fullname)
                username.setText(user?.username)
                bio.setText(user?.bio)
                Glide.with(applicationContext).load(user?.imageUrl).into(image_profile)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        close.setOnClickListener {
            finish()
        }
        img_change.setOnClickListener {

            CropImage.activity().setAspectRatio(1,1)
                .start(this@EditProfileActivity)
        }
        save.setOnClickListener {
            updateProfile(fullname.text.toString(),
            username.text.toString(),bio.text.toString())
        }
    }

    /**User data update
     * @param fullname fullname editText value
     * @param username username editText value
     * @param bio bio editText value
     * @return Changes to the Firebase of the specified parameters
     */
    private fun updateProfile(fullname: String, username: String, bio: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Users")
            .child(firebaseUser.uid)

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["fullname"] = fullname
        hashMap["username"] = username
        hashMap["bio"] = bio

        reference.updateChildren(hashMap)
    }

    /**
     *
     */
    private fun getFileExtension(uri: Uri):String{
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri)).toString()
    }

    private fun uploadImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading")
        progressDialog.show()

        val fileReference = storageReference.child(System.currentTimeMillis().toString()
                + "." + getFileExtension(mImageUri))

        uploadTask = fileReference.putFile(mImageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!

            }
            return@continueWithTask fileReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful){
                val downloadUri = task.result
                val mUrl = downloadUri.toString()

                val reference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(firebaseUser.uid)
                val hashMap = HashMap<String, Any>()
                hashMap["imageUrl"] = mUrl
                reference.updateChildren(hashMap)

                progressDialog.dismiss()
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { task ->
            Toast.makeText(this, task.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            val result = CropImage.getActivityResult(data)
            mImageUri = result.uri

            uploadImage()
        }else{
            Toast.makeText(this, "Whops! Something gone wrong", Toast.LENGTH_SHORT).show()
        }
    }

}