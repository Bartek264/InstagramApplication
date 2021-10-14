package eu.firebase.instagramapplication

import android.accounts.AuthenticatorDescription
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import eu.firebase.instagramapplication.model.PostData
import org.w3c.dom.Text

class PostActivity : AppCompatActivity() {

    lateinit var imageUri: Uri
    lateinit var myUri: String
    lateinit var uploadTask: UploadTask
    lateinit var storageReference: StorageReference

    lateinit var close : ImageView
    lateinit var img_added: ImageView
    lateinit var post : TextView
    lateinit var description: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        close = findViewById(R.id.close)
        img_added = findViewById(R.id.image_added)
        post = findViewById(R.id.post)
        description = findViewById(R.id.description)

        storageReference = FirebaseStorage.getInstance().getReference("posts")

        close.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        post.setOnClickListener {
            uploadImage()
        }

        CropImage.activity()
            .setAspectRatio(1, 1)
            .start(this@PostActivity)
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Posting")
        progressDialog.show()

        if (imageUri != null){
            val fileReference = storageReference.child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri))

            uploadTask = fileReference.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful){
                    throw task.exception!!
                }
                return@continueWithTask fileReference.downloadUrl
            }.addOnCompleteListener { task->
                if (task.isSuccessful){
                    val downloadUri: Uri = task.result!!

                    myUri = downloadUri.toString()

                    val reference = FirebaseDatabase.getInstance().getReference("Posts")
                    val postId = reference.push().key!!

                    //Create a database for the post
                    FirebaseDatabase.getInstance().getReference("Posts").child(postId)
                        .setValue(PostData(myUri, description.text.toString(),FirebaseAuth.getInstance().currentUser!!.uid))

                    //ProgressDialog off
                    progressDialog.dismiss()

                    startActivity(Intent(this@PostActivity, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getFileExtension(uri: Uri):String{
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri)).toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            imageUri = result.uri

            img_added.setImageURI(imageUri)
        }else{
            Toast.makeText(this, "Something went wrong, please try again later", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(this@PostActivity, MainActivity::class.java))
            finish()
        }
    }
}