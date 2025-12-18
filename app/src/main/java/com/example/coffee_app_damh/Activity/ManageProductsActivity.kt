// File: /app/src/main/java/com/example/coffee_app_damh/Activity/ManageProductsActivity.kt
package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
            // === SỬA LẠI ĐOẠN NÀY ===
            // Mở màn hình AddProductActivity
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
            // =======================
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
                        // Gán key của Firebase vào id của object để tiện cho việc sửa/xóa sau này
                        it.id = itemSnapshot.key ?: ""
                        items.add(it)
                    }
                }
                binding.progressBar.visibility = View.GONE
                if (items.isNotEmpty()) {
                    binding.productsRecyclerView.visibility = View.VISIBLE
                    binding.emptyTxt.visibility = View.GONE
                    binding.productsRecyclerView.layoutManager = LinearLayoutManager(this@ManageProductsActivity)
                    binding.productsRecyclerView.adapter = ProductManagementAdapter(items)
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
}
