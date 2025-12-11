// File: /app/src/main/java/com/example/coffee_app_damh/Activity/OrderHistoryActivity.kt
// HÃY THAY THẾ TOÀN BỘ FILE CŨ BẰNG NỘI DUNG NÀY

package com.example.coffee_app_damh.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.OrderHistoryAdapter // Adapter mới, đơn giản
import com.example.coffee_app_damh.Domain.OrderModel
import com.example.coffee_app_damh.databinding.ActivityOrderHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.backBtn.setOnClickListener { finish() }
        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        binding.progressBar.visibility = View.VISIBLE
        binding.historyRecyclerView.visibility = View.GONE
        binding.emptyTxt.visibility = View.GONE

        val userId = auth.currentUser?.uid
        if (userId == null) {
            binding.progressBar.visibility = View.GONE
            binding.emptyTxt.visibility = View.VISIBLE
            binding.emptyTxt.text = "Vui lòng đăng nhập để xem lịch sử"
            return
        }

        // === LOGIC ĐÃ ĐƠN GIẢN HÓA: CHỈ CẦN LẤY ĐƠN HÀNG ===
        val ordersQuery = database.getReference("Orders").orderByChild("userId").equalTo(userId)
        ordersQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                    .sortedByDescending { it.orderDate } // Sắp xếp đơn mới nhất lên đầu

                binding.progressBar.visibility = View.GONE

                if (orders.isEmpty()) {
                    binding.emptyTxt.visibility = View.VISIBLE
                } else {
                    binding.historyRecyclerView.visibility = View.VISIBLE
                    binding.historyRecyclerView.layoutManager = LinearLayoutManager(this@OrderHistoryActivity)

                    // Gọi đúng adapter mới, không cần truyền allItemsMap nữa
                    binding.historyRecyclerView.adapter = OrderHistoryAdapter(orders)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                binding.emptyTxt.text = "Lỗi khi tải dữ liệu."
                binding.emptyTxt.visibility = View.VISIBLE
            }
        })
    }
}
