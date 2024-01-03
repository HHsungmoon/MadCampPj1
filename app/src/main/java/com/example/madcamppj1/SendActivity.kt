package com.example.madcamppj1

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.telephony.SmsManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class SendActivity : AppCompatActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private val REQUEST_IMAGE_CAPTURE = 1

    var stk_num = R.drawable.sticker // 기본 스티커 설정
    var stk_X = 10f
    var stk_Y = 10f

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var selectedPhoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        var img_path = ""
        tap3()

        val firstBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_1)
        val imageView: ImageView = findViewById(R.id.imageInput)
        imageView.setImageBitmap(firstBitmap)

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    // 이미지를 저장할 파일 생성
                    val timeStamp =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val fileName = "captured_image_$timeStamp.jpg"
                    val imageFile = File(getExternalFilesDir(null), fileName)

                    // Bitmap 데이터를 파일로 저장
                    val fos = FileOutputStream(imageFile)
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()

                    // 저장한 파일을 BitmapFactory를 사용하여 Bitmap으로 디코딩
                    val decodedBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

                    // 스티커를 불러옵니다. (이미지 또는 텍스트)
                    val stickerBitmap = BitmapFactory.decodeResource(resources, stk_num)

                    // 이미지에 스티커를 붙입니다.
                    var resultBitmap =
                        addStickerToBitmap(decodedBitmap, stickerBitmap, stk_X, stk_Y, 20f)

                    // 이미지에 폴라로이드를 붙입니다.
                    val polaBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_1)
                    val finalBitmap = addStickerToBitmap(polaBitmap, resultBitmap, 650f, 385f, 2f)

                    // 결과 이미지를 표시합니다.
                    imageView.setImageBitmap(finalBitmap)

                    // 이미지를 저장합니다.
                    img_path = saveImage(finalBitmap)
                }
            }


        val btn_stk1 = findViewById<ImageButton>(R.id.btn_stk1)
        val btn_stk2 = findViewById<ImageButton>(R.id.btn_stk2)
        val btn_stk3 = findViewById<ImageButton>(R.id.btn_stk3)
        val btn_stk4 = findViewById<ImageButton>(R.id.btn_stk4)

        btn_stk1.setOnClickListener {
            stk_num = R.drawable.stk1
            stk_X = 50f
            stk_Y = 10f
            dispatchTakePictureIntent()
        }

        btn_stk2.setOnClickListener {
            stk_num = R.drawable.stk2
            stk_X = 50f
            stk_Y = 35f
            dispatchTakePictureIntent()
        }

        btn_stk3.setOnClickListener {
            stk_num = R.drawable.stk3
            stk_X = 40f
            stk_Y = 40f
            dispatchTakePictureIntent()
        }

        btn_stk4.setOnClickListener {
            stk_num = R.drawable.skt5
            stk_X = 100f
            stk_Y = 50f
            dispatchTakePictureIntent()
        }
//
//        val bottomSheet = findViewById<LinearLayout>(R.id.bottomSheet)
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

