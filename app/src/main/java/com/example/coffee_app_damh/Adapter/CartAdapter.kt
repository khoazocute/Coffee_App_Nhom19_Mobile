// HÃY THAY THẾ TOÀN BỘ FILE CartAdapter.kt CŨ BẰNG NỘI DUNG NÀY
package com.example.coffee_app_damh.Adapter

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.databinding.ViewholderCartBinding
import com.example.project1762.Helper.ManagmentCart
import com.uilover.project195.Helper.ChangeNumberItemsListener

class CartAdapter(
    private val lisItemSelected: ArrayList<ItemsModel>,
    context: Context,
    var changeNumberItemsListener: ChangeNumberItemsListener? = null
) : RecyclerView.Adapter<CartAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderCartBinding) : RecyclerView.ViewHolder(binding.root)

    private val managmentCart = ManagmentCart(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.Viewholder {
        val binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CartAdapter.Viewholder, position: Int) {
        val item = lisItemSelected[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.feeEachItem.text = "$${item.price}"
        holder.binding.totalEachItem.text = "$${Math.round(item.numberInCart * item.price * 100) / 100.0}"
        holder.binding.numberItemTxt.text = item.numberInCart.toString()

        // === SỬA LỖI CRASH TẠI ĐÂY: Logic hiển thị ảnh (Base64 vs URL) ===
        val requestOptions = RequestOptions().transform(CenterCrop())

        if (!item.picBase64.isNullOrEmpty()) {
            // Trường hợp 1: Sản phẩm mới (dùng Base64)
            try {
                val imageBytes = Base64.decode(item.picBase64, Base64.DEFAULT)
                Glide.with(holder.itemView.context)
                    .load(imageBytes)
                    .apply(requestOptions)
                    .placeholder(R.drawable.logo5)
                    .error(R.drawable.logo5)
                    .into(holder.binding.picCart)
            } catch (e: Exception) {
                holder.binding.picCart.setImageResource(R.drawable.logo5)
            }
        } else if (item.picUrl.isNotEmpty()) {
            // Trường hợp 2: Sản phẩm cũ (dùng URL)
            Glide.with(holder.itemView.context)
                .load(item.picUrl[0])
                .apply(requestOptions)
                .placeholder(R.drawable.logo5)
                .into(holder.binding.picCart)
        } else {
            // Trường hợp 3: Không có ảnh
            holder.binding.picCart.setImageResource(R.drawable.logo5)
        }
        // ================================================================

        holder.binding.plusEachItem.setOnClickListener {
            managmentCart.plusItem(lisItemSelected, position, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }
            })
        }

        holder.binding.minusEachItem.setOnClickListener {
            managmentCart.minusItem(lisItemSelected, position, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }
            })
        }

        holder.binding.removeItemBtn.setOnClickListener {
            managmentCart.romveItem(lisItemSelected, position, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }
            })
        }
    }

    override fun getItemCount(): Int = lisItemSelected.size
}
