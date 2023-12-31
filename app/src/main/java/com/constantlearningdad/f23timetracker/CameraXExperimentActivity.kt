package com.constantlearningdad.f23timetracker

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.constantlearningdad.f23timetracker.databinding.ActivityCameraXexperimentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn.hasPermissions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Locale

class CameraXExperimentActivity : AppCompatActivity() {
    private lateinit var binding :ActivityCameraXexperimentBinding
    private lateinit var storage : FirebaseStorage
    private lateinit var reference : StorageReference
    private val auth = FirebaseAuth.getInstance()

    //if using the CameraController
    private lateinit var cameraController : LifecycleCameraController

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
                permissions ->

            var permissionGranted = true

            permissions.entries.forEach{
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }

            if (!permissionGranted)
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            else
                startCamera()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXexperimentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!hasPermissions(baseContext)){
            //Request camera-related permissions
            activityResultLauncher.launch(REQUIRED_PERMISSIONS)
        }
        else
            startCamera()

        binding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }

        //configure the Firebase Storage
        storage = FirebaseStorage.getInstance()
        reference = storage.reference


        setSupportActionBar(binding.mainToolBar.toolbar)
    }

    /**
     * Add the menu to the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_project -> {
//                startActivity(Intent(applicationContext, CreateProjectActivity::class.java))
                return true
            }
            R.id.action_log_time -> {
                startActivity(Intent(applicationContext, LogTimeActivity::class.java))
                return true
            }
            R.id.action_edit_profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
            R.id.action_view_summary -> {
//                startActivity(Intent(applicationContext, SummaryActivity::class.java))
                return true
            }
            R.id.action_selfie -> {
                startActivity(Intent(applicationContext, CameraXExperimentActivity::class.java))
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }

    private fun startCamera(){
        val previewView : PreviewView = binding.viewFinder
        cameraController = LifecycleCameraController(baseContext)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        previewView.controller = cameraController
    }

    private fun takePhoto() {
        //create time stamped name and MediaStore entry
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        //Create output options objects which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}",exception)
                }

                override fun onImageSaved(output : ImageCapture.OutputFileResults){
                    val uri = output.savedUri.toString()
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG,msg)

                    //upload a file to Firebase storage

                    //creating the name of the file and storage area on Firebase Storage
                    val userID = auth.currentUser!!.uid
                    val fileUri = "profilesImages/"+userID+"-"+uri.substringAfterLast("/")
                    Log.i(TAG, "fileUri: $fileUri")

                    //configure Firebase storage location
                    val profileImageRef = reference.child(fileUri)

                    output.savedUri?.let{uri->
                        val uploadTask = profileImageRef.putFile(uri)
                            .addOnSuccessListener {
                                Log.i(TAG,"Upload to Firebase Storage Success $uri")
                            }
                            .addOnFailureListener{
                                Log.i(TAG,"Upload to Firebase Storage failed for $uri")
                                Log.i(TAG, "${it.message}")
                            }

                        //get the URL to the image and store in the user's profile
                        uploadTask.continueWithTask {task ->
                                if (!task.isSuccessful){
                                    task.exception?.let {
                                        Log.i(TAG,"upload error ${it.message}")
                                    }
                                }
                                profileImageRef.downloadUrl
                            }.addOnCompleteListener {task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result



                                Log.i(TAG, "Task complete download URI: $downloadUri class: ${downloadUri.javaClass}")

                                val userDB = FirebaseFirestore.getInstance().collection("users").document(userID)
                                userDB.get().addOnSuccessListener { document ->
                                   document?.let{
                                       //convert the document to be a User object
                                       val user = document.toObject(User::class.java)
                                       user!!.profileImageURL = downloadUri.toString()
                                       userDB.set(user)
                                   }
                                }
                            }
                        }
                    }

                }
            }
        )
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                android.Manifest.permission.CAMERA
            )
                //the .apply is only required for older devices that have external storage
                .apply {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                        add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }.toTypedArray()

        fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}