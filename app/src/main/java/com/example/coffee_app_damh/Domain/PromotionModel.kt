package com.example.coffee_app_damh.Domain

import java.io.Serializable

data class PromotionModel(
    var id: String = "",           // Dùng code làm ID luôn cho dễ quản lý
    var code: String = "",         // Mã hiển thị (VD: SALE50)
    var discountPercent: Int = 0,  // Số % giảm
    var description: String = "",  // Mô tả chương trình
    var startDate: Long = 0,       // Ngày bắt đầu (Timestamp)
    var endDate: Long = 0          // Ngày kết thúc (Timestamp)
) : Serializable
