package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.CartAdapter
import com.example.coffee_app_damh.databinding.ActivityCartBinding
import com.example.project1762.Helper.ManagmentCart
import com.uilover.project195.Helper.ChangeNumberItemsListener


class CartActivity : AppCompatActivity() {
    lateinit var binding: ActivityCartBinding
    lateinit var managmentCart: ManagmentCart
    private var tax: Double= 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart= ManagmentCart(this)
        calculateCart()
        bundle()
        initCartList()
        initListeners()

    }

    private fun initListeners() {
        binding.checkoutBtn.setOnClickListener {
            // Chỉ cần lấy các giá trị đã được tính toán trong calculateCart()
            val subTotal = managmentCart.getTotalFee()

            // Lấy các giá trị đã được tính toán và làm tròn trong calculateCart()
            // Đảm bảo logic tính toán này khớp với logic tính tiền trong CheckoutActivity.kt
            val taxValue = binding.taxTxt.text.toString().replace("$", "").toDoubleOrNull() ?: 0.0
            val deliveryValue = binding.deliveryTxt.text.toString().replace("$", "").toDoubleOrNull() ?: 0.0
            val totalValue = binding.totalTxt.text.toString().replace("$", "").toDoubleOrNull() ?: 0.0

            if (totalValue > 0.0) {
                // Tạo một "tấm vé" (Intent) để đi đến màn hình CheckoutActivity
                val intent = Intent(this, CheckoutActivity::class.java)

                // Ghi các thông tin tiền nong lên "tấm vé"
                intent.putExtra("subTotal", subTotal)
                intent.putExtra("tax", taxValue) // Gửi giá trị đã tính
                intent.putExtra("delivery", deliveryValue) // Gửi giá trị đã tính
                intent.putExtra("total", totalValue) // Gửi giá trị đã tính

                // Bắt đầu hành trình, chuyển màn hình
                startActivity(intent)
            } else {
                Toast.makeText(this, "Giỏ hàng trống vui lòng thêm đơn vào giỏ hàng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initCartList() {
        binding.apply {
            listView.layoutManager=
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL,false)
            listView.adapter= CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener{
                    override fun onChanged() {
                        calculateCart()
                    }

                }
            )
        }
    }


    private fun bundle() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun calculateCart() {
       val percentTax = 0.2
        val delivery = 15
        tax = Math.round((managmentCart.getTotalFee()*percentTax)*100)/100.0
        val total = Math.round((managmentCart.getTotalFee()+tax+delivery)*100)/100
        val itemTotal= Math.round(managmentCart.getTotalFee()*100)/100
        binding.apply {
            totalFeeTxt.text="$$itemTotal"
            taxTxt.text="$$tax"
            deliveryTxt.text="$$delivery"
            totalTxt.text="$$total"
        }
    }
}