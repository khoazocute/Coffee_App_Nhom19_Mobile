// File: /app/src/main/java/com/example/coffee_app_damh/Adapter/ProductManagementAdapter.kt
package com.example.coffee_app_damh.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.databinding.ViewholderProductManagementBinding
import java.text.DecimalFormat

class ProductManagementAdapter(private val items: List<ItemsModel>) : RecyclerView.Adapter<ProductManagementAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(val binding: ViewholderProductManagementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderProductManagementBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val formatter = DecimalFormat("$ ###,###.## ")

        holder.binding.apply {
            titleTxt.text = item.title
            priceTxt.text = formatter.format(item.price)
            // Dùng Glide để load ảnh

            Glide.with(context)
                .load(item.picUrl.firstOrNull()) // Lấy ảnh đầu tiên trong danh sách
                .into(pic)

            // TODO: Chúng ta sẽ gán sự kiện click cho editBtn và deleteBtn ở các phần sau
        }
    }

    override fun getItemCount(): Int = items.size
}
