package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.ManageOrdersAdapter
import com.example.coffee_app_damh.Domain.OrderModel
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.databinding.ActivityManageOrdersBinding
import com.google.firebase.database.*

class ManageOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageOrdersBinding
    private lateinit var database: FirebaseDatabase
    private val allOrders = ArrayList<OrderModel>() // Danh sách gốc
    private val displayOrders = ArrayList<OrderModel>() // Danh sách hiển thị sau khi lọc
    private lateinit var adapter: ManageOrdersAdapter
    private var currentFilter = "Tất cả"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        binding.backBtn.setOnClickListener { finish() }

        setupRecyclerView()
        setupFilters()
        loadOrders()
    }

    private fun setupRecyclerView() {
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)

        // --- SỬA TẠI ĐÂY ---
        adapter = ManageOrdersAdapter(displayOrders) { order ->
            // Khi click vào item, chuyển sang màn hình Chi tiết
            val intent = Intent(this, OrderDetailAdminActivity::class.java)
            intent.putExtra("object", order) // Truyền object OrderModel sang
            startActivity(intent)
        }
        // -------------------

        binding.ordersRecyclerView.adapter = adapter
    }


    private fun setupFilters() {
        val buttons = listOf(
            binding.filterAll, binding.filterProcessing,
            binding.filterShipping, binding.filterCompleted, binding.filterCancelled
        )

        val clickListener = View.OnClickListener { view ->
            val btn = view as AppCompatButton
            currentFilter = btn.text.toString()

            // 1. Cập nhật giao diện nút bấm
            buttons.forEach {
                updateFilterButtonStyle(it, it == btn)
            }

            // 2. Lọc danh sách
            filterOrders()
        }

        buttons.forEach { it.setOnClickListener(clickListener) }

        // Mặc định chọn "Tất cả"
        updateFilterButtonStyle(binding.filterAll, true)
    }

    private fun updateFilterButtonStyle(btn: AppCompatButton, isSelected: Boolean) {
        val bg = GradientDrawable()
        bg.shape = GradientDrawable.RECTANGLE
        bg.cornerRadius = 50f

        if (isSelected) {
            bg.setColor(Color.parseColor("#42A5F5")) // Màu xanh khi chọn
            btn.setTextColor(Color.WHITE)
        } else {
            bg.setColor(Color.parseColor("#F0F0F0")) // Màu xám khi không chọn
            btn.setTextColor(Color.GRAY)
        }
        btn.background = bg
    }

    private fun filterOrders() {
        displayOrders.clear()
        if (currentFilter == "Tất cả") {
            displayOrders.addAll(allOrders)
        } else {
            displayOrders.addAll(allOrders.filter { it.status == currentFilter })
        }

        adapter.notifyDataSetChanged()

        binding.emptyTxt.visibility = if (displayOrders.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun loadOrders() {
        binding.progressBar.visibility = View.VISIBLE
        val ordersRef = database.getReference("Orders")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allOrders.clear()
                for (childSnapshot in snapshot.children) {
                    val order = childSnapshot.getValue(OrderModel::class.java)
                    if (order != null) {
                        allOrders.add(order)
                    }
                }
                allOrders.sortByDescending { it.orderDate }

                // Sau khi tải xong thì lọc lại theo tab hiện tại
                filterOrders()

                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ManageOrdersActivity, "Lỗi tải: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
