package com.example.madcamppj1

import android.content.Intent
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.w1.Fragment_Gallery1
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        tmp1()


        //2. 갤러리 프래그먼트 출력
        val btnGallery1 = findViewById<ImageButton>(R.id.Gallery1)
        btnGallery1.setOnClickListener {
            val frag1 = supportFragmentManager.beginTransaction()
            frag1.replace(R.id.frameLayout,Fragment_Gallery1())
            frag1.commit()
        }

        //3. 여러개의 사진을 선택해 갤러리 생성
        val btnGallery2 = findViewById<ImageButton>(R.id.Gallery2)
        btnGallery2.setOnClickListener {
            val intent = Intent(this, Activity_Gallery2::class.java)
            startActivity(intent)
        }
    }


    fun tmp1(){
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_image
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_person -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_image -> return@setOnItemSelectedListener true
                R.id.bottom_send -> {
                    startActivity(Intent(applicationContext, SendActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

}