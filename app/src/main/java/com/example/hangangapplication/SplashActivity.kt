package com.example.hangangapplication

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hangangapplication.databinding.ActivityAddBinding
import com.example.hangangapplication.databinding.ActivitySplashBinding
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animatedImage = findViewById<ImageView>(R.id.animated_image)
        val animationDrawable = animatedImage.drawable as AnimationDrawable
        animationDrawable.start()

        val backgroundExe : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        val mainExe:Executor = ContextCompat.getMainExecutor(this)
        backgroundExe.schedule({
            mainExe.execute({ //쓰레드 실행
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
        }, 3,TimeUnit.SECONDS)

    }
}