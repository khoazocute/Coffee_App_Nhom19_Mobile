// HÃY THAY THẾ TOÀN BỘ NỘI DUNG FILE CŨ BẰNG FILE NÀY
package com.example.coffee_app_damh.Adapter

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.R
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
        val formatter = DecimalFormat("$###,###.##")

        holder.binding.apply {
            titleTxt.text = item.title
            priceTxt.text = formatter.format(item.price)
            categoryTxt.text = "Kho: ${item.stock}"
            // === LOGIC HIỂN THỊ ẢNH THÔNG MINH ===
            if (item.picBase64.isNotEmpty()) {
                // Ưu tiên 1: Sản phẩm mới dùng Base64
                try {
                    val imageBytes = Base64.decode(item.picBase64, Base64.DEFAULT)
                    Glide.with(context).load(imageBytes).into(pic)
                } catch (e: Exception) {
                    pic.setImageResource(R.drawable.logo3)
                }
            } else if (item.picUrl.isNotEmpty()) {
                // Ưu tiên 2: Sản phẩm cũ dùng URL
                Glide.with(context).load(item.picUrl.firstOrNull()).into(pic)
            } else {
                // Ưu tiên 3: Không có ảnh, hiện ảnh mặc định
                pic.setImageResource(R.drawable.logo3)
            }
            // =====================================
        }
    }

    override fun getItemCount(): Int = items.size
}

