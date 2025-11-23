package com.example.coffee_app_damh.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.ItemsListCategoryAdapter
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.ViewModel.MainViewModel
import com.example.coffee_app_damh.databinding.ActivityItemsList2Binding
import java.util.ResourceBundle.getBundle

class ItemsListActivity : AppCompatActivity() {
    lateinit var binding: ActivityItemsList2Binding // đây là đối tượng được tạo tự động từ file layut xml để lay cac đối tượng
    private val viewModel= MainViewModel() //Cầu nối yêu cầu lấy dữ liệu
    private var id:String=""
    private var title:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityItemsList2Binding.inflate(layoutInflater) // thiết lập giao diện người dùng
        setContentView(binding.root)

        getBundle() // Để lay id và title từ intent
        initList() // bắt đầu quá trình tải  dữ liệu và hiển thị danh sách
    }

    private fun initList() {
        binding.apply {
            progressBar.visibility= View.VISIBLE // Báo hiệu cho người dùng quá trình tải đang diễn ra
// Activity gửi yêu cầu tải dữ liệu sản phẩm theo "id" đến ViewModel và trả ve mot LiveData
// Đăng ký observe đ theo dõi livedata đó
// Đoạn code bên trong Observer sẽ tự thực hiện khi có dữ liệu mới
// "it" là danh sách sản phẩm List<ItemModel> nhận được từ livedata
            viewModel.loadItems(id).observe(this@ItemsListActivity, Observer{
                listView.layoutManager=
                    LinearLayoutManager(this@ItemsListActivity, LinearLayoutManager.VERTICAL,false)
                    listView.adapter= ItemsListCategoryAdapter(it)
                progressBar.visibility=View.GONE //Ẩn progressbar vì quá trình tải hoàn tất
            })
            backBtn.setOnClickListener { finish() }
        }
    }
    private fun getBundle() {
        id=intent.getStringExtra("id")!!
        title=intent.getStringExtra("title")!!
    }
}