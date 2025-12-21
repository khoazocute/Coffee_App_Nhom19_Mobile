package com.example.coffee_app_damh.Activity

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

        // Lấy danh sách từ node "users"
        database.getReference("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<UserModel>()

                for (child in snapshot.children) {
                    val user = child.getValue(UserModel::class.java)
                    if (user != null) {
                        user.uid = child.key ?: "" // Lấy key làm UID nếu trong model chưa có

                        // Không hiển thị chính bản thân Admin trong danh sách để tránh tự xóa mình
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
            // quy định các item sẽ xếp chồng lên nhau
            binding.usersRecyclerView.adapter = ManageUserAdapter(users) { userToDelete ->
                confirmDeleteUser(userToDelete)
            }
        }
    }

    private fun confirmDeleteUser(user: UserModel) {
        AlertDialog.Builder(this)
            .setTitle("Xóa người dùng")
            .setMessage("Bạn có chắc chắn muốn xóa user '${user.email}' khỏi hệ thống?\n(Lưu ý: Tài khoản đăng nhập Auth sẽ không bị xóa ngay lập tức)")
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
