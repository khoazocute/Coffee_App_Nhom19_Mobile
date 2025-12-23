package com.example.coffee_app_damh.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee_app_damh.Domain.PromotionModel
import com.example.coffee_app_damh.databinding.ViewholderPromotionBinding
import java.text.SimpleDateFormat
import java.util.*

class PromotionAdapter(
    private val items: List<PromotionModel>,
    private val isAdmin: Boolean, // Biến này quyết định giao diện Admin hay User
    private val onCopyClick: (String) -> Unit,
    private val onEditClick: (PromotionModel) -> Unit,
    private val onDeleteClick: (PromotionModel) -> Unit
) : RecyclerView.Adapter<PromotionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderPromotionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.binding.apply {
            codeTxt.text = item.code
            percentTxt.text = "Giảm ${item.discountPercent}%"

            // Hiển thị hạn sử dụng
            if (item.endDate > 0) {
                dateTxt.text = "Hạn: ${sdf.format(Date(item.endDate))}"
            } else {
                dateTxt.text = "Vô thời hạn"
            }

            // === LOGIC ẨN/HIỆN NÚT COPY ===
            if (isAdmin) {
                // TRƯỜNG HỢP ADMIN:
                // 1. Ẩn nút Copy
                btnCopy.visibility = View.GONE

                // 2. Hiện nút Sửa và Xóa (bạn cần đảm bảo Layout XML có 2 nút này)
                // Nếu layout chưa có ID cho cụm nút Admin, hãy dùng visibility cho từng cái
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE

                // Gán sự kiện
                btnEdit.setOnClickListener { onEditClick(item) }
                btnDelete.setOnClickListener { onDeleteClick(item) }

            } else {
                // TRƯỜNG HỢP USER:
                // 1. Hiện nút Copy
                btnCopy.visibility = View.VISIBLE

                // 2. Ẩn nút Sửa và Xóa
                btnEdit.visibility = View.GONE
                btnDelete.visibility = View.GONE

                // Gán sự kiện
                btnCopy.setOnClickListener { onCopyClick(item.code) }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
