package com.apptronics.matrix.ui


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.apptronics.matrix.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var mAuth: FirebaseAuth? = null

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        mAuth = FirebaseAuth.getInstance()
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
        goSignUp.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        })

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
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        btn_login.isEnabled=true
                        input_password.setText("")
                        Toast.makeText(this@LoginActivity,"Authentication failed.",Toast.LENGTH_SHORT).show()
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