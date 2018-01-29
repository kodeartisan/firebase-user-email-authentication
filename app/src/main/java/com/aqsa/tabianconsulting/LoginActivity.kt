package com.aqsa.tabianconsulting

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.name

    private val mEmail: EditText? by lazy { findViewById<EditText>(R.id.email) }
    private val mPassword: EditText? by lazy { findViewById<EditText>(R.id.password) }
    private val mProgressBar: ProgressBar? by lazy { findViewById<ProgressBar>(R.id.progressBar) }
    private val mSignin: Button? by lazy { findViewById<Button>(R.id.email_sign_in_button) }
    private val mRegister: TextView? by lazy { findViewById<TextView>(R.id.link_register) }
    private val mResendVerificationEmail: TextView? by lazy { findViewById<TextView>(R.id.resend_verification_email) }

    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initListener()
        setupFirebaseAuth()
    }

    private fun initListener() {
        mSignin?.setOnClickListener{
             FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail?.text.toString(), mPassword?.text.toString())
                     .addOnCompleteListener {   }
                     .addOnFailureListener { Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG) }
        }
        mRegister?.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        mResendVerificationEmail?.setOnClickListener { v: View? ->
            val dialog = ResendVerificationDialog()
            dialog.show(supportFragmentManager, "dialog_resend_email_verification")
        }
    }

    private fun setupFirebaseAuth() {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            var user = firebaseAuth.currentUser
            when(user) {
                null -> {
                    Log.d(TAG, "null onAuthStateChanged: signed_out")
                }
                else -> {
                     when(user.isEmailVerified) {
                         true -> {
                             Log.d(TAG, "onAuthStateChanged:signed_in"+ user.uid)
                             Toast.makeText(this, "Authenticatd with: "+ user.email, Toast.LENGTH_LONG).show()

                              val intent = Intent(this, SignedInActivity::class.java)
                             startActivity(intent)
                             finish()
                         }

                         false -> {
                             Toast.makeText(this, "Check your email to verification "+ user.email, Toast.LENGTH_LONG).show()
                             //FirebaseAuth.getInstance().signOut()
                         }
                     }

                }
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
