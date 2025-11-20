package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.databinding.ActivityDetailBinding
import com.example.project1762.Helper.ManagmentCart
import kotlin.math.min

// Activity chịu trách nhiệm hiển thị chi tiết sản phẩm và quản lý việc thêm vào giỏ hàng
class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
//Hàm .inflate(layoutInflater) có nhiệm vụ đọc layout
// Hàm .inflate(layoutInflater) có nhiệm vụ đọc tệp XML (activity_detail.xml) và biến nó thành một đối tượng View (một quy trình gọi là Inflating).
        binding= ActivityDetailBinding.inflate(layoutInflater)
//Hàm setContentView dùng để thiết lập giao diện người dùng
        setContentView(binding.root)
        managmentCart= ManagmentCart(this)
        bundle()
        initSizeList()
    }

    private fun initSizeList() {
        binding.apply {
            smallBtn.setOnClickListener {
                smallBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(0)
            }
            mediumBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
                largeBtn.setBackgroundResource(0)
            }
            largeBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
            }
        }
    }
//hàm xử lý dữ liệu
    private fun bundle() {
       binding.apply {
           item= intent.getSerializableExtra("object") as ItemsModel //Lấy đối tượng object (itemsmodel) đã được truyền vào
// Đối tượng Glide dùng để tải ảnh và hiển thị lên nơi có id là picMain
           Glide.with(this@DetailActivity)
               .load(item.picUrl[0])
               .into(binding.picMain)

           titleTxt.text = item.title
           descriptionTxt.text = item.description
           priceTxt.text = "$" + item.price
           ratingTxt.text = item.rating.toString()

           addToCartBtn.setOnClickListener {
               item.numberInCart = Integer.valueOf(
                   numberItemTxt.text.toString()
               )
               //lưu trữ dữ liệu vào giỏ hàng
               managmentCart.insertItems(item)
           }
           backBtn.setOnClickListener {
               startActivity(Intent(this@DetailActivity, MainActivity::class.java))
           }
           plusCart.setOnClickListener {
               numberItemTxt.text=(item.numberInCart+1).toString()
               item.numberInCart++
           }
           minusBtn.setOnClickListener {
               if(item.numberInCart>0)
                   numberItemTxt.text=(item.numberInCart-1).toString()
               item.numberInCart--
           }
       }
    }
}



