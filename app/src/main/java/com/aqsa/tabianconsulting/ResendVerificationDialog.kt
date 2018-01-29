package com.aqsa.tabianconsulting

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

/**
 * Created by kodeartisan on 1/27/18.
 */
class ResendVerificationDialog: DialogFragment() {

    private val TAG = ResendVerificationDialog::class.java.name

    private var mConfirmPassword: EditText? = null
    private var mConfirmEmail: EditText? = null
    private var mConfirmDialog: TextView? = null
    private var mCancelDialog: TextView? = null;
    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_resend_verification, container, false)
        mContext = activity
        mConfirmEmail = view?.findViewById(R.id.confirm_email)
        mConfirmPassword = view?.findViewById(R.id.confirm_password)
        mConfirmDialog = view?.findViewById(R.id.dialogConfirm)
        mCancelDialog = view?.findViewById(R.id.dialogCancel)

        mConfirmDialog?.setOnClickListener {
            authenticateAndResendEmail(mConfirmEmail?.text.toString(), mConfirmPassword?.text.toString())
        }

        mCancelDialog?.setOnClickListener { dialog.dismiss() }

        return view
    }

    private fun authenticateAndResendEmail(email: String, password: String) {

        val credential = EmailAuthProvider.getCredential(email, password)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->

                    when(task.isSuccessful) {
                        true -> {
                            Log.d(TAG, "onComplete: reauthenticate success")
                            sendVerificationEmail()
                            FirebaseAuth.getInstance().signOut()
                            dialog.dismiss()
                        }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(mContext, "Invalid credentials \n Reset your password and try again", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            it.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        when(task.isSuccessful) {
                            true ->  Toast.makeText(mContext, "Sent Verification Email", Toast.LENGTH_SHORT).show();
                        }
                    }
        }
    }
}