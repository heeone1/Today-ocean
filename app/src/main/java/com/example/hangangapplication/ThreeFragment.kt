package com.example.hangangapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hangangapplication.databinding.FragmentOneBinding
import com.example.hangangapplication.databinding.FragmentThreeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import java.io.BufferedReader
import java.io.File
import java.util.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThreeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThreeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentThreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThreeBinding.inflate(inflater, container, false)
        loadScrapData()

        val random = Random()
        val num = random.nextInt(5)
        lifecycle.addObserver(binding.youtubePlayerView1)
        binding.youtubePlayerView1.addYouTubePlayerListener(object: AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                val videoId : String
                if(num==1) videoId = "9oLlidhld9c"
                else if(num==2) videoId = "0obxm6JqN5Q"
                else if(num==3) videoId = "K3KkoIHbZtQ"
                else if(num==4) videoId = "hOF-p4GnLmI"
                else videoId = "RkKutWxa_bg"
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })

        return binding.root
    }

    fun loadScrapData() {
        val email = MyApplication.email
        binding.scrapContainer.removeAllViews()

        if (email != null) {
            val scrapList = getScrapList(email)

            if (scrapList.isEmpty()) {
                val textView = TextView(context).apply {
                    text = "스크랩된 글이 없습니다."
                    textSize = 16f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                binding.scrapContainer.addView(textView)
            } else {
                scrapList.forEach { item ->
                    val textView = TextView(context).apply {
                        text = "${item.sidoNm}, ${item.gugunNm}, ${item.staNm}"
                        textSize = 16f
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    binding.scrapContainer.addView(textView)
                }
            }
        } else {
            showLoginRequiredMessage()
        }
    }

    fun showLoginRequiredMessage() {
        binding.scrapContainer.removeAllViews()
        val textView = TextView(context).apply {
            text = "로그인이 필요합니다."
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        binding.scrapContainer.addView(textView)
    }

    private fun getScrapList(email: String): MutableList<myXmlItem> {
        val sharedPreferences = activity?.getSharedPreferences("scrap_data", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences?.getString(email, null)
        val type = object : TypeToken<MutableList<myXmlItem>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }


    override fun onStart() {
        super.onStart()

        if (MyApplication.checkAuth() || MyApplication.email != null) {
            binding.tvUserId.text = "아이디: ${MyApplication.email}"
            loadScrapData()
        } else {
            binding.tvUserId.text = "아이디: 로그인이 필요합니다."
        }
    }

    override fun onResume() {
        super.onResume()
        loadScrapData()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThreeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThreeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}