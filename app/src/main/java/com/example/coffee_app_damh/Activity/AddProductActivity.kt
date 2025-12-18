// HÃY THAY THẾ TOÀN BỘ FILE CŨ BẰNG NỘI DUNG NÀY
package com.example.coffee_app_damh.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.Domain.CategoryModel
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.databinding.ActivityAddProductBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var imageBase64: String? = null
    private val categories = mutableListOf<CategoryModel>()
    private var selectedCategoryId: Int = -1
    private var isEditMode = false       // Cờ đánh dấu chế độ sửa
    private var itemToEdit: ItemsModel? = null // Lưu sản phẩm cần sửa

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                Glide.with(this).load(it).into(binding.productImageView)
                imageBase64 = encodeImageToBase64(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kiểm tra xem có phải đang mở để Sửa không
        checkEditMode()

        loadCategories()
        initListeners()
    }

    private fun checkEditMode() {
        // Lấy object được truyền từ ManageProductsActivity (nếu có)
        itemToEdit = intent.getSerializableExtra("object") as? ItemsModel

        if (itemToEdit != null) {
            isEditMode = true
            binding.saveBtn.text = "Cập nhật sản phẩm"
            binding.headerTitle.text = "Sửa sản phẩm"

            // Đổ dữ liệu cũ vào form
            binding.titleEdt.setText(itemToEdit!!.title)
            binding.descriptionEdt.setText(itemToEdit!!.description)
            binding.priceEdt.setText(itemToEdit!!.price.toString())
            binding.stockEdt.setText(itemToEdit!!.stock.toString())

            // Xử lý ảnh cũ
            imageBase64 = itemToEdit!!.picBase64

            // --- SỬA LỖI TẠI ĐÂY: Dùng isNullOrEmpty() thay vì isNotEmpty() ---
            if (!imageBase64.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                    Glide.with(this).load(imageBytes).into(binding.productImageView)
                } catch (e: Exception) { }
            } else if (itemToEdit!!.picUrl.isNotEmpty()) {
                Glide.with(this).load(itemToEdit!!.picUrl[0]).into(binding.productImageView)
            }
        }
    }

    private fun initListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.productImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            pickImageLauncher.launch(intent)
        }

        binding.saveBtn.setOnClickListener {
            validateAndSaveData()
        }
    }

    private fun encodeImageToBase64(imageUri: Uri): String? {
        return try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            }
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun validateAndSaveData() {
        val title = binding.titleEdt.text.toString().trim()
        val description = binding.descriptionEdt.text.toString().trim()
        val priceStr = binding.priceEdt.text.toString().trim()
        val stockStr = binding.stockEdt.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEditMode && imageBase64 == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEditMode && imageBase64.isNullOrEmpty() && itemToEdit?.picUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategoryId == -1 && categories.isNotEmpty()) {
            selectedCategoryId = categories[0].id
        }

        showLoading(true)
        saveProductToDatabase(title, description, priceStr.toDouble(), selectedCategoryId, stockStr.toInt(), imageBase64 ?: "")
    }

    private fun saveProductToDatabase(title: String, description: String, price: Double, categoryId: Int, stock: Int, base64: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("Items")
        val popularRef = FirebaseDatabase.getInstance().getReference("Popular")

        val itemId = if (isEditMode && itemToEdit != null) itemToEdit!!.id else (databaseRef.push().key ?: "")

        val product = ItemsModel().apply {
            this.id = itemId
            this.title = title
            this.description = description
            this.price = price
            this.stock = stock
            this.rating = if (isEditMode) itemToEdit!!.rating else (35..50).random() / 10.0

            this.picBase64 = base64
            if (isEditMode && base64.isEmpty() && itemToEdit!!.picUrl.isNotEmpty()) {
                this.picUrl = itemToEdit!!.picUrl
            }
        }

        databaseRef.child(itemId).setValue(product).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                popularRef.child(itemId).setValue(product)

                showLoading(false)
                val msg = if (isEditMode) "Cập nhật thành công!" else "Thêm mới thành công!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                showLoading(false)
                Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadCategories() {
        showLoading(true)
        FirebaseDatabase.getInstance().getReference("Category")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    categories.clear()
                    snapshot.children.forEach {
                        it.getValue(CategoryModel::class.java)?.let { category ->
                            categories.add(category)
                        }
                    }
                    setupCategorySpinner()
                    showLoading(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    Toast.makeText(this@AddProductActivity, "Lỗi tải phân loại", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupCategorySpinner() {
        val categoryTitles = categories.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        if (isEditMode && itemToEdit != null && categories.isNotEmpty()) {
            // Logic tìm vị trí category cũ nếu cần
        }

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (categories.isNotEmpty()) {
                    selectedCategoryId = categories[position].id
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
