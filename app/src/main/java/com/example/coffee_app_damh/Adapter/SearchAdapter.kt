// File: app/src/main/java/com/example/coffee_app_damh/Adapter/SearchAdapter.kt
package com.example.coffee_app_damh.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.Activity.DetailActivity
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.databinding.SearchItemBinding

// SearchAdapter hiển thị danh sách sản phẩm tìm kiếm trong RecyclerView của Popup
class SearchAdapter(private val items: List<ItemsModel>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var context: Context
    var onItemClick: (() -> Unit)? = null // Callback để thông báo cho Activity khi một item được click

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        // Sử dụng ViewBinding để inflate layout search_item.xml
        val binding = SearchItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemTitle.text = item.title
        holder.binding.itemPrice.text = "$${item.price}"

        // Lấy URL hình ảnh đầu tiên, nếu có
        val imageUrl = item.picUrl.firstOrNull()
        if (imageUrl != null) {
            Glide.with(context)
                .load(imageUrl)
                .into(holder.binding.itemPic)
        }

        // Sự kiện click vào item -> Mở DetailActivity
        holder.itemView.setOnClickListener {
            //Tạo intent mở màn hình chi tiết sản phẩm
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", item)
                //Truyền toàn bộ đối tượng sản phẩm đó qua intent
                // Giúp lấy dúng sản phẩm đang click
            }
            context.startActivity(intent)
            onItemClick?.invoke() // Gọi callback để Activity có thể tắt popup
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root)
}
