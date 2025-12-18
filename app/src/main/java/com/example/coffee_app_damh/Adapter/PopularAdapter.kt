package com.example.coffee_app_damh.Adapter

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.coffee_app_damh.Activity.DetailActivity
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.databinding.ViewholderPopularBinding

class PopularAdapter(val items: MutableList<ItemsModel>) : RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    private lateinit var context: Context

    class Viewholder(val binding: ViewholderPopularBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "$" + item.price

        // Cấu hình hiển thị ảnh: Cắt ảnh cho vừa khung (CenterCrop)
        val requestOptions = RequestOptions().transform(CenterCrop())

        // LOGIC HIỂN THỊ ẢNH (Hỗ trợ cả Base64 mới và URL cũ)
        if (!item.picBase64.isNullOrEmpty()) {
            // Trường hợp 1: Sản phẩm mới (Lưu dạng mã hóa)
            try {
                val imageBytes = Base64.decode(item.picBase64, Base64.DEFAULT)
                Glide.with(context)
                    .load(imageBytes)
                    .apply(requestOptions)
                    .placeholder(R.drawable.logo5)
                    .into(holder.binding.pic)
            } catch (e: Exception) {
                holder.binding.pic.setImageResource(R.drawable.logo5)
            }
        } else if (item.picUrl.isNotEmpty()) {
            // Trường hợp 2: Sản phẩm cũ (Lưu dạng link URL)
            Glide.with(context)
                .load(item.picUrl[0])
                .apply(requestOptions)
                .placeholder(R.drawable.logo5)
                .into(holder.binding.pic)
        } else {
            holder.binding.pic.setImageResource(R.drawable.logo5)
        }

        // Click vào sản phẩm để xem chi tiết
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", item)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}