//        val addTagButton = findViewById<ImageButton>(R.id.btnAddTag)
//        addTagButton.setOnClickListener {
//            showBottomSheet()
//        }

        val sendButton = findViewById<ImageButton>(R.id.btnSendMsg)
        sendButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:$01038764685") // 문자 보낼 번호
            intent.putExtra("sms_body", "Hi") // 문자의 내용
            //intent.putExtra("sms_body", img_path)
            //intent.type = "image/png"
            startActivity(intent)
        }
    }

    // Bitmap을 ByteArray로 변환하는 메소드

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            takePictureLauncher.launch(takePictureIntent)
        }
    }

    private fun resizeBitmap(originalBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
    }

    private fun addStickerToBitmap(
        originalBitmap: Bitmap,
        stickerBitmap: Bitmap,
        stickerX: Float,
        stickerY: Float,
        stickerSize: Float
    ): Bitmap {
        // 이미지의 가로, 세로 크기를 가져옵니다.
        val width = originalBitmap.width
        val height = originalBitmap.height

        // 스티커의 크기를 원본 이미지의 10분의 1로 조절합니다.
        val stickerWidth = (width / stickerSize).roundToInt()
        val stickerHeight = (stickerBitmap.height * stickerWidth / stickerBitmap.width) + 50
        val scaledStickerBitmap =
            Bitmap.createScaledBitmap(stickerBitmap, stickerHeight, stickerHeight, false)

        // 결과 이미지를 생성합니다.
        val resultBitmap = Bitmap.createBitmap(width, height, originalBitmap.config)

        // 이미지와 스티커를 그릴 Canvas를 생성합니다.
        val canvas = Canvas(resultBitmap)

        // 원본 이미지를 그립니다.
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        // 사용자가 설정한 좌표에 크기를 조절한 스티커를 그립니다.
        canvas.drawBitmap(scaledStickerBitmap, stickerX, stickerY, null)

        return resultBitmap
    }

    private fun saveImage(bitmap: Bitmap): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "result_image_$timeStamp.jpg"

        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, fileName)

        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile.absolutePath
    }


    fun tap3() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_send
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_person -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_image -> {
                    startActivity(Intent(applicationContext, GalleryActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_send -> return@setOnItemSelectedListener true
            }
            false
        }
    }
}
//    private fun showBottomSheet() {
//        val receivedList: ArrayList<Profile>? = intent.getSerializableExtra("profilesList") as? ArrayList<Profile>
//
//        // Bottom Modal Sheet 보이기
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//
//        // RecyclerView 설정
//        val profileListView = findViewById<ListView>(R.id.profileListView)
////        val layoutManager = LinearLayoutManager(profileListView.context)
//        if (receivedList != null) {
//            // Use the receivedList here in your activity
//            val adapter = ProfileAdapter(this, R.layout.list_item_layout, receivedList)
//            profileListView.adapter = adapter
//
//            // ListView 아이템 클릭 이벤트 처리
//            profileListView.setOnItemClickListener { parent, view, position, id ->
//                val profile = receivedList[position]
//                // 해당 프로필의 전화번호 처리
//                selectedPhoneNumber = profile.phoneNumber
//                // 여기서 선택된 phoneNumber에 대한 작업을 수행합니다.
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//            }
//        }

//    }

