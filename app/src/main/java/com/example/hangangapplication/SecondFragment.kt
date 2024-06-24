package com.example.hangangapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hangangapplication.databinding.FragmentOneBinding
import com.example.hangangapplication.databinding.FragmentSecondBinding
import com.example.hangangapplication.databinding.FragmentThreeBinding
import com.example.hangangapplication.databinding.ItemCommentBinding
import com.google.firebase.firestore.Query
import java.io.BufferedReader
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentSecondBinding

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
        binding = FragmentSecondBinding.inflate(inflater, container, false)

        binding.mainFab.setOnClickListener {
            if(MyApplication.checkAuth() || MyApplication.email != null){ //로그인한 사람만 가능하게
                startActivity(Intent(requireContext(), AddActivity::class.java))
            }
            else{
                Toast.makeText(requireContext(), "인증을 먼저 진행해주세요..", Toast.LENGTH_SHORT).show()
            }
        }

        updateLastSavedTime(binding)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val binding = view?.let { FragmentSecondBinding.bind(it) }
        if (binding != null) {
            updateLastSavedTime(binding)
        }
    }

    private fun updateLastSavedTime(binding: FragmentSecondBinding) {
        val file = File(requireContext().filesDir, "write_time.txt")
        if (file.exists()) {
            val readstream: BufferedReader = file.reader().buffered()
            binding.lastsaved.text = "마지막 작성시간: " + readstream.readLine() + "  "
        } else {
            binding.lastsaved.text = "아직 후기를 작성하지 않았습니다."
        }
    }

    override fun onStart() {
        super.onStart()

        if(MyApplication.checkAuth()){
            MyApplication.db.collection("comments")
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result->
                    val itemList = mutableListOf<ItemData>()
                    for( document in result){
                        val item = document.toObject(ItemData::class.java)
                        item.docId = document.id
                        itemList.add(item)
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerView.adapter = BoardAdapter(requireContext(), itemList)
                }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), "서버데이터 획득 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}