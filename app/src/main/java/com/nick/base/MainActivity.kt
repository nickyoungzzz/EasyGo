package com.nick.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author NICK
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv.setOnClickListener {
            val intent = Intent(this, Second::class.java)
            startActivity(intent)
        }

//        tv.visibility = View.VISIBLE
//        tv.setOnClickListener {
//            val intent = Intent(this, Second::class.java)
//            startActivity(intent)
//        }
//        tv.animate().setDuration(5000).rotationXBy(50f).rotationYBy(50f)
//            .setInterpolator { input: Float ->
//                if (input <= 0.5f) sin(input * Math.PI).toFloat() / 2 else 1 - sin(input * Math.PI).toFloat() / 2
//            }.setInterpolator(LinearInterpolator()).start()
    }
}
