package com.example.hangangapplication

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hangangapplication.databinding.ActivityMain2Binding
import com.example.hangangapplication.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityMain2Binding

    // DrawerLayout Toggle
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var headerView : View

    class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
        val fragments : List<Fragment>
        init{
            fragments =
                listOf(OneFragment(), SecondFragment(), ThreeFragment())
        }

        // 반드시 포함해야하는 오버라이드 함수 -> getItemCount()
        override fun getItemCount(): Int {
            return fragments.size
        }
        // 반드시 포함해야하는 오버라이드 함수 -> createFragment()
        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "오늘, 바다"

        // DrawerLayout Toggle
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawer,
            R.string.drawer_opened,
            R.string.drawer_closed
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        // Drawer 메뉴
        binding.mainDrawerView.setNavigationItemSelectedListener(this)

        headerView = binding.mainDrawerView.getHeaderView(0)
        val button = headerView.findViewById<Button>(R.id.btnAuth)
        button.setOnClickListener {
            Log.d("mobileapp", "button.setOnClickListener")
            val intent = Intent(this, AuthActivity::class.java)
            if(button.text.equals("로그인")){
                intent.putExtra("status", "logout")
            }
            else if(button.text.equals("로그아웃")){
                intent.putExtra("status", "login")
            }

            startActivity(intent)

            binding.drawer.closeDrawers()
        }

        // 프래그먼트 어댑터 생성
        val fragmentAdapter = MyFragmentPagerAdapter(this)
        binding.viewpager.adapter = fragmentAdapter

        val tabTitles = arrayOf("바다 정보", "바다 후기", "마이페이지")

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }


    // DrawerLayout Toggle
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    // Drawer 메뉴
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_setting -> {
                Log.d("mobileapp", "설정 메뉴")
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                true
            }
        }
        return false
    }

    override fun onStart(){
        super.onStart()

        val button = headerView.findViewById<Button>(R.id.btnAuth)
        val tv = headerView.findViewById<TextView>(R.id.tvID)

        if(MyApplication.checkAuth() || MyApplication.email != null){
            button.text = "로그아웃"
            tv.text = "${MyApplication.email}님 \n 반갑습니다."
        }
        else{
            button.text = "로그인"
            tv.text = "안녕하세요.."
        }
    }

}