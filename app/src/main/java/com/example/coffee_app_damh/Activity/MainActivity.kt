package com.example.coffee_app_damh.Activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.Adapter.CategoryAdapter
import com.example.coffee_app_damh.Adapter.PopularAdapter
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
        //gọi các hàm khởi tạo dữ liệu giao diện
        initBanner()
        initCategory()
        initPopular()
    }

    // ------------------------- BANNER -----------------------------
    private fun initBanner() {
        // Hiện progress trong lúc chờ load dữ liệu
        binding.progressBarBanner.visibility = View.VISIBLE

        // Quan sát LiveData từ ViewModel
        viewModel.loadBanner().observeForever { list ->
            // Load ảnh banner đầu tiên bằng Glide
            Glide.with(this@MainActivity)
                .load(list[0].url)
                .into(binding.banner)

            // Tắt progress khi dữ liệu đã trả về
            binding.progressBarBanner.visibility = View.GONE
        }

        // Gửi yêu cầu load banner từ Firebase
        viewModel.loadBanner()
    }

    // ---------------------- CATEGORY ------------------------------
    private fun initCategory() {
        binding.progressCategory.visibility = View.VISIBLE

        // Quan sát dữ liệu category từ ViewModel
        viewModel.loadCategory().observeForever { list ->

            // Hiển thị Category theo dạng danh sách ngang
            binding.recyclerViewCat.layoutManager =
                LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

            // Gán Adapter để hiển thị danh sách Category
            binding.recyclerViewCat.adapter = CategoryAdapter(list)

            // Ẩn progress bar
            binding.progressCategory.visibility = View.GONE
        }

        // Bắt đầu tải dữ liệu Category từ Firebase
        viewModel.loadCategory()
    }

    // ---------------------- POPULAR ITEMS --------------------------
    private fun initPopular() {
        binding.progressBarPupolar.visibility = View.VISIBLE

        // Quan sát danh sách popular từ ViewModel
        viewModel.loadPopular().observeForever { list ->

            // Hiển thị dạng lưới 2 cột
            binding.recyclerViewPopular.layoutManager =
                GridLayoutManager(this, 2)

            // Set Adapter hiển thị danh sách món phổ biến
            binding.recyclerViewPopular.adapter = PopularAdapter(list)

            // Ẩn progress bar khi load xong
            binding.progressBarPupolar.visibility = View.GONE
        }

        // Gọi load dữ liệu từ Firebase
        viewModel.loadPopular()
    }
}