package com.example.coffee_app_damh.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.StatisticsAdapter
import com.example.coffee_app_damh.Domain.OrderModel
import com.example.coffee_app_damh.Domain.StatisticModel
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.databinding.ActivityStatisticsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private val database = FirebaseDatabase.getInstance()
    //Kết nối firebase và tải tất cả order đổ
    private val allOrders = ArrayList<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        loadAllOrders()
        setupTabs()
    }

    private fun setupTabs() {
        binding.btnDaily.setOnClickListener {
            setActiveTab(1)
            calculateRevenueByDay()
        }
        binding.btnMonthly.setOnClickListener {
            setActiveTab(2)
            calculateRevenueByMonth()
        }
        binding.btnTopProducts.setOnClickListener {
            setActiveTab(3)
            calculateTopProducts()
        }
    }

    private fun setActiveTab(index: Int) {
        val activeColor = ContextCompat.getColor(this, R.color.orange)
        val inactiveColor = ContextCompat.getColor(this, R.color.white)
        val activeText = ContextCompat.getColor(this, R.color.white)
        val inactiveText = ContextCompat.getColor(this, R.color.darkBrown)

        binding.btnDaily.setBackgroundColor(if (index == 1) activeColor else inactiveColor)
        binding.btnDaily.setTextColor(if (index == 1) activeText else inactiveText)

        binding.btnMonthly.setBackgroundColor(if (index == 2) activeColor else inactiveColor)
        binding.btnMonthly.setTextColor(if (index == 2) activeText else inactiveText)

        binding.btnTopProducts.setBackgroundColor(if (index == 3) activeColor else inactiveColor)
        binding.btnTopProducts.setTextColor(if (index == 3) activeText else inactiveText)
    }
//Kết nối đến node Orders, tải tất cả đơn hàng và đổ vào allOrders
    private fun loadAllOrders() {
        binding.progressBar.visibility = View.VISIBLE

        database.getReference("Orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allOrders.clear()
                for (child in snapshot.children) {
                    val order = child.getValue(OrderModel::class.java)
                    if (order != null) {
                        allOrders.add(order)
                    }
                }
                binding.progressBar.visibility = View.GONE

                // === TÍNH TOÁN 4 Ô TRẠNG THÁI ===
                calculateOrderStatus()

                // Mặc định hiển thị theo ngày
                setActiveTab(1)
                calculateRevenueByDay()
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@StatisticsActivity, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calculateOrderStatus() {
        var completedCount = 0
        var processingCount = 0
        var shippingCount = 0
        var cancelledCount = 0

        for (order in allOrders) {
            when (order.status) {
                "Hoàn thành" -> completedCount++
                "Đang xử lý" -> processingCount++
                "Đang giao" -> shippingCount++
                "Đã hủy" -> cancelledCount++
            }
        }

        binding.countCompletedTxt.text = completedCount.toString()
        binding.countProcessingTxt.text = processingCount.toString()
        binding.countShippingTxt.text = shippingCount.toString()
        binding.countCancelledTxt.text = cancelledCount.toString()
    }

    // 1. DOANH THU THEO NGÀY
    private fun calculateRevenueByDay() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val resultMap = TreeMap<String, Double>()

        var totalRevenue = 0.0

        for (order in allOrders) {
            if (order.status != "Đã hủy") {
                val dateKey = dateFormat.format(Date(order.orderDate))
                val currentVal = resultMap[dateKey] ?: 0.0
                resultMap[dateKey] = currentVal + order.total
                totalRevenue += order.total
            }
        }

        val listStats = ArrayList<StatisticModel>()
        var rank = 1
        for ((date, revenue) in resultMap.descendingMap()) {
            listStats.add(StatisticModel(rank++, date, revenue))
        }
        updateUI(listStats, totalRevenue, "Tổng doanh thu (Theo Ngày)")
    }

    // 2. DOANH THU THEO THÁNG
    private fun calculateRevenueByMonth() {
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val resultMap = TreeMap<String, Double>()
        var totalRevenue = 0.0

        for (order in allOrders) {
            if (order.status != "Đã hủy") {
                val dateKey = dateFormat.format(Date(order.orderDate))
                val currentVal = resultMap[dateKey] ?: 0.0
                resultMap[dateKey] = currentVal + order.total
                totalRevenue += order.total
            }
        }

        val listStats = ArrayList<StatisticModel>()
        var rank = 1
        for ((date, revenue) in resultMap.descendingMap()) {
            listStats.add(StatisticModel(rank++, "Tháng $date", revenue))
        }
        updateUI(listStats, totalRevenue, "Tổng doanh thu (Theo Tháng)")
    }

    // 3. TOP SẢN PHẨM
    private fun calculateTopProducts() {
        val quantityMap = HashMap<String, Int>()
        val revenueMap = HashMap<String, Double>()

        // Biến này dùng để tính TỔNG SỐ LƯỢNG của tất cả các món
        var totalQuantityAllProducts = 0

        for (order in allOrders) {
            if (order.status != "Đã hủy") {
                for (item in order.items) {
                    val key = item.title

                    val currentQty = quantityMap[key] ?: 0
                    quantityMap[key] = currentQty + item.quantity

                    val currentRev = revenueMap[key] ?: 0.0
                    val itemRevenue = item.priceAtOrder * item.quantity
                    revenueMap[key] = currentRev + itemRevenue

                    // Cộng dồn vào tổng số lượng toàn bộ
                    totalQuantityAllProducts += item.quantity
                }
            }
        }

        val sortedList = quantityMap.toList().sortedByDescending { (_, value) -> value }

        val listStats = ArrayList<StatisticModel>()
        var rank = 1
        for ((name, qty) in sortedList) {
            val revenue = revenueMap[name] ?: 0.0
            listStats.add(StatisticModel(rank++, name, revenue, "Đã bán: $qty"))
        }

        // --- SỬA TẠI ĐÂY: Truyền tổng số lượng nhưng ép kiểu về Double để khớp tham số hàm updateUI ---
        // Tham số thứ 3 là title, ta sửa lại cho rõ nghĩa
        updateUI(listStats, totalQuantityAllProducts.toDouble(), "Top Sản Phẩm (Theo số lượng)")
    }


    private fun updateUI(list: List<StatisticModel>, totalVal: Double, title: String) {
        val formatter = DecimalFormat("###,###")

        binding.titleSummaryTxt.text = title

        if (totalVal > 0) {
            // KIỂM TRA: Nếu tiêu đề chứa chữ "Top" (tức là đang ở Tab 3), thì hiển thị số lượng
            if (title.contains("Top")) {
                // Ép kiểu về số nguyên để bỏ phần thập phân (.0)
                val quantity = totalVal.toInt()
                binding.totalSummaryTxt.text = "$quantity"
            } else {
                // Các trường hợp khác (Ngày/Tháng) thì hiển thị Tiền ($)
                binding.totalSummaryTxt.text = "$${formatter.format(totalVal)}"
            }
            binding.totalSummaryTxt.visibility = View.VISIBLE
        } else {
            binding.totalSummaryTxt.visibility = View.GONE
        }

        if (list.isEmpty()) {
            binding.emptyTxt.visibility = View.VISIBLE
            binding.statsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyTxt.visibility = View.GONE
            binding.statsRecyclerView.visibility = View.VISIBLE
            binding.statsRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.statsRecyclerView.adapter = StatisticsAdapter(list)
        }
    }

}