//
//
//class SendActivity : AppCompatActivity() {
//
//    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
//    private val REQUEST_IMAGE_CAPTURE = 1
//
//    var stk_num = R.drawable.sticker // 기본 스티커 설정
//    var stk_X = 10f
//    var stk_Y = 10f
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_send)
//
//        var img_path = ""
//        tap3()
//
//        val firstBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_1)
//        val imageView: ImageView = findViewById(R.id.imageInput)
//        imageView.setImageBitmap(firstBitmap)
//
//        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val data: Intent? = result.data
//                val imageBitmap = data?.extras?.get("data") as Bitmap
//
//                // 이미지를 저장할 파일 생성
//                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//                val fileName = "captured_image_$timeStamp.jpg"
//                val imageFile = File(getExternalFilesDir(null), fileName)
//
//                // Bitmap 데이터를 파일로 저장
//                val fos = FileOutputStream(imageFile)
//                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//                fos.close()
//
//                // 저장한 파일을 BitmapFactory를 사용하여 Bitmap으로 디코딩
//                val decodedBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
//
//                // 스티커를 불러옵니다. (이미지 또는 텍스트)
//                val stickerBitmap = BitmapFactory.decodeResource(resources, stk_num)
//
//                // 이미지에 스티커를 붙입니다.
//                var resultBitmap = addStickerToBitmap(decodedBitmap, stickerBitmap, stk_X, stk_Y, 3f)
//
//                // 이미지에 폴라로이드를 붙입니다.
//                val polaBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_1)
//                val finalBitmap = addStickerToBitmap(polaBitmap, resultBitmap, 660f, 390f,2f)
//
//                // 결과 이미지를 표시합니다.
//                imageView.setImageBitmap(finalBitmap)
//
//                // 이미지를 저장합니다.
//                img_path = saveImage(finalBitmap)
//            }
//        }
//
//
//        val btn_stk1 = findViewById<ImageButton>(R.id.btn_stk1)
//        val btn_stk2 = findViewById<ImageButton>(R.id.btn_stk2)
//        val btn_stk3 = findViewById<ImageButton>(R.id.btn_stk3)
//        val btn_stk4 = findViewById<ImageButton>(R.id.btn_stk4)
//
//        btn_stk1.setOnClickListener {
//            stk_num = R.drawable.stk1
//            stk_X = 50f
//            stk_Y = 10f
//            dispatchTakePictureIntent()
//        }
//
//        btn_stk2.setOnClickListener {
//            stk_num = R.drawable.stk2
//            stk_X = 50f
//            stk_Y = 35f
//            dispatchTakePictureIntent()
//        }
//
//        btn_stk3.setOnClickListener {
//            stk_num = R.drawable.stk3
//            stk_X = 40f
//            stk_Y = 40f
//            dispatchTakePictureIntent()
//        }
//
//        btn_stk4.setOnClickListener {
//            stk_num = R.drawable.skt5
//            stk_X = 100f
//            stk_Y = 50f
//            dispatchTakePictureIntent()
//        }
//
//        val sendButton = findViewById<ImageButton>(R.id.btnSendMsg)
//        sendButton.setOnClickListener {
//            val intent = Intent(Intent.ACTION_SENDTO)
//            intent.data = Uri.parse("smsto:01024013487") // 문자 보낼 번호
//            intent.putExtra("sms_body", "Hello World") // 문자의 내용
//            //intent.putExtra("sms_body", img_path)
//            //intent.type = "image/png"
//            startActivity(intent)
//        }
//    }
//
//    // Bitmap을 ByteArray로 변환하는 메소드
//
//    private fun dispatchTakePictureIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (takePictureIntent.resolveActivity(packageManager) != null) {
//            takePictureLauncher.launch(takePictureIntent)
//        }
//    }
//    private fun resizeBitmap(originalBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
//        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
//    }
//    private fun addStickerToBitmap(originalBitmap: Bitmap, stickerBitmap: Bitmap, stickerX: Float, stickerY: Float, stickerSize:Float): Bitmap {
//        // 이미지의 가로, 세로 크기를 가져옵니다.
//        val width = originalBitmap.width
//        val height = originalBitmap.height
//
//        // 스티커의 크기를 원본 이미지의 10분의 1로 조절합니다.
//        val stickerWidth = (width / stickerSize).roundToInt()
//        val stickerHeight = (stickerBitmap.height * stickerWidth / stickerBitmap.width)+50
//        val scaledStickerBitmap = Bitmap.createScaledBitmap(stickerBitmap, stickerHeight, stickerHeight, false)
//
//        // 결과 이미지를 생성합니다.
//        val resultBitmap = Bitmap.createBitmap(width, height, originalBitmap.config)
//
//        // 이미지와 스티커를 그릴 Canvas를 생성합니다.
//        val canvas = Canvas(resultBitmap)
//
//        // 원본 이미지를 그립니다.
//        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
//
//        // 사용자가 설정한 좌표에 크기를 조절한 스티커를 그립니다.
//        canvas.drawBitmap(scaledStickerBitmap, stickerX, stickerY, null)
//
//        return resultBitmap
//    }
//
//    private fun saveImage(bitmap: Bitmap): String {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val fileName = "result_image_$timeStamp.jpg"
//
//        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val imageFile = File(storageDir, fileName)
//
//        try {
//            val fos = FileOutputStream(imageFile)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//            fos.flush()
//            fos.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return imageFile.absolutePath
//    }
//
//
//
//    fun tap3(){
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
//        bottomNavigationView.selectedItemId = R.id.bottom_send
//        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
//            when (item.itemId) {
//                R.id.bottom_person -> {
//                    startActivity(Intent(applicationContext, MainActivity::class.java))
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//                    finish()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.bottom_image -> {
//                    startActivity(Intent(applicationContext, GalleryActivity::class.java))
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//                    finish()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.bottom_send -> return@setOnItemSelectedListener true
//            }
//            false
//        }
//    }
//}