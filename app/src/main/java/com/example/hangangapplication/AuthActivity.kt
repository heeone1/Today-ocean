package com.example.hangangapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hangangapplication.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class AuthActivity : AppCompatActivity() {
    lateinit var binding:ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_auth)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "오늘, 바다"

        changeVisibility(intent.getStringExtra("status").toString())

        binding.goSignInBtn.setOnClickListener {    // 회원 가입 Button
            changeVisibility("signin")
        }

        binding.signBtn.setOnClickListener {    // 가입 Button
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener{sendTask ->
                                if(sendTask.isSuccessful){
                                    Toast.makeText(baseContext,"회원가입 성공!!.. 메일을 확인해주세요",Toast.LENGTH_SHORT).show()
                                    Log.d("mobileapp", "회원가입 성공!!")
                                    changeVisibility("logout")
                                }
                                else{
                                    Toast.makeText(baseContext,"메일발송 실패",Toast.LENGTH_SHORT).show()
                                    Log.d("mobileapp", "메일발송 실패")
                                    changeVisibility("logout")
                                }
                            }
                    }
                    else{
                        Toast.makeText(baseContext,"회원가입 실패",Toast.LENGTH_SHORT).show()
                        Log.d("mobileapp", "== ${task.exception} ==")
                        changeVisibility("logout")
                    }
                }
        }

        binding.loginBtn.setOnClickListener {   // 로그인 Button
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){
                        if(MyApplication.checkAuth()){
                            MyApplication.email = email
                            Log.d("mobileapp", "로그인 성공")
                            startActivity(Intent(this, MainActivity2::class.java))
                        }
                        else{
                            Toast.makeText(baseContext,"이메일 인증이 되지 않았습니다.",Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "이메일 인증 안됨")
                        }
                    }
                    else{
                        Toast.makeText(baseContext,"로그인 실패",Toast.LENGTH_SHORT).show()
                        Log.d("mobileapp", "로그인 실패")
                    }
                }
        }


        binding.logoutBtn.setOnClickListener {  // 로그아웃 Button
            MyApplication.auth.signOut()
            NaverIdLoginSDK.logout()
            MyApplication.email = null
            Log.d("mobileapp", "로그 아웃")
            finish()
        }

        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            Log.d("mobileapp","account1 : ${task.toString()}")
            try{
                val account = task.getResult(ApiException::class.java)
                val crendential = GoogleAuthProvider.getCredential(account.idToken, null)
                MyApplication.auth.signInWithCredential(crendential)
                    .addOnCompleteListener(this){task ->
                        if(task.isSuccessful){
                            MyApplication.email = account.email
                            Toast.makeText(baseContext,"구글 로그인 성공",Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "구글 로그인 성공")
                            finish()
                        }
                        else{
                            changeVisibility("logout")
                            Toast.makeText(baseContext,"구글 로그인 실패",Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "구글 로그인 실패")
                        }
                    }
            }catch (e: ApiException){ // APIException은 이미 지정된 exception말고 custom한 exception을 만들어서 쓰고 싶을때 사용
                changeVisibility("logout")
                Toast.makeText(baseContext,"구글 로그인 Exception : ${e.printStackTrace()},${e.statusCode}",Toast.LENGTH_SHORT).show()
                Log.d("mobileapp", "구글 로그인 Exception : ${e.message}, ${e.statusCode}")
            }
        }

        //구글 로그인
        binding.googleLoginBtn.setOnClickListener {
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build()
            val signInIntent = GoogleSignIn.getClient(this,gso).signInIntent
            requestLauncher.launch(signInIntent)
        }

        //네이버 로그인
        binding.naverLoginBtn.setOnClickListener{
            val oAuthLoginCallback = object: OAuthLoginCallback{
                override fun onSuccess() {
                    NidOAuthLogin().callProfileApi(object:NidProfileCallback<NidProfileResponse>{
                        override fun onSuccess(result: NidProfileResponse) {
                            MyApplication.email = result.profile?.email.toString()
                            finish()
                        }

                        override fun onError(errorCode: Int, message: String) {

                        }

                        override fun onFailure(httpStatus: Int, message: String) {

                        }
                    })
                }
                override fun onError(errorCode: Int, message: String) {

                }

                override fun onFailure(httpStatus: Int, message: String) {

                }
            }
            NaverIdLoginSDK.initialize(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), "mobilapp")
            NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
        }

    }


    fun changeVisibility(mode:String){
        if(mode.equals("login")){       // 현재 로그인 상태
            binding.run{
                authMainTextView.text = "정말 로그아웃하시겠습니까?"
                authMainTextView.visibility = View.VISIBLE
                logoutBtn.visibility = View.VISIBLE
                goSignInBtn.visibility = View.GONE
                authEmailEditView.visibility = View.GONE
                authPasswordEditView.visibility = View.GONE
                signBtn.visibility = View.GONE
                loginBtn.visibility= View.GONE
                googleLoginBtn.visibility = View.GONE
                naverLoginBtn.visibility = View.GONE

                line1.visibility = View.GONE
                line2.visibility = View.GONE
            }
        }
        else if(mode.equals("logout")){ // 현재 로그아웃 상태
            binding.run{
                authMainTextView.visibility = View.GONE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.GONE
                loginBtn.visibility= View.VISIBLE
                googleLoginBtn.visibility = View.VISIBLE
                naverLoginBtn.visibility = View.VISIBLE

                line1.visibility = View.VISIBLE
                line2.visibility = View.VISIBLE
            }
        }
        else if(mode.equals("signin")){    // 회원가입 버튼 클릭 : 회원가입 진행 상태
            binding.run{
                authMainTextView.visibility = View.GONE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                loginBtn.visibility= View.GONE
                googleLoginBtn.visibility = View.GONE
                naverLoginBtn.visibility = View.GONE

                line1.visibility = View.GONE
                line2.visibility = View.GONE
            }
        }
    }
}