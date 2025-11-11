package com.example.coffee_app_damh.Activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.Adapter.CategoryAdapter
import com.example.coffee_app_damh.ViewModel.MainViewModel
import com.example.coffee_app_damh.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding //binding là biến dùng để truy cập giao diện và MainBinding tự động sinh ra
    //Lúc này binding “gắn” file XML vào Activity, và bạn có thể gọi trực tiếp mọi View trong XML mà không cần findViewById() nữa.
    private val viewModel= MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBanner()
        initCategory()
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility= View.VISIBLE
        viewModel.loadBanner().observeForever {
            Glide.with(this@MainActivity)
                .load(it[0].url)
                .into(binding.banner)
            binding.progressBarBanner.visibility= View.GONE
        }
        viewModel.loadBanner()
    }

    private fun initCategory(){
        binding.progressCategory.visibility= View.VISIBLE
        viewModel.loadCategory().observeForever {
            binding.recyclerViewCat.layoutManager =
                LinearLayoutManager(
                    this@MainActivity, LinearLayoutManager.HORIZONTAL,
                    false)

            binding.recyclerViewCat.adapter= CategoryAdapter(it)
            binding.progressCategory.visibility= View.GONE
        }
        viewModel.loadCategory()
    }
}