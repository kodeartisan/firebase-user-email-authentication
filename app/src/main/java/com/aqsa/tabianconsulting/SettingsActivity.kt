package com.aqsa.tabianconsulting

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast



class SettingsActivity : AppCompatActivity() {

    private val TAG = SettingsActivity::class.java.name

    //private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    private val mEmail: EditText? by lazy { findViewById<EditText>(R.id.input_email) }
    private val mCurrentPassword: EditText? by lazy { findViewById<EditText>(R.id.input_password) }
    private val mSaveBtn: Button? by lazy { findViewById<Button>(R.id.btn_save) }
    private val mProgressBar: ProgressBar? by lazy { findViewById<ProgressBar>(R.id.progressBar) }
    private val mResetPasswordLink: TextView? by lazy { findViewById<TextView>(R.id.change_password) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupFirebaseAuth()

        mSaveBtn?.setOnClickListener { v ->
            editUserEmail()
        }
        mResetPasswordLink?.setOnClickListener {
            Log.d(TAG, "onClick: sending password reset link")
             /*
                ------ Reset Password Link -----
                */
                setResetPasswordLink()
        }
    }

    private fun setResetPasswordLink() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser?.email!!)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Log.d(TAG, "onComplete: Password Reset Email sent.");
                            Toast.makeText(this, "Sent Password Reset Link to Email",
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onComplete: No user associated with that email.");

                            Toast.makeText(this, "No User Associated with that Email.",
                                    Toast.LENGTH_SHORT).show();
                    }
                }
    }

    private fun editUserEmail() {
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        val email = FirebaseAuth.getInstance().currentUser?.email
        var password = mCurrentPassword?.text.toString()
        val credential = EmailAuthProvider.getCredential(email!!, password)
        Log.d(TAG, "editUserEmail: reauthenticating with:  \n email " + FirebaseAuth.getInstance().currentUser!!.email
                + " \n passowrd: " + mCurrentPassword?.text.toString())

        FirebaseAuth.getInstance().currentUser?.reauthenticate(credential)
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Log.d(TAG, "onComplete: reauthenticate success.")
                        ///////////////////now check to see if the email is not already present in the database
                        FirebaseAuth.getInstance().fetchProvidersForEmail(mEmail?.text.toString())
                                .addOnCompleteListener { task ->
                                    if(task.isSuccessful)
                                    ///////// getProviders().size() will return size 1 if email ID is in use.
                                    Log.d(TAG, "onComplete: RESULT: " + task.result.providers?.size)
                                    if(task.result.providers?.size == 1) {
                                        Log.d(TAG, "onComplete: That email is already in use.")
                                        Toast.makeText(this, "That email is already in use", Toast.LENGTH_LONG).show()
                                    } else {
                                        Log.d(TAG, "onComplete: That email is available.")
                                        /////////////////////add new email
                                        FirebaseAuth.getInstance().currentUser?.updateEmail(mEmail?.text.toString())
                                                ?.addOnCompleteListener { task ->
                                                    if(task.isSuccessful) {
                                                        Log.d(TAG, "onComplete: User email address updated.")
                                                        Toast.makeText(this, "Updated email", Toast.LENGTH_LONG)

                                                        sendVerificationEmail()
                                                        FirebaseAuth.getInstance().signOut()
                                                    } else {
                                                        Log.d(TAG, "could not updated email")
                                                        Toast.makeText(this, "unable to update email", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                ?.addOnFailureListener { exception ->
                                                    Toast.makeText(this, "Unable to update email", Toast.LENGTH_LONG)
                                                }
                                    }
                                }
                    }
                }
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            it.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) Toast.makeText(this, "Sent Verification Email", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this, "Couldn't Verification Send Email", Toast.LENGTH_SHORT).show();
                    }
        }
    }


    override fun onResume() {
        super.onResume()
        checkAuthenticationState()
    }

    private fun setCurrentEmail() {
        Log.d(TAG, "setCurrentEmail")

        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            val email = it.email
            mEmail?.setText(email)
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

    private fun setupFirebaseAuth() {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if(firebaseAuth.currentUser != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseAuth.uid)
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
                Toast.makeText(this, "Signed out", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        mAuthListener.let { FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener)}
    }

}
