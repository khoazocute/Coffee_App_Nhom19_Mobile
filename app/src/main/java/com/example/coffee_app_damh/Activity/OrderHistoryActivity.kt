// File: /app/src/main/java/com/example/coffee_app_damh/Activity/OrderHistoryActivity.kt
// HÃY THAY THẾ TOÀN BỘ FILE CŨ BẰNG NỘI DUNG NÀY

package com.example.coffee_app_damh.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.OrderHistoryAdapter
import com.example.coffee_app_damh.Domain.OrderModel
import com.example.coffee_app_damh.databinding.ActivityOrderHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var targetUserId: String? = null // Biến lưu ID người cần xem (nếu có)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // 1. Nhận dữ liệu userId từ Admin gửi sang (nếu có)
        targetUserId = intent.getStringExtra("userId")

        // 2. Thay đổi tiêu đề nếu là Admin đang xem
        if (targetUserId != null) {
            // Nếu bạn có TextView tiêu đề trong layout, hãy set lại text
            // Ví dụ: binding.titleTxt.text = "Lịch sử (Admin View)"
            // Nếu không có ID titleTxt trong XML thì bỏ qua dòng này
        }

        binding.backBtn.setOnClickListener { finish() }
        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        binding.progressBar.visibility = View.VISIBLE
        binding.historyRecyclerView.visibility = View.GONE
        binding.emptyTxt.visibility = View.GONE

        // 3. QUAN TRỌNG: Quyết định xem lấy đơn hàng của ai?
        // - Nếu targetUserId có dữ liệu -> Lấy của user đó (Admin xem)
        // - Nếu không -> Lấy của chính mình (User xem)
        val userIdToLoad = targetUserId ?: auth.currentUser?.uid

        if (userIdToLoad == null) {
            binding.progressBar.visibility = View.GONE
            binding.emptyTxt.visibility = View.VISIBLE
            binding.emptyTxt.text = "Không xác định được người dùng."
            return
        }

        // 4. Truy vấn Firebase
        val ordersQuery = database.getReference("Orders").orderByChild("userId").equalTo(userIdToLoad)

        ordersQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                    .sortedByDescending { it.orderDate } // Sắp xếp mới nhất lên đầu

                binding.progressBar.visibility = View.GONE

                if (orders.isEmpty()) {
                    binding.emptyTxt.visibility = View.VISIBLE
                    binding.emptyTxt.text = "Người dùng này chưa có đơn hàng nào."
                } else {
                    binding.historyRecyclerView.visibility = View.VISIBLE
                    binding.historyRecyclerView.layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
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
