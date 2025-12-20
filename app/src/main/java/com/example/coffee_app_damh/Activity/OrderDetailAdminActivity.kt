package com.example.coffee_app_damh.Activity

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.OrderDetailAdapter
import com.example.coffee_app_damh.Domain.OrderModel
import com.example.coffee_app_damh.databinding.ActivityOrderDetailAdminBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderDetailAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailAdminBinding
    private lateinit var order: OrderModel
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // === THAY ĐỔI QUAN TRỌNG TẠI ĐÂY (Parcelable) ===
        // Cách lấy dữ liệu Parcelable
        val receivedOrder = intent.getParcelableExtra<OrderModel>("object")

        if (receivedOrder != null) {
            order = receivedOrder
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy dữ liệu đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        // ===============================================

        // 2. Hiển thị dữ liệu lên giao diện
        initUI()

        // 3. Hiển thị danh sách sản phẩm
        initProductsList()

        // 4. Xử lý sự kiện nút bấm
        binding.backBtn.setOnClickListener { finish() }

        binding.updateStatusBtn.setOnClickListener {
            showUpdateStatusDialog()
        }
    }

    // ... (Giữ nguyên các hàm initUI, initProductsList, showUpdateStatusDialog bên dưới không đổi) ...

    private fun initUI() {
        val formatter = DecimalFormat("###,###")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        binding.apply {
            orderIdTxt.text = "#${order.orderId.takeLast(6).uppercase()}"
            orderDateTxt.text = dateFormat.format(Date(order.orderDate))

            customerNameTxt.text = "Tên: ${order.name}"
            phoneTxt.text = "SĐT: ${order.phone}"
            addressTxt.text = "Địa chỉ: ${order.address}"

            subTotalTxt.text = "$ ${formatter.format(order.subTotal)} "
            deliveryTxt.text = "$ ${formatter.format(order.delivery)} "
            totalTxt.text = "$ ${formatter.format(order.total)} "

            updateStatusColor(order.status)
        }
    }

    private fun updateStatusColor(status: String) {
        binding.statusTxt.text = status.uppercase()
        val color = when(status) {
            "Đang xử lý" -> "#FFA726"
            "Đang giao" -> "#42A5F5"
            "Hoàn thành" -> "#66BB6A"
            "Đã hủy" -> "#EF5350"
            else -> "#BDBDBD"
        }
        try {
            binding.statusTxt.setTextColor(Color.parseColor(color))
        } catch (e: Exception) {
            binding.statusTxt.setTextColor(Color.GRAY)
        }
    }

    private fun initProductsList() {
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerView.adapter = OrderDetailAdapter(order.items)
    }

    private fun showUpdateStatusDialog() {
        val statuses = arrayOf("Đang xử lý", "Đang giao", "Hoàn thành", "Đã hủy")
        var checkedItem = statuses.indexOf(order.status)
        if (checkedItem == -1) checkedItem = 0

        AlertDialog.Builder(this)
            .setTitle("Cập nhật trạng thái")
            .setSingleChoiceItems(statuses, checkedItem) { dialog, which ->
                val newStatus = statuses[which]
                updateOrderStatus(newStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Đóng", null)
            .show()
    }

    private fun updateOrderStatus(newStatus: String) {
        database.getReference("Orders").child(order.orderId).child("status").setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã cập nhật: $newStatus", Toast.LENGTH_SHORT).show()
                order.status = newStatus
                updateStatusColor(newStatus)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show()
            }
    }
}
