package com.aqsa.tabianconsulting

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class SignedInActivity : AppCompatActivity() {

    private val TAG = SignedInActivity::class.java.name

    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in)
        setupFirebaseAuth()
        getUserDetails()
        setUserDetails()
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticationState()
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        mAuthListener?.let {
            FirebaseAuth.getInstance().removeAuthStateListener(it)
        }
    }

    private fun checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        val user = FirebaseAuth.getInstance().currentUser
        when(user) {
            null -> {
                val intent = Intent(this, LoginActivity::class.java)
                        .apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                startActivity(intent)
                finish()
            }
            else -> {
                Log.d(TAG, "checkAuthenticationState: user is authenticated.");
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.optionSignOut -> { signOut(); true
            }

            R.id.optionSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun signOut() {
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut()
    }

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.")
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            when(user) {
                null -> {
                    Log.d(TAG, "onAuthStateChanged:signed_out")
                    val intent = Intent(this, LoginActivity::class.java)
                            .apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid);
                }
            }
        }
    }

    private fun setUserDetails() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val profileUpdates = UserProfileChangeRequest.Builder().apply {
                setDisplayName("Dika Budiaji")
                setPhotoUri(Uri.parse("https://avatars2.githubusercontent.com/u/8433116?s=460&v=4"))
            }.build()
            it.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) Log.d(TAG, "onComplete: User profile updated")
                        else Log.d(TAG, "onCompelete: User not updated")
                    }
        }
    }

    private fun getUserDetails() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {

        }
    }

}
