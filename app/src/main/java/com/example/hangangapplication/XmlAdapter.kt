package com.example.hangangapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hangangapplication.MyApplication.Companion.email
import com.example.hangangapplication.databinding.ItemMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject

class XmlViewHolder(val binding: ItemMainBinding): RecyclerView.ViewHolder(binding.root)
class XmlAdapter(val datas:MutableList<myXmlItem>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {     // 1-1
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return XmlViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as XmlViewHolder).binding
        val model = datas!![position]           // 1-2

        // 1-3
        binding.name1.text = model.sidoNm
        binding.name2.text = model.gugunNm
        binding.name3.text = model.staNm
        binding.water2.text = model.resYn
        binding.where.text = model.resLocDetail
        binding.lat.text = model.lat
        binding.lon.text = model.lon

        val email = MyApplication.email
        if (email != null) {
            updateScrapButtonText(binding, email, model)
        }

        binding.map.setOnClickListener {
            val latitude = binding.lat.text.toString()
            val longitude = binding.lon.text.toString()

            if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                val uri = Uri.parse("geo:$latitude,$longitude")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (intent.resolveActivity(binding.root.context.packageManager) != null) {
                    binding.root.context.startActivity(intent)
                }
            }
        }

        binding.btnScrap.setOnClickListener {
            if (email != null) {
                if (isScrapped(binding.root.context, email, model)) {
                    removeScrapData(binding.root.context, email, model)
                } else {
                    saveScrapData(binding.root.context, email, model)
                }
                // Update the button text after changing scrap status
                updateScrapButtonText(binding, email, model)
            }
        }
    }

    private fun updateScrapButtonText(binding: ItemMainBinding, email: String, model: myXmlItem) {
        if (isScrapped(binding.root.context, email, model)) {
            binding.btnScrap.text = "스크랩취소"
        } else {
            binding.btnScrap.text = "스크랩"
        }
    }

    private fun saveScrapData(context: Context, email: String, model: myXmlItem) {
        val sharedPreferences = context.getSharedPreferences("scrap_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val scrapList: MutableList<myXmlItem> = getScrapList(context, email)
        scrapList.add(model)

        val json = gson.toJson(scrapList)
        editor.putString(email, json)
        editor.apply()
    }

    private fun removeScrapData(context: Context, email: String, model: myXmlItem) {
        val sharedPreferences = context.getSharedPreferences("scrap_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val scrapList: MutableList<myXmlItem> = getScrapList(context, email)
        scrapList.removeAll { it.sidoNm == model.sidoNm && it.gugunNm == model.gugunNm && it.staNm == model.staNm }

        val json = gson.toJson(scrapList)
        editor.putString(email, json)
        editor.apply()
    }

    private fun isScrapped(context: Context, email: String, model: myXmlItem): Boolean {
        val scrapList: MutableList<myXmlItem> = getScrapList(context, email)
        return scrapList.any { it.sidoNm == model.sidoNm && it.gugunNm == model.gugunNm && it.staNm == model.staNm }
    }

    private fun getScrapList(context: Context, email: String): MutableList<myXmlItem> {
        val sharedPreferences = context.getSharedPreferences("scrap_data", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(email, null)
        val type = object : TypeToken<MutableList<myXmlItem>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }
}