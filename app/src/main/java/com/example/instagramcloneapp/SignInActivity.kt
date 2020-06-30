package com.example.instagramcloneapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        buttonOnClickListener()
        textFieldValidationChecking()

        setLoginButtonEnabled(false)
        startLoadingView(false)
        enableComponents(true)
    }

    override fun onStart()
    {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonOnClickListener()
    {
        signup_link_btn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            email_login.text = null
            password_login.text = null
            lblInvalidEmail.visibility = View.GONE
        }

        login_btn.setOnClickListener {
            enableComponents(false)
            setLoginButtonEnabled(false)
            startLoadingView(true)
            loginUser()
        }

        layout_sign_in_relative.setOnTouchListener { _, _ ->
            if (email_login.text.toString() != "" && !isEmailValid(email_login.text.toString()))
            {
                lblInvalidEmail.visibility = View.VISIBLE
            }
            else
            {
                lblInvalidEmail.visibility = View.GONE
            }

            hideSoftKeyboard(this)
            email_login.clearFocus()
            password_login.clearFocus()
            false
        }
    }

    private fun textFieldValidationChecking()
    {
        email_login.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?)
            {
                if (!isEmailValid(email_login.text.toString()))
                {
                    lblInvalidEmail.visibility = View.VISIBLE
                }
                else
                {
                    lblInvalidEmail.visibility = View.GONE
                    if (email_login.text.toString() != "" && password_login.text.toString() != "")
                    {
                        setLoginButtonEnabled(true)
                    }
                    else
                    {
                        setLoginButtonEnabled(false)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (email_login.text.toString() != "" && password_login.text.toString() != "")
                {
                    setLoginButtonEnabled(true)
                }
                else
                {
                    setLoginButtonEnabled(false)
                }
            }
        })

        password_login.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?)
            {
                if (email_login.text.toString() != "" && password_login.text.toString() != "")
                {
                    setLoginButtonEnabled(true)
                }
                else
                {
                    setLoginButtonEnabled(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (email_login.text.toString() != "" && password_login.text.toString() != "")
                {
                    setLoginButtonEnabled(true)
                }
                else
                {
                    setLoginButtonEnabled(false)
                }
            }
        })
    }

    fun isEmailValid(email: CharSequence): Boolean
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setLoginButtonEnabled(isEnabled: Boolean)
    {
        if (isEnabled)
        {
            login_btn.isEnabled = true
            login_btn.isClickable = true
            login_btn.background = resources.getDrawable(R.drawable.rounded_corner_black)
        }
        else
        {
            login_btn.isEnabled = false
            login_btn.isClickable = false
            login_btn.background = resources.getDrawable(R.drawable.rounded_corner_light_gray)
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

    private fun startLoadingView(start: Boolean)
    {
        if (start)
        {
            layoutLoadingView.visibility = View.VISIBLE
            animLoadingView.setAnimation("paperplane_lottie_animation.json")
            animLoadingView.playAnimation()
            animLoadingView.loop(true)
        }
        else
        {
            layoutLoadingView.visibility = View.GONE
            animLoadingView.cancelAnimation()
        }
    }

    private fun enableComponents(isEnabled: Boolean)
    {
        if (isEnabled)
        {
            signup_link_btn.isEnabled = true
            email_login.isEnabled = true
            password_login.isEnabled = true

        }
        else
        {
            signup_link_btn.isEnabled = false
            email_login.isEnabled = false
            password_login.isEnabled = false
        }
    }

    private fun loginUser()
    {
        val email = email_login.text.toString()
        val password = password_login.text.toString()

        when
        {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required.", Toast.LENGTH_LONG).show()

            else ->
            {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                //progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        progressDialog.dismiss()
                        startLoadingView(false)
                        enableComponents(true)
                        setLoginButtonEnabled(true)

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
                        setLoginButtonEnabled(true)
                        //progressDialog.dismiss()
                    }
                }
            }
        }
    }
}
