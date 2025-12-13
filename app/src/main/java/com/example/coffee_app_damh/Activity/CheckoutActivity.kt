// File: /app/src/main/java/com/example/coffee_app_damh/Activity/CheckoutActivity.kt
// HÃY THAY THẾ TOÀN BỘ FILE CŨ BẰNG NỘI DUNG NÀY

package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.CheckoutAdapter
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.Domain.OrderItemModel
import com.example.coffee_app_damh.Domain.OrderModel
import com.example.coffee_app_damh.databinding.ActivityCheckoutBinding
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var managmentCart: ManagmentCart
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartItems: ArrayList<ItemsModel>

    // Bỏ giá trị khởi tạo ở đây để tránh nhầm lẫn
    private var subTotal: Double = 0.0
    private var tax: Double = 0.0
    private var delivery: Double = 0.0
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        cartItems = managmentCart.getListCart()

        // === THỨ TỰ LOGIC ĐÚNG ===

        // 1. Lấy dữ liệu trước tiên
        getBundle()

        // 2. Kiểm tra dữ liệu ngay sau khi lấy
        if (total == 0.0 || cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show()
            finish() // Tự đóng lại nếu không có gì để thanh toán
            return   // Dừng hàm onCreate() tại đây
        }

        // 3. Nếu dữ liệu hợp lệ, mới tiếp tục cài đặt các thành phần khác
        initUI()
        initOrderSummary()
        initListeners()
    }

    private fun getBundle() {
        subTotal = intent.getDoubleExtra("subTotal", 0.0)
        tax = intent.getDoubleExtra("tax", 0.0)
        delivery = intent.getDoubleExtra("delivery", 0.0)
        total = intent.getDoubleExtra("total", 0.0)
    }

    private fun initUI() {
        val formatter = DecimalFormat("###,###.##")
        binding.subtotalTxt.text = "$${formatter.format(subTotal)}"
        binding.taxTxt.text = "$${formatter.format(tax)}"
        binding.deliveryTxt.text = "$${formatter.format(delivery)}"
        binding.totalTxt.text = "$${formatter.format(total)}"
    }

    private fun initOrderSummary() {
        binding.orderSummaryRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CheckoutAdapter(cartItems)
        binding.orderSummaryRecyclerView.adapter = adapter
    }

    private fun initListeners() {
        binding.backBtn.setOnClickListener { finish() }
        binding.orderBtn.setOnClickListener {
            if (validateInput()) {
                placeOrder()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.nameEdt.text.isNullOrEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên người nhận", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.addressEdt.text.isNullOrEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.phoneEdt.text.isNullOrEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun placeOrder() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show()
            return
        }

        binding.orderBtn.isEnabled = false
        binding.orderBtn.text = "Processing..."

        val orderItems = ArrayList<OrderItemModel>()
        cartItems.forEach { cartItem ->
            orderItems.add(
                OrderItemModel(
                    itemId = cartItem.id,
                    title = cartItem.title,
                    quantity = cartItem.numberInCart,
                    priceAtOrder = cartItem.price
                )
            )
        }

        val orderRef = database.getReference("Orders")
        val orderId = orderRef.push().key ?: ""

        val order = OrderModel(
            orderId = orderId,
            userId = currentUser.uid,
            name = binding.nameEdt.text.toString(),
            address = binding.addressEdt.text.toString(),
            phone = binding.phoneEdt.text.toString(),
            paymentMethod = if (binding.codRadio.isChecked) "COD" else "Online",
            items = orderItems,
            subTotal = subTotal,
            tax = tax,
            delivery = delivery,
            total = total,
            orderDate = System.currentTimeMillis(),
            status = "Đang xử lý"
        )

        orderRef.child(orderId).setValue(order).addOnSuccessListener {
            managmentCart.clearCart()
            // 2. Tạo một "tấm vé" (Intent) để đi đến màn hình popup
            val intent = Intent(this, OrderSuccessActivity::class.java)

            // 3. Đóng gói tất cả thông tin vào "tấm vé"
            intent.putExtra("orderId", order.orderId)
            intent.putExtra("orderDate", order.orderDate)
            intent.putExtra("name", order.name)
            intent.putExtra("address", order.address)
            intent.putExtra("total", order.total)

            // === GỬI THÊM DANH SÁCH SẢN PHẨM VÀO ĐÂY ===
            intent.putExtra("items", cartItems) // cartItems là ArrayList<ItemsModel> đã có sẵn
            // ============================================

            // 4. Xóa hết các màn hình cũ (Cart, Checkout) để người dùng không thể "Back" lại
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // 5. Bắt đầu hành trình, chuyển đến màn hình popup
            startActivity(intent)
            finish() // Đóng màn hình Checkout lại

        }.addOnFailureListener {
            Toast.makeText(this, "Đặt hàng thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            binding.orderBtn.isEnabled = true
            binding.orderBtn.text = "Xác nhận Đặt hàng"
        }
    }
}
