package com.constantlearningdad.f23timetracker

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.constantlearningdad.f23timetracker.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //connect the scrollbars so they work
        binding.termsTextView.movementMethod=ScrollingMovementMethod()

        //get the user information

        if (auth.currentUser == null)
        {
//            logout()
        }
        else {
            auth.currentUser?.let { user ->
                binding.userNameTextView.text = user.displayName
                binding.emailTextView.text = user.email

                //get the user image and populate it in the imageview
                val userDB = FirebaseFirestore.getInstance().collection("users").document(user.uid)
                userDB.get().addOnSuccessListener { document ->
                    document?.let {
                        //convert the document to be a User object
                        val user = document.toObject(User::class.java)
                        if (user!!.profileImageURL != null) {
                            val urlToProfileImage = user!!.profileImageURL.toString()

                            val imageRef =
                                FirebaseStorage.getInstance().getReferenceFromUrl(urlToProfileImage)

                            imageRef.getBytes(10 * 1024 * 1024)
                                .addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                                    binding.imageView.setImageBitmap(bitmap)
                                    binding.imageView.rotation = -90F
                                }
                                .addOnFailureListener {
                                    Log.i("CameraXApp", "bitmap download failed ${it.message}")
                                }
                        }
                    }
                }
            }
        }
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
                startActivity(Intent(applicationContext, CreateProjectActivity::class.java))
                return true
            }
            R.id.action_log_time -> {
                startActivity(Intent(applicationContext, LogTimeActivity::class.java))
                return true
            }
            R.id.action_edit_profile -> {
//                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
            R.id.action_view_summary -> {
                startActivity(Intent(applicationContext, SummaryActivity::class.java))
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout()
    {
        auth.signOut()
        finish()
        startActivity(Intent(this, SigninActivity::class.java))
    }

}