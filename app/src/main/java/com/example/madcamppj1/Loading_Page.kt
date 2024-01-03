package com.example.madcamppj1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.madcamppj1.MainActivity

class Loading_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_page)

        val nextIntent = Intent(this, MainActivity::class.java)
        startActivity(nextIntent)

    }
}