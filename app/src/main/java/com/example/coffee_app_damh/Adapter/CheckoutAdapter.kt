// File: /app/src/main/java/com/example/coffee_app_damh/Adapter/CheckoutAdapter.kt
// HÃY DÁN NỘI DUNG NÀY VÀO FILE ADAPTER CỦA BẠN

package com.example.coffee_app_damh.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee_app_damh.Domain.ItemsModel // Đảm bảo bạn đã tạo ItemsModel
import com.example.coffee_app_damh.databinding.ViewholderCheckoutItemBinding
import java.text.DecimalFormat

class CheckoutAdapter(private val items: List<ItemsModel>) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderCheckoutItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val formatter = DecimalFormat("###,###.##")

        holder.binding.apply {
            titleTxt.text = item.title
            quantityTxt.text = "SL: ${item.numberInCart}"
            priceTxt.text = "$${formatter.format(item.price * item.numberInCart)}"
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewholderCheckoutItemBinding) : RecyclerView.ViewHolder(binding.root)
}
