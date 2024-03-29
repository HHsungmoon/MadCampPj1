package com.example.madcamppj1

import android.content.Context
import android.content.Intent
import android.os.Bundle
//import android.support.v4.app.DialogFragment
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import android.net.Uri
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class MainActivity : AppCompatActivity(), OnProfileAddedListener {
    private lateinit var profilesList: MutableList<Profile>
    private lateinit var adapter: ProfileAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView: ListView = findViewById(R. id.listView)
        profilesList = loadProfilesFromJson()
        // Custom adapter for the ListView
        adapter = ProfileAdapter(this, R.layout.list_item_layout, profilesList)
        listView.adapter = adapter

        val button = findViewById<ImageButton>(R.id.btnAddContact)
        button.setOnClickListener{
            showBottomSheet(profilesList)
        }
        moveTab()

    }
//        // Set item click listener for the ListView
//        listView.setOnItemClickListener { _, _, position, _ ->
//            val profile = profilesList[position]
//            val intent = Intent(this, ProfileDetailsActivity::class.java)
//            intent.putExtra("PROFILE", profile)
//            startActivity(intent)
//        }

    private fun loadProfilesFromJson(): MutableList<Profile> {
        val profiles = mutableListOf<Profile>()

        try {
            val json = application.assets.open("jsons/contact.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val imageName = jsonObject.getString("profileImageResId")
                val name = jsonObject.getString("name")
                val phoneNumber = jsonObject.getString("phoneNumber")
                // 이미지 이름을 기반으로 Uri 생성
                val imageUri = getImageUriFromDrawable(this, imageName)

                val profile = Profile(imageUri, name, phoneNumber)
                profiles.add(profile)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        return profiles
    }
    private fun showBottomSheet(pList: MutableList<Profile>) {
        val frag = BottomSheetFrag()
        frag.setProfilesList(pList)
        frag.setOnProfileAddedListener(this) // 인터페이스 설정
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
        frag.show(supportFragmentManager, frag.tag)
    }
    override fun onProfileAdded(profile: Profile) {
        // BottomSheetFrag에서 프로필 추가 시 호출되는 메서드
        profilesList.add(profile) // MainActivity의 profilesList에 추가
        adapter.notifyDataSetChanged() // ListView 업데이트
    }
    private fun getImageUriFromDrawable(context:Context, imageName: String): Uri? {
        // 이미지 이름을 기반으로 Uri를 생성하는 로직 작성
        // 예시: "android.resource://${packageName}/${drawableId}"
        // 참고: https://developer.android.com/reference/android/net/Uri
        val resources = context.resources
        val drawableResourceId = resources.getIdentifier(
            imageName, "drawable", context.packageName
        )

        return if (drawableResourceId != 0) {
            Uri.parse("android.resource://${context.packageName}/$drawableResourceId")
        } else {
            null
        }

        return null // Uri 반환 또는 null 처리
    }

    fun moveTab(){
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_person
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_person -> return@setOnItemSelectedListener true
                R.id.bottom_image -> {
                    startActivity(Intent(applicationContext, GalleryActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_send -> {
//                    val profilesList = ArrayList(profilesList)
//                    intent.putExtra("profilesList", profilesList)
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

