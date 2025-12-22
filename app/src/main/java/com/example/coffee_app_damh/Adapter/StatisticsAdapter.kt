package com.example.coffee_app_damh.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.coffee_app_damh.Domain.StatisticModel
import kotlin.text.format
import kotlin.text.isNotEmpty

import com.example.coffee_app_damh.databinding.ViewholderStatisticBinding
import java.text.DecimalFormat
//List<StatisticModel> : chứa các dữ liệu đã tính toán xong để đổ lên recycleview
class StatisticsAdapter(private val items: List<StatisticModel>) :
    RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {
//Sử dụng ViewBinding để truy cập nhanh vào các thành phần mà không cần dùng findViewById.
    class ViewHolder(val binding: ViewholderStatisticBinding) :
        RecyclerView.ViewHolder(binding.root)

//Hàm này chạy khi RecyclerView cần tạo một dòng mới.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderStatisticBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
//Đây là hàm quan trọng nhất, chạy mỗi khi một dòng xuất hiện trên màn hình:
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val formatter = DecimalFormat("###,###")

        holder.binding.apply {
            rankTxt.text = "#${item.rank}"
            labelTxt.text = item.label
            valueTxt.text = "$${formatter.format(item.value)}"

            if (item.subLabel.isNotEmpty()) {
                subLabelTxt.text = item.subLabel
                subLabelTxt.visibility = View.VISIBLE
            } else {
                subLabelTxt.visibility = View.GONE
            }
        }
    }
//Thông báo cho RecyclerView biết tổng số lượng dòng cần hiển thị
    override fun getItemCount(): Int = items.size
}
