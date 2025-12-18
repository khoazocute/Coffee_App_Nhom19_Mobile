// HÃY THAY THẾ TOÀN BỘ FILE ManageProductsActivity.kt CŨ BẰNG NỘI DUNG NÀY
package com.example.coffee_app_damh.Activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.ProductManagementAdapter
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.databinding.ActivityManageProductsBinding
import com.google.firebase.database.*

class ManageProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageProductsBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        initListeners()
        loadProducts()
    }

    private fun initListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.addFab.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE
        val ref: DatabaseReference = database.getReference("Items")

        // Dùng addValueEventListener để tự động cập nhật list khi có xóa/thêm
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ItemsModel>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(ItemsModel::class.java)
                    item?.let {
                        // Quan trọng: Lấy key từ Firebase gán vào ID
                        it.id = itemSnapshot.key ?: ""
                        items.add(it)
                    }
                }

                binding.progressBar.visibility = View.GONE

                if (items.isNotEmpty()) {
                    binding.productsRecyclerView.visibility = View.VISIBLE
                    binding.emptyTxt.visibility = View.GONE
                    binding.productsRecyclerView.layoutManager = LinearLayoutManager(this@ManageProductsActivity)

                    // === TRUYỀN HÀM XỬ LÝ XÓA VÀO ADAPTER ===
                    binding.productsRecyclerView.adapter = ProductManagementAdapter(items) { productToDelete ->
                        showDeleteConfirmationDialog(productToDelete)
                    }

                } else {
                    binding.productsRecyclerView.visibility = View.GONE
                    binding.emptyTxt.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ManageProductsActivity, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Hiển thị hộp thoại xác nhận xóa
    private fun showDeleteConfirmationDialog(item: ItemsModel) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '${item.title}' không?")
            .setPositiveButton("Xóa") { dialog, _ ->
                deleteProductFromFirebase(item)
                dialog.dismiss()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Thực hiện xóa trên Firebase
    private fun deleteProductFromFirebase(item: ItemsModel) {
        if (item.id.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        // 1. Xóa trong node Items (Quản lý)
        val itemsRef = database.getReference("Items").child(item.id)
        itemsRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                // 2. Xóa luôn trong node Popular (User) nếu có để đồng bộ
                val popularRef = database.getReference("Popular").child(item.id)
                popularRef.removeValue()

                Toast.makeText(this, "Đã xóa sản phẩm thành công", Toast.LENGTH_SHORT).show()
                // Không cần gọi loadProducts() lại vì addValueEventListener tự động lắng nghe thay đổi
            } else {
                Toast.makeText(this, "Xóa thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.GONE
        }
    }
}
