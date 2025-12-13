// File: /app/src/main/java/com/example/coffee_app_damh/Activity/OrderSuccessActivity.kt
package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.CheckoutAdapter // TÁI SỬ DỤNG ADAPTER NÀY
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.databinding.ActivityOrderSuccessBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Lấy tất cả dữ liệu đã được gửi từ CheckoutActivity
        val orderId = intent.getStringExtra("orderId") ?: ""
        val orderDate = intent.getLongExtra("orderDate", 0)
        val name = intent.getStringExtra("name") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val total = intent.getDoubleExtra("total", 0.0)

        // Lấy danh sách sản phẩm từ Intent
        @Suppress("DEPRECATION") // Dùng để ẩn cảnh báo vì getSerializableExtra đã cũ nhưng vẫn hoạt động tốt
        val items = intent.getSerializableExtra("items") as? ArrayList<ItemsModel> ?: arrayListOf()

        // 2. Hiển thị thông tin lên giao diện
        displayOrderInfo(orderId, orderDate, name, address, total)
        initItemsRecyclerView(items) // <-- Gọi hàm để hiển thị danh sách sản phẩm

        // 3. Xử lý nút bấm "Về Trang Chủ"
        binding.backToHomeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun displayOrderInfo(orderId: String, orderDate: Long, name: String, address: String, total: Double) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formatter = DecimalFormat("###,###.## VNĐ")

        val dateString = dateFormat.format(Date(orderDate))
        val totalString = formatter.format(total)

        // Tạo chuỗi thông tin giao hàng (không bao gồm tổng tiền)
        val info = """
            Mã đơn hàng: #${orderId.take(6).uppercase()}
            Ngày đặt: $dateString
            Người nhận: $name
            Địa chỉ: $address
        """.trimIndent()

        binding.orderInfoTxt.text = info
        binding.totalTxt.text = totalString // Gán tổng tiền vào TextView riêng
    }

    // HÀM MỚI ĐỂ HIỂN THỊ DANH SÁCH SẢN PHẨM
    private fun initItemsRecyclerView(items: ArrayList<ItemsModel>) {
        if(items.isNotEmpty()) {
            binding.itemsRecyclerView.layoutManager = LinearLayoutManager(this)
            // Tái sử dụng CheckoutAdapter mà không cần thay đổi gì
            val adapter = CheckoutAdapter(items)
            binding.itemsRecyclerView.adapter = adapter
        }
    }

    // Chặn người dùng nhấn nút Back vật lý của điện thoại để đóng popup

}
