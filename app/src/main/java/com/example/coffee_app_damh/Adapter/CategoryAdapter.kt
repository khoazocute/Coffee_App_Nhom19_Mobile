package com.example.coffee_app_damh.Adapter
import com.example.coffee_app_damh.R
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee_app_damh.Activity.ItemsListActivity
import com.example.coffee_app_damh.Domain.CategoryModel
import com.example.coffee_app_damh.databinding.ActivityMainBinding
import com.example.coffee_app_damh.databinding.ViewholderCategoryBinding
import kotlinx.coroutines.delay


class CategoryAdapter(val items: MutableList<CategoryModel>):
    RecyclerView.Adapter<CategoryAdapter.Viewholder>(){
        private lateinit var context: Context
        private var selectedPosition=-1
    private var lastSelectedPosition=-1
    inner class Viewholder(var binding: ViewholderCategoryBinding)
        : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.Viewholder {
        context=parent.context
        val binding= ViewholderCategoryBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)// Tạo ra 1 ViewHolder mới cho từng item
    }

    override fun onBindViewHolder(holder: CategoryAdapter.Viewholder, position: Int) {
        val item=items[position] //Lấy category tại vị trí hiện tại
        holder.binding.titleCat.text=item.title

        holder.binding.root.setOnClickListener {
            lastSelectedPosition=selectedPosition
            selectedPosition=position
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)


            Handler(Looper.getMainLooper()).postDelayed({
                val intent= Intent(context, ItemsListActivity::class.java).apply {
                    putExtra("id",item.id.toString())
                    putExtra("title",item.title)
                }
                ContextCompat.startActivity(context,intent,null)
            }, 500)
        }
        if(selectedPosition==position){
            holder.binding.titleCat.setBackgroundResource(R.drawable.dark_brown_bg) // Đổi nền sang nâu đậm
            holder.binding.titleCat.setTextColor(context.resources.getColor(R.color.white)) // Chữ thành trắng
        } else {
            holder.binding.titleCat.setBackgroundResource(R.drawable.white_bg) // Đổi nền sang nâu đậm
            holder.binding.titleCat.setTextColor(context.resources.getColor(R.color.darkBrown)) // Chữ thành trắng
        }

    }

    override fun getItemCount(): Int = items.size

}