// HÃY THAY THẾ TOÀN BỘ FILE NÀY
package com.example.coffee_app_damh.Activity

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

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ItemsModel>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(ItemsModel::class.java)
                    item?.let {
                        it.id = itemSnapshot.key ?: ""
                        items.add(it)
                    }
                }

                binding.progressBar.visibility = View.GONE

                if (items.isNotEmpty()) {
                    binding.productsRecyclerView.visibility = View.VISIBLE
                    binding.emptyTxt.visibility = View.GONE
                    binding.productsRecyclerView.layoutManager = LinearLayoutManager(this@ManageProductsActivity)

                    // === CẬP NHẬT ADAPTER VỚI CẢ 2 HÀM CALLBACK (XÓA & SỬA) ===
                    binding.productsRecyclerView.adapter = ProductManagementAdapter(
                        items,
                        onDeleteClick = { productToDelete ->
                            showDeleteConfirmationDialog(productToDelete)
                        },
                        onEditClick = { productToEdit ->
                            // Chuyển sang màn hình AddProductActivity nhưng ở chế độ Sửa
                            val intent = Intent(this@ManageProductsActivity, AddProductActivity::class.java)
                            intent.putExtra("object", productToEdit) // ItemsModel phải implements Serializable
                            startActivity(intent)
                        }
                    )

                } else {
                    binding.productsRecyclerView.visibility = View.GONE
                    binding.emptyTxt.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ManageProductsActivity, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(item: ItemsModel) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa '${item.title}'?")
            .setPositiveButton("Xóa") { dialog, _ ->
                deleteProductFromFirebase(item)
                dialog.dismiss()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteProductFromFirebase(item: ItemsModel) {
        if (item.id.isEmpty()) return

        binding.progressBar.visibility = View.VISIBLE

        // Xóa Items
        val itemsRef = database.getReference("Items").child(item.id)
        itemsRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Xóa Popular
                database.getReference("Popular").child(item.id).removeValue()
                Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.GONE
        }
    }
}
