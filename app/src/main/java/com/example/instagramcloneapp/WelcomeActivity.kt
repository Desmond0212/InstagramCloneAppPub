package com.example.instagramcloneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.instagramcloneapp.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity()
{
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        animLoadingView_welcome.setAnimation("18430-welcome-white.json")
        animLoadingView_welcome.playAnimation()
        animLoadingView_welcome.loop(true)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue(UserModel::class.java)
                    lblUsername.text = user?.getUsername().toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        val signInIntent = Intent(this, SignInActivity::class.java)
        val timer = object : Thread()
        {
            override fun run()
            {
                try
                {
                    sleep(4000)
                } catch (e: InterruptedException)
                {
                    e.printStackTrace()
                } finally
                {
                    startActivity(signInIntent)
                    finish()
                }
            }
        }
        timer.start()
    }
}