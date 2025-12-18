// HÃY THAY THẾ TOÀN BỘ FILE DetailActivity.kt BẰNG NỘI DUNG NÀY
package com.example.coffee_app_damh.Activity

import android.os.Bundle
import android.util.Base64
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.databinding.ActivityDetailBinding
import com.example.project1762.Helper.ManagmentCart

// Activity chịu trách nhiệm hiển thị chi tiết sản phẩm và quản lý việc thêm vào giỏ hàng
class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        // Hàm .inflate(layoutInflater) có nhiệm vụ đọc tệp XML (activity_detail.xml)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        // Hàm setContentView dùng để thiết lập giao diện người dùng
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

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

    // hàm xử lý dữ liệu
    private fun bundle() {
        binding.apply {
            // Lấy đối tượng object (ItemsModel) đã được truyền vào từ màn hình trước
            item = intent.getSerializableExtra("object") as ItemsModel

            // === PHẦN SỬA ĐỔI QUAN TRỌNG ĐỂ KHÔNG BỊ LỖI GIỎ HÀNG ===
            // Logic: Kiểm tra ảnh Base64 trước, nếu không có mới kiểm tra URL

            val requestOptions = RequestOptions().transform(CenterCrop())

            if (!item.picBase64.isNullOrEmpty()) {
                // Trường hợp 1: Sản phẩm mới (ảnh từ máy admin)
                try {
                    val imageBytes = Base64.decode(item.picBase64, Base64.DEFAULT)
                    Glide.with(this@DetailActivity)
                        .load(imageBytes)
                        .apply(requestOptions)
                        .into(binding.picMain)
                } catch (e: Exception) {
                    binding.picMain.setImageResource(R.drawable.logo5)
                }
            } else if (item.picUrl.isNotEmpty()) {
                // Trường hợp 2: Sản phẩm cũ (ảnh từ link URL)
                Glide.with(this@DetailActivity)
                    .load(item.picUrl[0])
                    .apply(requestOptions)
                    .into(binding.picMain)
            } else {
                // Trường hợp 3: Không có ảnh
                binding.picMain.setImageResource(R.drawable.logo5)
            }
            // ========================================================

            titleTxt.text = item.title
            descriptionTxt.text = item.description
            priceTxt.text = "$" + item.price
            ratingTxt.text = item.rating.toString()

            addToCartBtn.setOnClickListener {
                // Cập nhật số lượng item hiện tại vào object trước khi thêm
                item.numberInCart = Integer.valueOf(
                    numberItemTxt.text.toString()
                )
                // lưu trữ dữ liệu vào giỏ hàng
                managmentCart.insertItems(item)
            }

            backBtn.setOnClickListener {
                finish()
            }

            plusCart.setOnClickListener {
                // Tăng số lượng hiển thị trên màn hình
                var currentNum = numberItemTxt.text.toString().toInt()
                currentNum++
                numberItemTxt.text = currentNum.toString()
                // Cập nhật biến tạm (lưu ý: số lượng thực tế sẽ được set lại khi bấm AddToCart)
                item.numberInCart = currentNum
            }

            minusBtn.setOnClickListener {
                var currentNum = numberItemTxt.text.toString().toInt()
                if (currentNum > 1) { // Chỉ giảm khi số lượng > 1
                    currentNum--
                    numberItemTxt.text = currentNum.toString()
                    item.numberInCart = currentNum
                }
            }
        }
    }
}
