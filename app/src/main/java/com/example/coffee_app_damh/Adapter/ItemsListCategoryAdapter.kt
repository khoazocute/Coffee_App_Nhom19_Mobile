package com.example.coffee_app_damh.Adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee_app_damh.Domain.ItemsModel

import android.content.Context
import android.content.Intent
import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.Activity.DetailActivity
import com.example.coffee_app_damh.databinding.ViewholderItemPicLeftBinding
import com.example.coffee_app_damh.databinding.ViewholderItemPicRightBinding
import kotlin.rem

class ItemsListCategoryAdapter(val items: MutableList<ItemsModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM1 = 0
        const val TYPE_ITEM2 = 1
    }

    lateinit var context: Context
    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) TYPE_ITEM1 else TYPE_ITEM2
    }
// trả về số lượng phần tử trong danh sách
    override fun getItemCount(): Int = items.size



    class ViewholderITem1(val binding: ViewholderItemPicRightBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewholderITem2(val binding: ViewholderItemPicLeftBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            TYPE_ITEM1 -> {
                val binding = ViewholderItemPicRightBinding.inflate(
                    LayoutInflater.from(context),
                    parent, false
                )
                ViewholderITem1(binding)
            }

            TYPE_ITEM2 -> {
                val binding = ViewholderItemPicLeftBinding.inflate(
                    LayoutInflater.from(context),
                    parent, false
                )
                ViewholderITem2(binding)
            }

            else -> throw IllegalArgumentException("Invalidview Type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = items[position]
        fun bindCommonData(
            titleTxt: String,
            priceTxt: String,
            rating: Float,
            picUrl: String
        ) {
            when (holder) {

                // Layout RIGHT
                is ViewholderITem1 -> {
                    holder.binding.titleTxt.text = titleTxt
                    holder.binding.priceTxt.text = priceTxt
                    holder.binding.ratingBar.rating = rating

                    Glide.with(context)
                        .load(picUrl)
                        .into(holder.binding.picMain)

                    // CLICK ITEM
                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("object", items[position])
                        context.startActivity(intent)
                    }
                }

                // Layout LEFT
                is ViewholderITem2 -> {
                    holder.binding.titleTxt.text = titleTxt
                    holder.binding.priceTxt.text = priceTxt
                    holder.binding.ratingBar.rating = rating

                    Glide.with(context)
                        .load(picUrl)
                        .into(holder.binding.picMain)

                    // CLICK ITEM
                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("object", items[position])
                        context.startActivity(intent)
                    }
                }
            }
        }
        bindCommonData(
            titleTxt = item.title,
            priceTxt = "${item.price} USD",
            rating = item.rating.toFloat(),
            picUrl = item.picUrl[0]
        )
    }
}

/*Ba hàm bắt buộc của RecyclerView.Adapter đóng vai trò quan trọng trong việc quản lý và hiển thị danh sách dữ liệu.Dưới đây là tóm tắt vai trò của chúng:🛠️
Tóm Tắt Vai Trò của 3 Hàm Bắt buộcHàm Bắt buộcVai trò chính (What)
Chức năng (How)getItemCount()Cung cấp Kích thước Danh sáchTrả về tổng số lượng phần tử (items.size)
cho RecyclerView biết cần phải hiển thị bao nhiêu mục.

onCreateViewHolder()Tạo Khung Giao diệnTạo và trả về một ViewHolder mới (khung giao diện trống) bằng cách inflate (thổi phồng) file layout XML,
 khi không có ViewHolder nào có thể tái sử dụng.

onBindViewHolder()Đổ Dữ liệu vào KhungLấy dữ liệu cụ thể tại một vị trí (position) và gán nó vào các thành phần UI (TextView, ImageView,...) của ViewHolder đã tạo

 */