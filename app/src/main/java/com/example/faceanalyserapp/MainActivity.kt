package com.example.faceanalyserapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realTimeBtn.setOnClickListener {
            startActivity(Intent(applicationContext, RealTimeActivity::class.java))
        }

        photoBtn.setOnClickListener {
            startActivity(Intent(applicationContext, PhotoAnalysisActivity::class.java))
        }
        natarioBtn.setOnClickListener {
            startActivity(Intent(applicationContext, NatarioActivity::class.java))
        }
    }
}
