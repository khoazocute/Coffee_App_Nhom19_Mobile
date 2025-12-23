package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.PromotionAdapter
import com.example.coffee_app_damh.Domain.PromotionModel
import com.example.coffee_app_damh.databinding.ActivityManagePromotionsBinding
import com.google.firebase.database.*

class ManagePromotionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagePromotionsBinding
    private val database = FirebaseDatabase.getInstance()
    private val list = ArrayList<PromotionModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagePromotionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
        binding.addFab.setOnClickListener {
            startActivity(Intent(this, AddPromotionActivity::class.java))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        loadData()
    }

    private fun loadData() {
        database.getReference("Promotions").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(PromotionModel::class.java)
                    item?.let { list.add(it) }
                }

                binding.recyclerView.adapter = PromotionAdapter(list, isAdmin = true,
                    onCopyClick = {},
                    onEditClick = { promo ->
                        val intent = Intent(this@ManagePromotionsActivity, AddPromotionActivity::class.java)
                        intent.putExtra("object", promo)
                        startActivity(intent)
                    },
                    onDeleteClick = { promo -> showDeleteDialog(promo) }
                )

                binding.emptyTxt.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showDeleteDialog(promo: PromotionModel) {
        AlertDialog.Builder(this)
            .setTitle("Xóa mã?")
            .setMessage("Xóa mã ${promo.code}?")
            .setPositiveButton("Xóa") { _, _ ->
                database.getReference("Promotions").child(promo.code).removeValue()
            }
            .setNegativeButton("Hủy", null).show()
    }
}
