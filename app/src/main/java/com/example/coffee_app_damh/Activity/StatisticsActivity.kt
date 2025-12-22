// HÃY THAY THẾ TOÀN BỘ FILE StatisticsActivity.kt CŨ BẰNG NỘI DUNG NÀY
package com.example.coffee_app_damh.Activity

import android.graphics.Color
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
import com.google.firebase.database.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// Import thư viện biểu đồ BarChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

// Import thư viện biểu đồ PieChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private val database = FirebaseDatabase.getInstance()
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
        binding.btnDaily.setOnClickListener { setActiveTab(1) }
        binding.btnMonthly.setOnClickListener { setActiveTab(2) }
        binding.btnTopProducts.setOnClickListener { setActiveTab(3) }
        binding.btnChart.setOnClickListener { setActiveTab(4) }
    }

    private fun setActiveTab(index: Int) {
        val buttons = listOf(binding.btnDaily, binding.btnMonthly, binding.btnTopProducts, binding.btnChart)
        val activeColor = ContextCompat.getColor(this, R.color.orange)
        val inactiveColor = ContextCompat.getColor(this, android.R.color.transparent)
        val activeTextColor = ContextCompat.getColor(this, R.color.white)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.grey)

        buttons.forEachIndexed { i, button ->
            if ((i + 1) == index) {
                button.setBackgroundColor(activeColor)
                button.setTextColor(activeTextColor)
            } else {
                button.setBackgroundColor(inactiveColor)
                button.setTextColor(inactiveTextColor)
            }
        }

        // Cập nhật nội dung dựa trên tab được chọn
        when (index) {
            1 -> {
                binding.dataScrollView.visibility = View.VISIBLE
                binding.chartLayout.visibility = View.GONE
                calculateRevenueByDay()
            }
            2 -> {
                binding.dataScrollView.visibility = View.VISIBLE
                binding.chartLayout.visibility = View.GONE
                calculateRevenueByMonth()
            }
            3 -> {
                binding.dataScrollView.visibility = View.VISIBLE
                binding.chartLayout.visibility = View.GONE
                calculateTopProducts()
            }
            4 -> {
                binding.dataScrollView.visibility = View.GONE
                binding.chartLayout.visibility = View.VISIBLE
                showChart() // Hàm này sẽ vẽ tất cả các biểu đồ
            }
        }
    }

    private fun loadAllOrders() {
        binding.progressBar.visibility = View.VISIBLE

        database.getReference("Orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allOrders.clear()
                for (child in snapshot.children) {
                    child.getValue(OrderModel::class.java)?.let { allOrders.add(it) }
                }
                binding.progressBar.visibility = View.GONE
                calculateOrderStatus()
                setActiveTab(1) // Mặc định hiển thị theo ngày
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@StatisticsActivity, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calculateOrderStatus() {
        var completedCount = 0
        var processingCount = 0
        var shippingCount = 0
        var cancelledCount = 0

        allOrders.forEach {
            when (it.status) {
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

    // ... (Các hàm tính toán danh sách giữ nguyên) ...
    private fun calculateRevenueByDay() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val resultMap = TreeMap<String, Double>()
        var totalRevenue = 0.0

        allOrders.filter { it.status != "Đã hủy" }.forEach {
            val dateKey = dateFormat.format(Date(it.orderDate))
            resultMap[dateKey] = (resultMap[dateKey] ?: 0.0) + it.total
            totalRevenue += it.total
        }

        val listStats = ArrayList<StatisticModel>()
        var rank = 1
        resultMap.descendingMap().forEach { (date, revenue) ->
            listStats.add(StatisticModel(rank++, date, revenue))
        }
        updateUI(listStats, totalRevenue, "Tổng Doanh thu (Ngày)")
    }

    private fun calculateRevenueByMonth() {
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val resultMap = TreeMap<String, Double>()
        var totalRevenue = 0.0

        allOrders.filter { it.status != "Đã hủy" }.forEach {
            val dateKey = dateFormat.format(Date(it.orderDate))
            resultMap[dateKey] = (resultMap[dateKey] ?: 0.0) + it.total
            totalRevenue += it.total
        }

        val listStats = ArrayList<StatisticModel>()
        var rank = 1
        resultMap.descendingMap().forEach { (date, revenue) ->
            listStats.add(StatisticModel(rank++, "Tháng $date", revenue))
        }
        updateUI(listStats, totalRevenue, "Tổng Doanh thu (Tháng)")
    }

    private fun calculateTopProducts() {
        val quantityMap = HashMap<String, Int>()
        // === BỔ SUNG KHAI BÁO revenueMap ĐỂ TÍNH DOANH THU ===
        val revenueMap = HashMap<String, Double>()
        var totalQuantity = 0

        allOrders.filter { it.status != "Đã hủy" }.forEach { order ->
            order.items.forEach { item ->
                val key = item.title
                // Tính số lượng
                quantityMap[key] = (quantityMap[key] ?: 0) + item.quantity
                totalQuantity += item.quantity

                // === BỔ SUNG LOGIC TÍNH DOANH THU CHO TỪNG SẢN PHẨM ===
                val currentRevenue = revenueMap[key] ?: 0.0
                val itemRevenue = item.priceAtOrder * item.quantity
                revenueMap[key] = currentRevenue + itemRevenue
            }
        }

        val sortedList = quantityMap.toList().sortedByDescending { it.second }
        val listStats = ArrayList<StatisticModel>()

        sortedList.forEachIndexed { index, pair ->
            val name = pair.first
            val qty = pair.second
            // Lấy doanh thu tương ứng của sản phẩm
            val revenue = revenueMap[name] ?: 0.0

            // === SỬA LỖI TẠI ĐÂY: Thêm 'value = revenue' vào hàm khởi tạo ===
            listStats.add(
                StatisticModel(
                    rank = index + 1,
                    label = name,
                    value = revenue, // <--- THAM SỐ BỊ THIẾU ĐÃ ĐƯỢC THÊM
                    subLabel = "Đã bán: $qty"
                )
            )
        }
        // Giờ tổng giá trị sẽ là tổng số lượng
        updateUI(listStats, totalQuantity.toDouble(), "Top Sản phẩm (Số lượng)")
    }


    // === GỌI TẤT CẢ CÁC HÀM VẼ BIỂU ĐỒ ===
    private fun showChart() {
        drawRevenueChart()
        //drawTopProductsChart()
        drawTopProductsPieChart() // Hàm mới
    }

    private fun drawRevenueChart() {
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val resultMap = TreeMap<String, Double>()
        allOrders.filter { it.status != "Đã hủy" }.forEach {
            val dateKey = dateFormat.format(Date(it.orderDate))
            resultMap[dateKey] = (resultMap[dateKey] ?: 0.0) + it.total
        }
        val listStats = ArrayList<StatisticModel>()
        resultMap.forEach { (date, revenue) ->
            listStats.add(StatisticModel(rank = 0, label = date, value = revenue))
        }
        setupBarChart(chart = binding.revenueBarChart, list = listStats, isRevenueChart = true)
    }

   // private fun drawTopProductsChart() {
        //val quantityMap = HashMap<String, Int>()
       // allOrders.filter { it.status != "Đã hủy" }.forEach { order ->
          //  order.items.forEach { item ->
                //quantityMap[item.title] = (quantityMap[item.title] ?: 0) + item.quantity
           // }
       // }
       // val sortedList = quantityMap.toList().sortedByDescending { it.second }
        //val listStats = ArrayList<StatisticModel>()
        //sortedList.forEachIndexed { index, pair ->
        //    listStats.add(StatisticModel(rank = index + 1, label = pair.first, value = pair.second.toDouble()))
        //}
        //setupBarChart(chart = binding.topProductsBarChart, list = listStats, isRevenueChart = false)
    //}

    // === HÀM MỚI: VẼ BIỂU ĐỒ TRÒN (PIE CHART) ===
    private fun drawTopProductsPieChart() {
        // 1. Tổng hợp dữ liệu
        val quantityMap = HashMap<String, Int>()
        allOrders.filter { it.status != "Đã hủy" }.forEach { order ->
            order.items.forEach { item ->
                quantityMap[item.title] = (quantityMap[item.title] ?: 0) + item.quantity
            }
        }

        // 2. Sắp xếp giảm dần
        val sortedList = quantityMap.toList().sortedByDescending { it.second }

        // 3. Chuẩn bị dữ liệu cho PieChart
        val entries = ArrayList<PieEntry>()

        // Lấy top 5 sản phẩm đầu tiên
        val topN = 5
        var currentCount = 0
        var otherCount = 0

        for (item in sortedList) {
            if (currentCount < topN) {
                entries.add(PieEntry(item.second.toFloat(), item.first)) // first=Title, second=Quantity
                currentCount++
            } else {
                otherCount += item.second
            }
        }
        // Nếu còn sản phẩm khác, gom vào mục "Khác"
        if (otherCount > 0) {
            entries.add(PieEntry(otherCount.toFloat(), "Khác"))
        }

        if (entries.isEmpty()) {
            binding.topProductsPieChart.visibility = View.GONE
            return
        }
        binding.topProductsPieChart.visibility = View.VISIBLE

        // 4. Cấu hình DataSet
        val dataSet = PieDataSet(entries, "Sản phẩm")

        // Sử dụng bảng màu đa dạng
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        dataSet.colors = colors

        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f

        // 5. Cấu hình PieData
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.topProductsPieChart)) // Định dạng %

        // 6. Cấu hình PieChart View
        binding.topProductsPieChart.apply {
            this.data = data
            isDrawHoleEnabled = true // Có lỗ tròn ở giữa
            setHoleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 45f
            transparentCircleRadius = 50f

            setUsePercentValues(true) // Hiển thị giá trị dạng %
            setEntryLabelTextSize(10f)
            setEntryLabelColor(Color.BLACK)
            description.isEnabled = false // Tắt mô tả góc dưới

            legend.isEnabled = true // Hiện chú thích
            legend.isWordWrapEnabled = true // Xuống dòng nếu chú thích dài

            animateY(1400) // Hiệu ứng xoay
            invalidate() // Vẽ lại
        }
    }

    // File: StatisticsActivity.kt
    // Tìm đến hàm setupBarChart và thay thế toàn bộ bằng hàm này

    private fun setupBarChart(chart: BarChart, list: List<StatisticModel>, isRevenueChart: Boolean) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // === SỬA LỖI TẠI ĐÂY ===
        val chartData = if (list.size > 7) {
            if (isRevenueChart) {
                // Nếu là biểu đồ doanh thu: Lấy 7 ngày CUỐI CÙNG (Mới nhất)
                list.takeLast(7)
            } else {
                // Nếu là Top món: Lấy 7 món ĐẦU TIÊN (Bán chạy nhất)
                list.take(7)
            }
        } else {
            list
        }
        // =======================

        chartData.forEachIndexed { index, item ->
            entries.add(BarEntry(index.toFloat(), item.value.toFloat()))
            val shortLabel = if (item.label.length > 15) item.label.substring(0, 15) + "..." else item.label
            labels.add(shortLabel)
        }

        if (entries.isEmpty()) {
            chart.visibility = View.GONE
            return
        }
        chart.visibility = View.VISIBLE

        val dataSet = BarDataSet(entries, "Dữ liệu").apply {
            color = ContextCompat.getColor(this@StatisticsActivity, R.color.orange)
            valueTextColor = Color.BLACK
            valueTextSize = 10f

            valueFormatter = if (isRevenueChart) {
                object : ValueFormatter() {
                    // Định dạng số tiền có dấu phẩy (Ví dụ: 500,000)
                    private val formatter = DecimalFormat("###,###")
                    override fun getFormattedValue(value: Float): String {
                        return formatter.format(value)
                    }
                }
            } else {
                object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }
        }

        chart.data = BarData(dataSet)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.animateY(1000)
        chart.axisRight.isEnabled = false
        chart.axisLeft.apply {
            setDrawGridLines(true)
            axisMinimum = 0f
        }
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            valueFormatter = IndexAxisValueFormatter(labels)
            labelRotationAngle = -45f
        }
        chart.invalidate()
    }


    private fun updateUI(list: List<StatisticModel>, totalVal: Double, title: String) {
        val formatter = DecimalFormat("###,###")
        binding.titleSummaryTxt.text = title

        if (totalVal > 0) {
            binding.totalSummaryTxt.visibility = View.VISIBLE
            if (title.contains("Top")) {
                val quantity = totalVal.toInt()
                binding.totalSummaryTxt.text = "$quantity"
            } else {
                binding.totalSummaryTxt.text = "$${formatter.format(totalVal)}"
            }
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
