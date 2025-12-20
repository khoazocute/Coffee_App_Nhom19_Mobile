package com.example.coffee_app_damh.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee_app_damh.Domain.OrderItemModel
import com.example.coffee_app_damh.databinding.ViewholderOrderDetailItemBinding
import java.text.DecimalFormat

class OrderDetailAdapter(private val items: List<OrderItemModel>) : RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderOrderDetailItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderOrderDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val formatter = DecimalFormat("###,###")

        holder.binding.apply {
            titleTxt.text = item.title
            quantityTxt.text = "x${item.quantity}"

            // Giá đơn lẻ
            priceSingleTxt.text = " $ ${formatter.format(item.priceAtOrder)} "

            // Tổng tiền của dòng này (Số lượng * Giá)
            val lineTotal = item.priceAtOrder * item.quantity
            totalPriceTxt.text = "$ ${formatter.format(lineTotal)} "
        }
    }

    override fun getItemCount(): Int = items.size
}
