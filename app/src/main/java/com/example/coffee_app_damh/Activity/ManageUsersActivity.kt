// File: /app/src/main/java/com/example/coffee_app_damh/Activity/ManageUsersActivity.kt

package com.example.coffee_app_damh.Activity

import android.content.Intent // Nhớ import Intentimport android.os.Bundle
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.ManageUserAdapter
import com.example.coffee_app_damh.Domain.UserModel
import com.example.coffee_app_damh.databinding.ActivityManageUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageUsersBinding
    private val database = FirebaseDatabase.getInstance()
    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        loadUsers()
    }

    private fun loadUsers() {
        binding.progressBar.visibility = View.VISIBLE

        database.getReference("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<UserModel>()

                for (child in snapshot.children) {
                    val user = child.getValue(UserModel::class.java)
                    if (user != null) {
                        user.uid = child.key ?: ""
                        if (user.uid != currentUserUid) {
                            userList.add(user)
                        }
                    }
                }

                binding.progressBar.visibility = View.GONE
                setupRecyclerView(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ManageUsersActivity, "Lỗi tải: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(users: List<UserModel>) {
        if (users.isEmpty()) {
            binding.emptyTxt.visibility = View.VISIBLE
            binding.usersRecyclerView.visibility = View.GONE
        } else {
            binding.emptyTxt.visibility = View.GONE
            binding.usersRecyclerView.visibility = View.VISIBLE

            binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)

            // === CẬP NHẬT ADAPTER TẠI ĐÂY ===
            binding.usersRecyclerView.adapter = ManageUserAdapter(
                users = users,
                onDeleteClick = { userToDelete ->
                    confirmDeleteUser(userToDelete)
                },
                // Thêm sự kiện click vào item
                onItemClick = { userToView ->
                    viewUserHistory(userToView)
                }
            )
        }
    }

    // Hàm chuyển sang màn hình lịch sử và gửi ID
    private fun viewUserHistory(user: UserModel) {
        val intent = Intent(this, OrderHistoryActivity::class.java)
        intent.putExtra("userId", user.uid) // Gửi ID của user này sang
        startActivity(intent)
    }

    private fun confirmDeleteUser(user: UserModel) {
        AlertDialog.Builder(this)
            .setTitle("Xóa người dùng")
            .setMessage("Bạn có chắc chắn muốn xóa user '${user.email}' khỏi hệ thống?")
            .setPositiveButton("Xóa") { dialog, _ ->
                deleteUserFromDatabase(user.uid)
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteUserFromDatabase(uid: String) {
        database.getReference("users").child(uid).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Đã xóa thông tin người dùng", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi xóa: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
