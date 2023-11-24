package com.constantlearningdad.f23timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SigninActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = Firebase.auth

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.time_tracker_logo)
            .build()
        signInLauncher.launch(signInIntent)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserProfile()
            startActivity(Intent(this, CreateProjectActivity::class.java))
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            checkUserProfile()
            val intent = Intent(this, CreateProjectActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(this, "Sigin failed", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SigninActivity::class.java))
        }
    }

    private fun checkUserProfile()
    {
        val user = FirebaseAuth.getInstance().currentUser

        //connect with FirebaseFirestore to see if this user has a profile created already
        val userDB = FirebaseFirestore.getInstance().collection("users").document(user!!.uid)
        userDB.get().addOnSuccessListener { document ->
                if (document.data == null)  //the user does not have a profile
                {
                    val newUser = User(userID = user!!.uid)
                    userDB.set(newUser)
                    Log.i("CameraXApp","user profile created")
                }
                else
                {
                    Log.i("CameraXApp","user profile already existed")
                }
            }
            .addOnFailureListener {
                Log.d("CameraXApp", "${it.message}")
            }
    }
}