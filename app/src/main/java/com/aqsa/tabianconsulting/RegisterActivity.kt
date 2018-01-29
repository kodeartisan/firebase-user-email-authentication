package com.aqsa.tabianconsulting

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by kodeartisan on 1/27/18.
 */
class RegisterActivity: AppCompatActivity() {

    private val TAG = RegisterActivity::class.java.name

    private val mEmail: EditText? by lazy { findViewById<EditText>(R.id.input_email) }
    private val mPassword: EditText? by lazy { findViewById<EditText>(R.id.input_password) }
    private val mConfirmPassword: EditText? by lazy { findViewById<EditText>(R.id.input_confirm_password) }
    private val mProgressBar: ProgressBar? by lazy { findViewById<ProgressBar>(R.id.progressBar) }
    private val mBtnRegister: Button? by lazy { findViewById<Button>(R.id.btn_register) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initListener()
    }

    private fun initListener() {
        mBtnRegister?.setOnClickListener{
             registerNewEmail(mEmail?.text.toString(), mPassword?.text.toString())
        }
    }

    private fun registerNewEmail(email: String, password: String) {
        showDialog()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            Log.d(TAG, "onComplete: ${task.isSuccessful}")

            when(task.isSuccessful) {
                true -> {
                    Log.d(TAG, "onComplete: AuthState: ${FirebaseAuth.getInstance().currentUser?.uid} ")
                    sendVerificationEmail()
                    FirebaseAuth.getInstance().signOut()
                    redirectLoginScreen()
                }
                false -> {
                    Toast.makeText(this, "Unable to register", Toast.LENGTH_LONG)
                }
            }

            hideDialog()

        }
    }


    private fun hideSoftKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun sendVerificationEmail() {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    when(task.isSuccessful) {
                        true -> Toast.makeText(this, "Send verification email", Toast.LENGTH_LONG).show()
                        false -> Toast.makeText(this, "Couln't send verification email", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun redirectLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showDialog() {
        mProgressBar?.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        if(mProgressBar?.visibility == View.VISIBLE) {
            mProgressBar?.visibility = View.VISIBLE
        }
    }

}