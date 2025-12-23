package com.example.coffee_app_damh.Activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coffee_app_damh.Domain.PromotionModel
import com.example.coffee_app_damh.databinding.ActivityAddPromotionBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddPromotionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPromotionBinding
    private var startTs: Long = 0
    private var endTs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPromotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editItem = intent.getSerializableExtra("object") as? PromotionModel
        if (editItem != null) {
            fillData(editItem)
        }

        binding.startDateBtn.setOnClickListener {
            pickDate { ts ->
                startTs = ts
                binding.startDateTxt.text = formatDate(ts)
            }
        }

        binding.endDateBtn.setOnClickListener {
            pickDate { ts ->
                endTs = ts
                binding.endDateTxt.text = formatDate(ts)
            }
        }

        binding.saveBtn.setOnClickListener { saveToFirebase() }
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun saveToFirebase() {
        val code = binding.codeEdt.text.toString().uppercase().trim()
        val percentStr = binding.percentEdt.text.toString().trim()
        val desc = binding.descEdt.text.toString().trim()

        if (code.isEmpty() || percentStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Mã và % giảm", Toast.LENGTH_SHORT).show()
            return
        }

        val percent = percentStr.toInt()

        if (startTs == 0L || endTs == 0L) {
            Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show()
            return
        }

        if (endTs < startTs) {
            Toast.makeText(this, "Ngày kết thúc phải sau ngày bắt đầu", Toast.LENGTH_SHORT).show()
            return
        }

        val promo = PromotionModel(code, code, percent, desc, startTs, endTs)

        FirebaseDatabase.getInstance().getReference("Promotions").child(code).setValue(promo)
            .addOnSuccessListener {
                Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pickDate(callback: (Long) -> Unit) {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            c.set(y, m, d, 0, 0, 0)
            callback(c.timeInMillis)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun formatDate(ts: Long): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))
    }

    private fun fillData(item: PromotionModel) {
        binding.codeEdt.setText(item.code)
        binding.codeEdt.isEnabled = false
        binding.percentEdt.setText(item.discountPercent.toString())
        binding.descEdt.setText(item.description)
        startTs = item.startDate
        endTs = item.endDate
        binding.startDateTxt.text = formatDate(startTs)
        binding.endDateTxt.text = formatDate(endTs)
        binding.saveBtn.text = "Cập nhật"
    }
}
