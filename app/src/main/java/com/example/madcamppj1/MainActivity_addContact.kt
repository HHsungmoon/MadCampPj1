package com.example.madcamppj1

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
//import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFrag : BottomSheetDialogFragment() {
    private var onProfileAddedListener: OnProfileAddedListener? = null
    private var profilesList: MutableList<Profile>? = null
    private var imgView: ImageView? = null
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addPhotoButton: Button
    private lateinit var doneButton: Button
    private lateinit var cancelButton: Button

    private var selectedImageUri: Uri? = null // 사용자가 선택한 이미지의 URI

    fun setOnProfileAddedListener(listener: OnProfileAddedListener) {
        onProfileAddedListener = listener
    }
    // Function to set profilesList from MainActivity
    fun setProfilesList(profiles: MutableList<Profile>?) {
        profilesList = profiles
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.add_contact, container, false)
        imgView = view.findViewById(R.id.imageView)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgView = view.findViewById<ImageView>(R.id.imageView)
        nameEditText = view.findViewById(R.id.editTextName)
        phoneEditText = view.findViewById(R.id.editTextPhone)
        addPhotoButton = view.findViewById(R.id.btnAddPhoto)
        doneButton = view.findViewById(R.id.btnDone)
        cancelButton = view.findViewById(R.id.btnCancel)
//        doneButton.setClickable(false);
//        doneButton.setTextColor(Color.parseColor("#E9E9E9"))
        // Handling addPhotoButton click event
        addPhotoButton.setOnClickListener {
            openGallery()
        }

        // Handling doneButton click event
        doneButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val phoneNumber = phoneEditText.text.toString()
            // Here you can use 'selectedImageUri' for the selected image URI
            // Perform any required operation with the data obtained from the EditText fields and the image URI
            if (name.isNotEmpty()&&phoneNumber.isNotEmpty()) {
                val contact = Profile(selectedImageUri,"$name", phoneNumber)
                onProfileAddedListener?.onProfileAdded(contact)
                dismiss()
                // TODO: contactList를 사용하여 ListView에 데이터 표시
                // 예를 들어, Custom Adapter를 사용하여 ListView에 데이터 연결
            } else {
                if(name.isEmpty()&&phoneNumber.isEmpty()) {
                    Toast.makeText(context, "Please enter a name and phone number.", Toast.LENGTH_SHORT).show()
                }
                // 사용자에게 이름을 입력하도록 요청하는 메시지 표시
                else if (name.isEmpty()) (Toast.makeText(context, "Please enter a name.", Toast.LENGTH_SHORT).show())
                else (Toast.makeText(context, "Please enter a phone number.", Toast.LENGTH_SHORT).show())
            }
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }
    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }

    // Handle the result of the gallery Intent if needed (if using startActivityForResult)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
//            if(selectedImageUri!=null) {
////                    imageView.setImageURI(null)
////                    print(selectedImageUri)
////                    imageView.setImageURI(selectedImageUri)
//                Glide.with(this)
//                    .load(selectedImageUri)
//                    .into(imgView)
////                    imageView.setImageResource(R.drawable.daniel)
////                    imageView.invalidate()
//                // Do something with the selectedImageUri
//            }
            imgView?.let { imageView ->
                selectedImageUri?.let { uri ->
                    Glide.with(this@BottomSheetFrag) // Use the correct reference to the fragment
                        .load(uri)
                        .into(imageView)
                }
            }
        }
    }

}

