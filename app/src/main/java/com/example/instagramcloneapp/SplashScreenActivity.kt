package com.example.instagramcloneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val myAnim = AnimationUtils.loadAnimation(this, R.anim.mytransition)

        imgSplashLogo.startAnimation(myAnim)
        lblSplashTitle.startAnimation(myAnim)
        lblPowerBy.startAnimation(myAnim)
        lblDesmond.startAnimation(myAnim)

        val signInIntent = Intent(this, SignInActivity::class.java)
        val welcomeIntent = Intent(this, WelcomeActivity::class.java)
        val timer = object : Thread()
        {
            override fun run()
            {
                try
                {
                    sleep(3000)
                } catch (e: InterruptedException)
                {
                    e.printStackTrace()
                } finally
                {
                    if (FirebaseAuth.getInstance().currentUser != null)
                    {
                        startActivity(welcomeIntent)
                    }
                    else
                    {
                        startActivity(signInIntent)
                    }

                    finish()
                }
            }
        }
        timer.start()
    }
}