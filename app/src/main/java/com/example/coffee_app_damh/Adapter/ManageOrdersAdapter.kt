package com.example.coffee_app_damh.Adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee_app_damh.Domain.OrderModel
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.databinding.ViewholderManageOrderBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManageOrdersAdapter(
    private val orders: List<OrderModel>,
    private val onItemClick: (OrderModel) -> Unit
) : RecyclerView.Adapter<ManageOrdersAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderManageOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderManageOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        val formatter = DecimalFormat("###,###")
        val dateFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormatDay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.binding.apply {
            // ID và Ngày giờ
            orderIdTxt.text = "#${order.orderId.takeLast(6).uppercase()}"
            dateTxt.text = "Hôm nay, ${dateFormatTime.format(Date(order.orderDate))}" // Logic "Hôm nay" cần xử lý kỹ hơn nếu muốn
            dateBottomTxt.text = dateFormatDay.format(Date(order.orderDate))

            customerNameTxt.text = order.name
            totalTxt.text = "$ ${formatter.format(order.total)} "
            statusTxt.text = order.status.uppercase()

            // === XỬ LÝ GIAO DIỆN THEO TRẠNG THÁI ===

            // 1. Định nghĩa màu chủ đạo
            val colorHex = when (order.status) {
                "Đang xử lý" -> "#FFA726"   // Cam
                "Đang giao" -> "#42A5F5"    // Xanh dương
                "Hoàn thành" -> "#66BB6A"   // Xanh lá
                "Đã hủy" -> "#EF5350"       // Đỏ
                else -> "#BDBDBD"           // Xám
            }
            val mainColor = Color.parseColor(colorHex)

            // 2. Tạo màu nền nhạt (Opacity 20%)
            val lightColor = ColorUtils.setAlphaComponent(mainColor, 50) // 50/255 alpha

            // 3. Chọn Icon tương ứng (Bạn cần có các drawable này, nếu chưa có hãy dùng tạm cái khác)
            //val iconRes = when (order.status) {
                //"Đang xử lý" -> R.drawable.logo5 // Thay bằng icon cái túi hoặc đồng hồ
                //"Đang giao" -> R.drawable.logo5  // Thay bằng icon xe tải
                //"Hoàn thành" -> R.drawable.logo5 // Thay bằng icon dấu tích (check)
                //"Đã hủy" -> R.drawable.logo5     // Thay bằng icon dấu X
               // else -> R.drawable.logo5
            //}
            //statusIcon.setImageResource(iconRes)
            //statusIcon.setColorFilter(mainColor) // Tô màu icon

            // 4. Set background cho Icon Container (Hình vuông bo tròn)
            val iconBg = GradientDrawable()
            iconBg.shape = GradientDrawable.RECTANGLE
            iconBg.cornerRadius = 15f
            iconBg.setColor(lightColor) // Màu nền nhạt
            iconContainer.background = iconBg

            // 5. Set background cho Status Badge (Hình viên thuốc)
            val badgeBg = GradientDrawable()
            badgeBg.shape = GradientDrawable.RECTANGLE
            badgeBg.cornerRadius = 50f
            badgeBg.setColor(lightColor) // Màu nền nhạt
            statusTxt.background = badgeBg
            statusTxt.setTextColor(mainColor) // Màu chữ đậm

            // 6. Màu giá tiền
            if (order.status == "Đã hủy") {
                totalTxt.setTextColor(Color.GRAY)
            } else {
                totalTxt.setTextColor(Color.parseColor("#4CAF50")) // Màu xanh tiền
            }

            // Click event
            root.setOnClickListener { onItemClick(order) }
        }
    }

    override fun getItemCount(): Int = orders.size
}
