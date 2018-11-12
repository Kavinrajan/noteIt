package com.kavin.noteit.noteit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.os.Handler
import android.content.Intent

import com.kavin.noteit.noteit.ConsultActivity
import com.kavin.noteit.noteit.R

import org.jetbrains.anko.toast


/**
 * Created by kavinrajansm on 5/23/2018.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash)
            Handler().postDelayed(
                     /* method will be executed once the timer is over
                     Start your app main activity */
                    {
                        startActivity(Intent(this@SplashActivity, ConsultActivity::class.java))
                        finish()
                    }, SPLASH_TIME_OUT.toLong())

        } catch (e: Exception) {
            toast(e.toString())
        }
    }


    companion object {
            var SPLASH_TIME_OUT = 500
    }
}
