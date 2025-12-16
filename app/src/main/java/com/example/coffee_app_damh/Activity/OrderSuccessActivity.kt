// File: /app/src/main/java/com/example/coffee_app_damh/Activity/OrderSuccessActivity.kt
// HÃY THAY THẾ TOÀN BỘ FILE CŨ BẰNG NỘI DUNG NÀY

package com.example.coffee_app_damh.Activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee_app_damh.Adapter.CheckoutAdapter
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.databinding.ActivityOrderSuccessBinding
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding

    // Các biến để lưu trữ thông tin đơn hàng, tiện cho việc tạo PDF
    private var orderId = ""
    private var orderDate = 0L
    private var customerName = ""
    private var address = ""
    private var total = 0.0
    private var items = arrayListOf<ItemsModel>()
    private val formatter = DecimalFormat("###,###.## VNĐ")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Trình xử lý yêu cầu quyền
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                createAndSavePdf()
            } else {
                Toast.makeText(this, "Cần cấp quyền để lưu hóa đơn", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // === THỨ TỰ LOGIC ĐÚNG ===
        // 1. Lấy tất cả dữ liệu từ Intent
        getIntentData()

        // 2. Hiển thị thông tin lên các TextView
        displayOrderInfo()

        // 3. Hiển thị danh sách sản phẩm lên RecyclerView
        initItemsRecyclerView() // <-- KHÔNG CẦN TRUYỀN THAM SỐ VÌ ĐÃ LÀ BIẾN TOÀN CỤC

        // 4. Gán sự kiện cho các nút bấm
        initListeners()
    }

    private fun getIntentData() {
        orderId = intent.getStringExtra("orderId") ?: ""
        orderDate = intent.getLongExtra("orderDate", 0)
        customerName = intent.getStringExtra("name") ?: ""
        address = intent.getStringExtra("address") ?: ""
        total = intent.getDoubleExtra("total", 0.0)
        @Suppress("DEPRECATION")
        items = intent.getSerializableExtra("items") as? ArrayList<ItemsModel> ?: arrayListOf()
    }

    private fun displayOrderInfo() {
        val dateString = dateFormat.format(Date(orderDate))
        val totalString = formatter.format(total)

        val info = """
            Mã đơn hàng: #${orderId.take(6).uppercase()}
            Ngày đặt: $dateString
            Người nhận: $customerName
            Địa chỉ: $address
        """.trimIndent()

        binding.orderInfoTxt.text = info
        binding.totalTxt.text = totalString
    }

    // Hàm này bây giờ sẽ sử dụng biến `items` toàn cục đã được lấy trong getIntentData()
    private fun initItemsRecyclerView() {
        if(items.isNotEmpty()) {
            binding.itemsRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.itemsRecyclerView.adapter = CheckoutAdapter(items)
        }
    }

    private fun initListeners() {
        binding.backToHomeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        binding.downloadPdfBtn.setOnClickListener {
            checkStoragePermission()
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createAndSavePdf()
            return
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                createAndSavePdf()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun createAndSavePdf() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            textSize = 24f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
        }
        val textPaint = Paint().apply {
            textSize = 14f
        }

        var yPosition = 60f

        canvas.drawText("HÓA ĐƠN BÁN HÀNG", 180f, yPosition, titlePaint)
        yPosition += 40f
        canvas.drawText("Koda Coffee", 40f, yPosition, headerPaint)
        yPosition += 20f
        canvas.drawText("Ngày: ${dateFormat.format(Date(orderDate))}", 40f, yPosition, textPaint)
        yPosition += 20f
        canvas.drawText("Mã HĐ: #${orderId.take(6).uppercase()}", 40f, yPosition, textPaint)
        yPosition += 40f

        canvas.drawText("THÔNG TIN KHÁCH HÀNG", 40f, yPosition, headerPaint)
        yPosition += 25f
        canvas.drawText("Tên: $customerName", 40f, yPosition, textPaint)
        yPosition += 20f
        canvas.drawText("Địa chỉ: $address", 40f, yPosition, textPaint)
        yPosition += 40f

        canvas.drawText("CHI TIẾT ĐƠN HÀNG", 40f, yPosition, headerPaint)
        yPosition += 10f
        canvas.drawLine(40f, yPosition, 555f, yPosition, textPaint)
        yPosition += 20f


        for (item in items) {
            canvas.drawText("${item.title} (x${item.numberInCart})", 40f, yPosition, textPaint)
            val itemPriceText = formatter.format(item.price * item.numberInCart)
            canvas.drawText(itemPriceText, 450f, yPosition, textPaint)
            yPosition += 20f
        }
        yPosition += 5f
        canvas.drawLine(40f, yPosition, 555f, yPosition, textPaint)
        yPosition += 25f

        headerPaint.textSize = 18f
        canvas.drawText("TỔNG CỘNG:", 40f, yPosition, headerPaint)
        canvas.drawText(formatter.format(total), 430f, yPosition, headerPaint)
        yPosition += 40f

        canvas.drawText("Cảm ơn quý khách đã mua hàng!", 180f, yPosition, textPaint)

        pdfDocument.finishPage(page)

        val fileName = "HoaDon_${orderId.take(6)}.pdf"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                        Toast.makeText(this, "Hóa đơn đã được lưu vào thư mục Downloads", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                val file = java.io.File(filePath, fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(this, "Hóa đơn đã được lưu vào thư mục Downloads", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi khi lưu hóa đơn: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }

}
