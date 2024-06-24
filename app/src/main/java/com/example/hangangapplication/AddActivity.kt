package com.example.hangangapplication

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.hangangapplication.databinding.ActivityAddBinding
import java.io.File
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat

class AddActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddBinding
    var uri: Uri? = null //사진

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "오늘, 바다"

        binding.tvId.text = MyApplication.email

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        //문자열 변경
        val idStr = sharedPreferences.getString("settitle", "바다 후기 작성하기")
        binding.text.text = idStr

        //색깔 변경
        val color = sharedPreferences.getString("color", "#536DFE")
        binding.saveButton.setBackgroundColor(Color.parseColor(color))

        //스타일 변경
        val textStyle = sharedPreferences.getString("textStyle", "normal")
        val typefaceStyle = when (textStyle) {
            "bold" -> Typeface.BOLD
            "italic" -> Typeface.ITALIC
            "bold_italic" -> Typeface.BOLD_ITALIC
            else -> Typeface.NORMAL
        }
        binding.saveButton.setTypeface(null, typefaceStyle)

        //글자 크기 변경
        val size = sharedPreferences.getString("size", "16.0f")
        binding.tvId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size!!.toFloat())

        //사진
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode === android.app.Activity.RESULT_OK){
                binding.addImageView.visibility = View.VISIBLE
                Glide //이미지 화면에 보이게 할 때 Glide 사용
                    .with(applicationContext)
                    .load(it.data?.data)
                    .override(200,150)
                    .into(binding.addImageView)

                uri = it.data?.data!!
            }
        }
        binding.uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK) //갤러리로 이동
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            if (binding.input.text.isNotEmpty()) {
                // 게시물 데이터를 먼저 저장
                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val savetime = dateFormat.format(System.currentTimeMillis())
                val data = mapOf(
                    "title" to binding.inputtitle.text.toString(),
                    "email" to MyApplication.email,
                    "stars" to binding.ratingBar.rating.toFloat(),
                    "comment" to binding.input.text.toString(),
                    "date_time" to savetime
                )
                MyApplication.db.collection("comments")
                    .add(data)
                    .addOnSuccessListener { documentReference ->
                        val docId = documentReference.id
                        if (uri != null) {
                            // 이미지가 있는 경우 이미지를 업로드
                            uploadImage(docId) {
                                // 이미지 업로드 성공 후 이미지 URL을 저장
                                val imageRef = MyApplication.storage.reference.child("images/${docId}.jpg")
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    MyApplication.db.collection("comments").document(docId)
                                        .update("image_url", uri.toString())
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "데이터 저장 성공", Toast.LENGTH_SHORT).show()
                                            // UI 업데이트 또는 액티비티 종료
                                            saveCurrentTimeToFile(savetime)
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "데이터 업데이트 실패", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        } else {
                            // 이미지가 없는 경우 바로 저장 완료
                            Toast.makeText(this, "데이터 저장 성공", Toast.LENGTH_SHORT).show()
                            saveCurrentTimeToFile(savetime)
                            finish()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "데이터 저장 실패", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "한줄평을 먼저 진행해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //파일 만들기
    private fun saveCurrentTimeToFile(currentTime: String) {
        val fileName = "write_time.txt"
        val file = File(filesDir, fileName)
        file.writeText(currentTime)
    }

    override fun onResume() {
        super.onResume()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        //문자열 변경
        val idStr = sharedPreferences.getString("settitle", "바다 후기 작성하기")
        binding.text.text = idStr

        //색깔 변경
        val color = sharedPreferences.getString("color", "#536DFE")
        binding.saveButton.setBackgroundColor(Color.parseColor(color))

        //스타일 변경
        val textStyle = sharedPreferences.getString("textStyle", "normal")
        val typefaceStyle = when (textStyle) {
            "bold" -> Typeface.BOLD
            "italic" -> Typeface.ITALIC
            "bold_italic" -> Typeface.BOLD_ITALIC
            else -> Typeface.NORMAL
        }
        binding.saveButton.setTypeface(null, typefaceStyle)

        //글자 크기 변경
        val size = sharedPreferences.getString("size", "16.0f")
        binding.tvId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size!!.toFloat())
    }

    //사진
    fun uploadImage(docId: String, callback: () -> Unit) {
        val imageRef = MyApplication.storage.reference.child("images/${docId}.jpg")
        val uploadTask = imageRef.putFile(uri!!)

        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "사진 업로드 성공", Toast.LENGTH_SHORT).show()
            callback()
        }

        uploadTask.addOnFailureListener {
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show()
        }
    }
}