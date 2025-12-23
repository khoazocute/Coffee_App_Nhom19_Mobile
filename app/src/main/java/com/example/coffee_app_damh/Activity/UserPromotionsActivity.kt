package com.example.coffee_app_damh.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.PromotionAdapter
import com.example.coffee_app_damh.Domain.PromotionModel
import com.example.coffee_app_damh.databinding.ActivityUserPromotionsBinding
import com.google.firebase.database.*

class UserPromotionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserPromotionsBinding
    private val database = FirebaseDatabase.getInstance()
    private val list = ArrayList<PromotionModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPromotionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadPromotions()
    }

    private fun loadPromotions() {
        val now = System.currentTimeMillis()
        database.getReference("Promotions").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(PromotionModel::class.java)
                    // Chỉ hiện mã còn hạn sử dụng
                    if (item != null && item.endDate >= now) {
                        list.add(item)
                    }
                }

                binding.recyclerView.adapter = PromotionAdapter(list, isAdmin = false,
                    onCopyClick = { code ->
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Coupon", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(this@UserPromotionsActivity, "Đã copy: $code", Toast.LENGTH_SHORT).show()
                    },
                    onEditClick = {}, onDeleteClick = {}
                )

                binding.emptyTxt.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
