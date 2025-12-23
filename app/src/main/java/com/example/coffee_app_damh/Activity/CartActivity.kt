package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.CartAdapter
import com.example.coffee_app_damh.Domain.PromotionModel
import com.example.coffee_app_damh.databinding.ActivityCartBinding
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uilover.project195.Helper.ChangeNumberItemsListener

class CartActivity : AppCompatActivity() {
    lateinit var binding: ActivityCartBinding
    lateinit var managmentCart: ManagmentCart

    private val database = FirebaseDatabase.getInstance() // Database

    // Các biến để lưu và truyền sang Checkout
    private var itemTotal: Double = 0.0
    private var tax: Double = 0.0
    private var delivery: Double = 15.0
    private var total: Double = 0.0
    private var discountAmount: Double = 0.0 // Số tiền được giảm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        // Reset discount mỗi khi vào lại giỏ hàng (tùy chọn)
        // managmentCart.saveDiscount(0)

        initCartList()
        calculateCart()
        initListeners()
    }

    private fun initListeners() {
        binding.backBtn.setOnClickListener { finish() }

        // === XỬ LÝ NÚT ÁP DỤNG MÃ ===
        // Giả sử ID trong XML của bạn là applyCouponBtn và couponEdt
        // Nếu tên ID khác, hãy sửa lại dòng dưới đây
        binding.applyCouponBtn.setOnClickListener {
            val code = binding.couponEdt.text.toString().trim().uppercase()
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã", Toast.LENGTH_SHORT).show()
            } else {
                checkCoupon(code)
            }
        }

        binding.checkoutBtn.setOnClickListener {
            if (total > 0.0) {
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("subTotal", itemTotal)
                intent.putExtra("tax", tax)
                intent.putExtra("delivery", delivery)
                intent.putExtra("total", total)
                // Truyền thêm số tiền đã giảm
                intent.putExtra("discount", discountAmount)

                startActivity(intent)
            } else {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCoupon(code: String) {
        val promoRef = database.getReference("Promotions").child(code)
        promoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val promotion = snapshot.getValue(PromotionModel::class.java)
                    if (promotion != null) {
                        // Lưu % giảm giá
                        managmentCart.saveDiscount(promotion.discountPercent)
                        Toast.makeText(this@CartActivity, "Áp dụng: Giảm ${promotion.discountPercent}%", Toast.LENGTH_SHORT).show()
                        calculateCart()
                    }
                } else {
                    Toast.makeText(this@CartActivity, "Mã không hợp lệ", Toast.LENGTH_SHORT).show()
                    managmentCart.saveDiscount(0)
                    calculateCart()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Lỗi kiểm tra mã", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initCartList() {
        binding.apply {
            listView.layoutManager = LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            listView.adapter = CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }
                }
            )
        }
    }

    private fun calculateCart() {
        val percentTax = 0.02 // 2% thuế

        // 1. Tổng tiền hàng
        itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100.0

        // 2. Lấy % giảm giá đã lưu
        val discountPercent = managmentCart.getDiscount()

        // 3. Tính tiền giảm
        discountAmount = Math.round((itemTotal * discountPercent / 100) * 100) / 100.0

        // 4. Giá sau giảm (để tính thuế)
        val priceAfterDiscount = itemTotal - discountAmount

        // 5. Tính thuế
        tax = Math.round((priceAfterDiscount * percentTax) * 100) / 100.0

        // 6. Tổng cộng
        total = Math.round((priceAfterDiscount + tax + delivery) * 100) / 100.0

        binding.apply {
            totalFeeTxt.text = "$$itemTotal"
            taxTxt.text = "$$tax"
            deliveryTxt.text = "$$delivery"
            totalTxt.text = "$$total"

            // Hiển thị dòng giảm giá (Nếu trong XML bạn có TextView id là discountTxt)
            // Nếu chưa có id này trong XML, hãy comment dòng dưới lại để tránh lỗi crash
            discountTxt.text = "-$$discountAmount"
        }
    }
}
