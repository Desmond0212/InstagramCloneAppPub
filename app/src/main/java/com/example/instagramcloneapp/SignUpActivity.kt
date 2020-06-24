package com.example.instagramcloneapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signin_link_btn.setOnClickListener {
            finish()
        }

        signup_btn.setOnClickListener {
            enableComponents(false)
            startLoadingView(true)
            createAccount()
        }

        layout_sign_up_relative.setOnTouchListener { _, _ ->
            hideSoftKeyboard(this)
            false
        }

        setSignUpButtonEnabled(false)
        startLoadingView(false)
        enableComponents(true)
        initUI()
    }

    private fun initUI()
    {
        fullname_signup.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?)
            {
                if (fullname_signup.text.toString() != "" &&
                    username_signup.text.toString() != "" &&
                    email_signup.text.toString() != "" &&
                    password_signup.text.toString() != "")
                {
                    setSignUpButtonEnabled(true)
                }
                else
                {
                    setSignUpButtonEnabled(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (fullname_signup.text.toString() != "" &&
                    username_signup.text.toString() != "" &&
                    email_signup.text.toString() != "" &&
                    password_signup.text.toString() != "")
                {
                    setSignUpButtonEnabled(true)
                }
                else
                {
                    setSignUpButtonEnabled(false)
                }
            }
        })

        username_signup.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?)
            {
                if (fullname_signup.text.toString() != "" &&
                    username_signup.text.toString() != "" &&
                    email_signup.text.toString() != "" &&
                    password_signup.text.toString() != "")
                {
                    setSignUpButtonEnabled(true)
                }
                else
                {
                    setSignUpButtonEnabled(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (fullname_signup.text.toString() != "" &&
                    username_signup.text.toString() != "" &&
                    email_signup.text.toString() != "" &&
                    password_signup.text.toString() != "")
                {
                    setSignUpButtonEnabled(true)
                }
                else
                {
                    setSignUpButtonEnabled(false)
                }
            }
        })

        email_signup.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?)
            {
                if (isEmailValid(email_signup.text.toString()))
                {
                    lbl_invalid_email.visibility = View.GONE
                }
                else
                {
                    lbl_invalid_email.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (fullname_signup.text.toString() != "" &&
                    username_signup.text.toString() != "" &&
                    email_signup.text.toString() != "" &&
                    password_signup.text.toString() != "")
                {
                    setSignUpButtonEnabled(true)
                }
                else
                {
                    setSignUpButtonEnabled(false)
                }
            }
        })

        password_signup.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?)
            {
                if (fullname_signup.text.toString() != "" &&
                    username_signup.text.toString() != "" &&
                    email_signup.text.toString() != "" &&
                    password_signup.text.toString() != "")
                {
                    setSignUpButtonEnabled(true)
                }
                else
                {
                    setSignUpButtonEnabled(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (fullname_signup.text.toString() != "" &&
                    username_signup.text.toString() != "" &&
                    email_signup.text.toString() != "" &&
                    password_signup.text.toString() != "")
                {
                    setSignUpButtonEnabled(true)
                }
                else
                {
                    setSignUpButtonEnabled(false)
                }
            }
        })
    }

    private fun setSignUpButtonEnabled(isEnabled: Boolean)
    {
        if (isEnabled)
        {
            signup_btn.isEnabled = true
            signup_btn.background = resources.getDrawable(R.drawable.rounded_corner_black)
        }
        else
        {
            signup_btn.isEnabled = false
            signup_btn.background = resources.getDrawable(R.drawable.rounded_corner_light_gray)
        }
    }

    private fun isEmailValid(email: CharSequence): Boolean
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun startLoadingView(start: Boolean)
    {
        if (start)
        {
            layoutLoadingView_SignIn.visibility = View.VISIBLE
            animLoadingView_SignIn.setAnimation("paperplane_lottie_animation.json")
            animLoadingView_SignIn.playAnimation()
            animLoadingView_SignIn.loop(true)
        }
        else
        {
            layoutLoadingView_SignIn.visibility = View.GONE
            animLoadingView_SignIn.cancelAnimation()
        }
    }

    private fun enableComponents(isEnabled: Boolean)
    {
        if (isEnabled)
        {
            signin_link_btn.isEnabled = true
            signup_btn.isEnabled = true
            fullname_signup.isEnabled = true
            username_signup.isEnabled = true
            email_signup.isEnabled = true
            password_signup.isEnabled = true
        }
        else
        {
            signin_link_btn.isEnabled = false
            signup_btn.isEnabled = false
            fullname_signup.isEnabled = false
            username_signup.isEnabled = false
            email_signup.isEnabled = false
            password_signup.isEnabled = false
        }
    }

    private fun hideSoftKeyboard(activity: Activity)
    {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken, 0
        )
    }

    private fun createAccount()
    {
        val fullName = fullname_signup.text.toString()
        val userName = username_signup.text.toString()
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()

        when
        {
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "Full Name is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "User Name is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required.", Toast.LENGTH_LONG).show()

            else ->
            {
                /*val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Sign Up")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()*/

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                        {
                            saveUserInfo(fullName, userName, email, password)

                        }
                        else
                        {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message" , Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            startLoadingView(false)
                            enableComponents(true)
                            //progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, password: String)
    {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()

        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Welcome to my Instagram Clone App!"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagram-clone-app-7f61a.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=1d051bfb-77fd-4404-9652-95836cfb796f"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener {task ->
                if (task.isSuccessful)
                {
                    //progressDialog.dismiss()
                    startLoadingView(false)
                    enableComponents(true)

                    Toast.makeText(this, "Account has been created successfully.", Toast.LENGTH_LONG).show()

                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(currentUserID)
                        .child("Following")
                        .child(currentUserID)
                        .setValue(true)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message" , Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    startLoadingView(false)
                    enableComponents(true)
                    //progressDialog.dismiss()
                }
            }
    }
}