//
//        // XML의 뷰 요소 초기화
//        firstNameEditText = view.findViewById(R.id.editTextFirstName)
//        lastNameEditText = view.findViewById(R.id.editTextLastName)
//        phoneEditText = view.findViewById(R.id.editTextPhone)
//        addPhotoButton = view.findViewById(R.id.btnAddPhoto)
//        doneButton = view.findViewById(R.id.btnDone)
//
//        // Add Photo 버튼 클릭 시 갤러리 열기
//        addPhotoButton.setOnClickListener {
//            openGalleryForImage()
//        }
//
//        // Done 버튼 클릭 시 연락처 저장
//        doneButton.setOnClickListener {
//            saveContact()
//        }
//
//        // EditText에 텍스트가 변경될 때마다 Done 버튼 활성화 여부 확인하는 리스너 등록
//        firstNameEditText.addTextChangedListener(textWatcher)
//
//        return view
//    }
//
//    // EditText 텍스트 변경 감지하여 Done 버튼 활성화 여부 결정하는 TextWatcher
//    private val textWatcher = object : TextWatcher {
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//        override fun afterTextChanged(s: Editable?) {
//            doneButton.isEnabled = !firstNameEditText.text.isNullOrBlank()
//        }
//    }
//
//    // 갤러리에서 이미지 선택하는 기능
//    private fun openGalleryForImage() {
//        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivity(galleryIntent, REQUEST_CODE_GALLERY)
//    }
//
//    // 갤러리에서 이미지를 선택한 후 돌아오는 결과 처리
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
//            val imageUri: Uri? = selectedImageUri
//            selectedImageUri = data.data
//            // 선택한 이미지의 URI를 사용하여 이미지뷰에 이미지 설정하거나 필요한 작업 수행
//        }
//    }
//
//    companion object {
//        private const val REQUEST_CODE_GALLERY = 100
//    }
//
//    // Done 버튼 클릭 시, 연락처 저장
//    private fun saveContact() {
//        val firstName = firstNameEditText.text.toString()
//        val lastName = lastNameEditText.text.toString()
//        val phoneNumber = phoneEditText.text.toString()
//
//        if (firstName.isNotEmpty()) {
//            val contact = Contact(firstName, lastName, phoneNumber, selectedImageUri)
//            contactList.add(contact)
//
//            // 예를 들어, Custom Adapter를 사용하여 ListView에 데이터 연결
//        } else {
//            // 사용자에게 이름을 입력하도록 요청하는 메시지 표시
//            Toast.makeText(context, "Please enter a first name.", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
////        // XML의 뷰 요소 초기화
////        firstNameEditText = view.findViewById(R.id.editTextFirstName)
////        lastNameEditText = view.findViewById(R.id.editTextLastName)
////        phoneEditText = view.findViewById(R.id.editTextPhone)
////        addPhotoButton = view.findViewById(R.id.btnAddPhoto)
////        doneButton = view.findViewById(R.id.btnDone)
////
////        // Add Photo 버튼 클릭 시 갤러리 열기
////        addPhotoButton.setOnClickListener {
////            openGalleryForImage()
////        }
////
////        // Done 버튼 클릭 시 연락처 저장
////        doneButton.setOnClickListener {
////            saveContact()
////        }
////
////        // EditText에 텍스트가 변경될 때마다 Done 버튼 활성화 여부 확인하는 리스너 등록
////        firstNameEditText.addTextChangedListener(textWatcher)
////
////        return view
////    }
////    // 갤러리에서 이미지 선택하는 기능
////    private fun openGalleryForImage() {
////        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
////        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
////    }
////
////    // 갤러리에서 이미지를 선택한 후 돌아오는 결과 처리
////    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////        super.onActivityResult(requestCode, resultCode, data)
////        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
////            selectedImageUri = data.data
////            // 선택한 이미지의 URI를 사용하여 이미지뷰에 이미지 설정하거나 필요한 작업 수행
////        }
////    }
////
////    // Done 버튼 클릭 시, 연락처 저장
////    private fun saveContact() {
////        val firstName = firstNameEditText.text.toString()
////        val lastName = lastNameEditText.text.toString()
////        val phoneNumber = phoneEditText.text.toString()
////
////        if (firstName.isNotEmpty()) {
////            val contact = Contact(firstName, lastName, phoneNumber, selectedImageUri)
////            contactList.add(contact)
////
////            // TODO: contactList를 사용하여 ListView에 데이터 표시
////            // 예를 들어, Custom Adapter를 사용하여 ListView에 데이터 연결
////        } else {
////            // 사용자에게 이름을 입력하도록 요청하는 메시지 표시
////            Toast.makeText(context, "Please enter a first name.", Toast.LENGTH_SHORT).show()
////        }
////    }
////
////    // EditText 텍스트 변경 감지하여 Done 버튼 활성화 여부 결정하는 TextWatcher
////    private val textWatcher = object : TextWatcher {
////        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
////
////        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
////
////        override fun afterTextChanged(s: Editable?) {
////            doneButton.isEnabled = !firstNameEditText.text.isNullOrBlank()
////        }
////    }
////
////    companion object {
////        private const val REQUEST_CODE_GALLERY = 100
////    }
////
////}
