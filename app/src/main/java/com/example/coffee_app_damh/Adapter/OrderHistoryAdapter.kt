// File: /app/src/main/java/com/example/coffee_app_damh/Adapter/OrderHistoryAdapter.kt
// HÃY THAY THẾ TOÀN BỘ FILE NÀY

package com.example.coffee_app_damh.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee_app_damh.Domain.OrderModel
import com.example.coffee_app_damh.databinding.ViewholderOrderHistoryBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

// KHÔNG CẦN allItemsMap nữa
class OrderHistoryAdapter(private val orders: List<OrderModel>) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        val formatter = DecimalFormat("###,###.##")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        holder.binding.apply {
            orderIdTxt.text = "#${order.orderId.take(6).uppercase()}"
            dateTxt.text = dateFormat.format(Date(order.orderDate))
            statusTxt.text = order.status
            totalTxt.text = "$${formatter.format(order.total)}"

            // === SỬA LỖI LOGIC: Đọc thẳng title từ order.items ===
            val itemsSummary = order.items.joinToString(separator = "\n") { orderItem ->
                // Bây giờ orderItem đã có sẵn "title"
                "${orderItem.title} (x${orderItem.quantity})"
            }
            itemsSummaryTxt.text = itemsSummary
        }
    }

    override fun getItemCount(): Int = orders.size
}
