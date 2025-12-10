// File: /app/src/main/java/com/example/coffee_app_damh/Domain/OrderModel.kt
package com.example.coffee_app_damh.Domain

import android.os.Parcelable
import com.example.coffee_app_damh.Domain.OrderItemModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderModel(
    // --- Thông tin cơ bản ---
    var orderId: String = "",
    var userId: String = "",
    var orderDate: Long = 0,
    var status: String = "Đang xử lý", // Ví dụ: Đang xử lý, Đang giao, Đã giao, Đã hủy

    // --- Thông tin người nhận ---
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var paymentMethod: String = "COD",

    // --- Chi tiết sản phẩm (ĐÃ TỐI ƯU) ---
    var items: ArrayList<OrderItemModel> = ArrayList(),

    // --- Chi tiết thanh toán ---
    var subTotal: Double = 0.0,
    var delivery: Double = 0.0,
    var tax: Double = 0.0,
    var total: Double = 0.0

) : Parcelable
