package com.apptronics.matrix.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast


import com.google.firebase.auth.FirebaseAuth
import com.apptronics.matrix.R
import com.apptronics.matrix.model.User
import timber.log.Timber
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.LinearLayout
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var email: String? = null
    var name: String? = null
    var phone: String? = null
    var profilePicURL: String? = null
    var password: String? = null

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
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        btn_signup.setOnClickListener(this)

        goLogin.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })
    }

    fun signUp() {

        if (!validate()) {
            Timber.i("invalid details")
            return
        }

        btn_signup!!.isEnabled = false

        val email = input_email!!.text.toString()
        val password = input_password!!.text.toString()

        mAuth=FirebaseAuth.getInstance()
        mAuth!!.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){ task->
                    if (task.isSuccessful) {
                        // Sign in: success
                        // update UI for current User
                        val user = mAuth!!.currentUser
                        if (profilePicURL == null || profilePicURL === "") {
                            profilePicURL = "https://lh3.googleusercontent.com/-hyObPp970Jc/AAAAAAAAAAI/AAAAAAAAAAA/AIcfdXDTGc78NEQBCMosvaMCpwPJJM05RQ/mo/photo.jpg?sz=46"
                        }
                        val userDB = User(name,email,profilePicURL,phone)
                        mDatabase=FirebaseDatabase.getInstance().reference
                        mDatabase!!.child("users").child(user!!.uid).setValue(userDB).addOnCompleteListener(OnCompleteListener {
                            if(task.isSuccessful){
                                startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                Toast.makeText(this,"Failed to add user to db. "+task.result.additionalUserInfo,Toast.LENGTH_SHORT).show()
                            }
                        })

                    } else {
                        Toast.makeText(this,"Failed to create user. "+task.result.additionalUserInfo,Toast.LENGTH_SHORT).show()
                    }
                }

    }



    fun validate(): Boolean {
        var valid = true

        email = input_email.text.toString()
        name = input_name.text.toString()
        phone = input_phone.text.toString()
        profilePicURL = input_profile_photo_url.text.toString()
        password = input_password.text.toString()

        if (email!!.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = getString(R.string.enter_valid_email)
            valid = false
        } else {
            input_email.error = null
        }

        if (password!!.isEmpty() ) {
            input_password.error = getString(R.string.password_error)
            valid = false
        } else {
            input_password.error = null
        }

        if (phone!!.isEmpty() || !android.util.Patterns.PHONE.matcher(phone).matches()) {
            input_phone.error = getString(R.string.phone_error)
            valid = false
        } else {
            input_phone.error = null
        }

        if (name!!.isEmpty() ) {
            input_name.error = getString(R.string.name_error)
            valid = false
        } else {
            input_name.error = null
        }

        return valid
    }

    override fun onClick(v: View) {

        Timber.i("sign up clicked")
        signUp()
    }



}
