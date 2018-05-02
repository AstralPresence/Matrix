package com.apptronics.matrix.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast


import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth
import android.widget.RadioGroup
import com.apptronics.matrix.R
import timber.log.Timber


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var mAuth: FirebaseAuth? = null
    internal var RC_SIGN_IN = 1
    internal var progressDialog: ProgressDialog? = null

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.getCurrentUser()
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }

    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener(this)


    }

    fun login() {

        if (!validate()) {
            Timber.i("invalid details")
            return
        }

        btn_login!!.isEnabled = false

        val email = input_email!!.text.toString()
        val password = input_password!!.text.toString()

        mAuth=FirebaseAuth.getInstance()
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth!!.getCurrentUser()
                        startActivity(Intent(this, MainActivity::class.java))

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Failed. Check network",
                                Toast.LENGTH_SHORT).show()
                        btn_login.isEnabled=true

                    }

                    // ...
                }
    }


    fun validate(): Boolean {
        var valid = true

        val email = input_email.text.toString()
        val password = input_password.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = getString(R.string.enter_valid_email)
            valid = false
        } else {
            input_email.error = null
        }

        if (password.isEmpty() ) {
            input_password.error = getString(R.string.password_error)
            valid = false
        } else {
            input_password.error = null
        }

        return valid
    }

    override fun onClick(v: View) {

        Timber.i("login clicked")
        login()
    }


}