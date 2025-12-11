// File: /app/src/main/java/com/example/coffee_app_damh/Domain/OrderItemModel.kt
package com.example.coffee_app_damh.Domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItemModel(
    var itemId: String = "",
    var title: String = "",// Chỉ lưu ID của sản phẩm
    var quantity: Int = 0,          // Số lượng
    var priceAtOrder: Double = 0.0  // Lưu lại giá tại thời điểm đặt hàng (Rất quan trọng!)
) : Parcelable

