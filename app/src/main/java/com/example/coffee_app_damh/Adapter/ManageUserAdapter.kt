package com.example.coffee_app_damh.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee_app_damh.Domain.UserModel
import com.example.coffee_app_damh.databinding.ViewholderManageUserBinding

class ManageUserAdapter(
    private val users: List<UserModel>,
    private val onDeleteClick: (UserModel) -> Unit,
    private val onItemClick: (UserModel) -> Unit
) : RecyclerView.Adapter<ManageUserAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderManageUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderManageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        holder.binding.apply {
            nameTxt.text = if (user.name.isNotEmpty()) user.name else "Chưa đặt tên"
            emailTxt.text = user.email
            roleTxt.text = "Vai trò: ${user.role.uppercase()}"

            deleteBtn.setOnClickListener {
                onDeleteClick(user)
            }
            // Bấm vào bất cứ đâu trên item (trừ nút xóa) -> Xem lịch sử
            root.setOnClickListener {
                onItemClick(user)
            }
        }
    }

    override fun getItemCount(): Int = users.size
}
