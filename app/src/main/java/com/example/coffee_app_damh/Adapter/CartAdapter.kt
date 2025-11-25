package com.example.coffee_app_damh.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.databinding.ViewholderCartBinding
import com.example.project1762.Helper.ManagmentCart
import com.uilover.project195.Helper.ChangeNumberItemsListener
import kotlin.math.roundToInt

class CartAdapter
    (
    private val lisItemSelected: ArrayList<ItemsModel>,
    context: Context,
    var changeNumberItemsListener: ChangeNumberItemsListener? =null
            ): RecyclerView.Adapter<CartAdapter.Viewholder>(){
    class Viewholder (val binding: ViewholderCartBinding): RecyclerView.ViewHolder(binding.root)
//binding sẽ tham chiếu đến tất cả thành phần UI được định nghĩa bên trong layout

    private val managmentCart= ManagmentCart(context)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.Viewholder {
        val binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CartAdapter.Viewholder, position: Int) {
        val item= lisItemSelected[position]

        holder.binding.titleTxt.text=item.title
        holder.binding.feeEachItem.text="$${item.price}"
        holder.binding.totalEachItem.text="$${Math.round(item.numberInCart * item.price)}"
        holder.binding.numberItemTxt.text=item.numberInCart.toString()

        Glide.with(holder.itemView.context)
            .load(item.picUrl[0])
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.picCart)

        holder.binding.plusEachItem.setOnClickListener {
            managmentCart.plusItem(lisItemSelected,position,object : ChangeNumberItemsListener{
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }

            })
        }

        holder.binding.minusEachItem.setOnClickListener {
            managmentCart.minusItem(lisItemSelected,position,object : ChangeNumberItemsListener{
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }

            })
        }

        holder.binding.removeItemBtn.setOnClickListener {
            managmentCart.romveItem(lisItemSelected,position,object : ChangeNumberItemsListener {
                override fun onChanged() {
                   notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }

            })
        }
    }

    override fun getItemCount(): Int = lisItemSelected.size


}
