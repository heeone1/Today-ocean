package com.example.hangangapplication


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hangangapplication.databinding.ActivityMainBinding
import com.navercorp.nid.NaverIdLoginSDK


class MainActivity : AppCompatActivity(){
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = " "

        //시작 버튼 눌렀을 때
        binding.start.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        //로그인 버튼 눌렀을 때
        binding.gologin.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            if (binding.gologin.text == "로그인") {
                intent.putExtra("status", "logout")
            } else if (binding.gologin.text == "로그아웃") {
                intent.putExtra("status", "login")
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateLoginButton()
    }

    private fun updateLoginButton() {
        if (MyApplication.auth.currentUser != null || MyApplication.email != null) {
            binding.gologin.text = "로그아웃"
        } else {
            binding.gologin.text = "로그인"
        }
    }
}