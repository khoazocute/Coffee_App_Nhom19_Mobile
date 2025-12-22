package com.example.coffee_app_damh.Domain

data class StatisticModel(
    val rank: Int,
    val label: String,      // Ngày tháng hoặc Tên món
    val value: Double,      // Doanh thu
    val subLabel: String = "" // Số lượng (chỉ dùng cho top món)
)
