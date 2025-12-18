// HÃY THAY THẾ TOÀN BỘ NỘI DUNG FILE CŨ BẰNG FILE NÀY
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

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                Glide.with(this).load(it).into(binding.productImageView)
                imageBase64 = encodeImageToBase64(it)
                if (imageBase64 == null) {
                    Toast.makeText(this, "Không thể chuyển đổi ảnh, vui lòng thử ảnh khác", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadCategories()
        initListeners()
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

        if (imageBase64.isNullOrEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ảnh hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        // Nếu không có loại nào được chọn (và danh sách loại có sẵn), tự động gán loại đầu tiên
        if (selectedCategoryId == -1 && categories.isNotEmpty()) {
            selectedCategoryId = categories[0].id
        }

        showLoading(true)
        saveProductToDatabase(title, description, priceStr.toDouble(), selectedCategoryId, stockStr.toInt(), imageBase64!!)
    }

    private fun saveProductToDatabase(title: String, description: String, price: Double, categoryId: Int, stock: Int, base64: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("Items")
        val itemId = databaseRef.push().key ?: ""

        val newProduct = ItemsModel().apply {
            this.id = itemId
            this.title = title
            this.description = description
            this.price = price
            this.rating = (35..50).random() / 10.0
            this.stock = stock
            //this.categoryId = categoryId
            this.picBase64 = base64
        }

        databaseRef.child(itemId).setValue(newProduct).addOnCompleteListener { task ->
            showLoading(false)
            if (task.isSuccessful) {
                Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Lưu sản phẩm thất bại: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ================================================================
    // === PHẦN CODE QUAN TRỌNG ĐÃ ĐƯỢC BỔ SUNG LẠI ĐẦY ĐỦ Ở ĐÂY ===
    // ================================================================

    private fun loadCategories() {
        showLoading(true)
        FirebaseDatabase.getInstance().getReference("Category")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    categories.clear()
                    snapshot.children.forEach {
                        // Đảm bảo CategoryModel có constructor rỗng
                        it.getValue(CategoryModel::class.java)?.let { category ->
                            categories.add(category)
                        }
                    }
                    // Sau khi tải xong, gọi hàm để hiển thị lên Spinner
                    setupCategorySpinner()
                    showLoading(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    Toast.makeText(this@AddProductActivity, "Lỗi tải phân loại: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupCategorySpinner() {
        // Lấy danh sách tên của các loại
        val categoryTitles = categories.map { it.title }
        // Tạo một adapter đơn giản để hiển thị danh sách tên này
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Gán adapter cho Spinner
        binding.categorySpinner.adapter = adapter

        // Lắng nghe sự kiện khi người dùng chọn một mục trong Spinner
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Khi một mục được chọn, lưu lại ID của mục đó
                if (categories.isNotEmpty()) {
                    selectedCategoryId = categories[position].id
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Không cần làm gì ở đây
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
