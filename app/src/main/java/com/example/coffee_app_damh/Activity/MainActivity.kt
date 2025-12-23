package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.PopupWindow
import android.widget.Toast

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.R
import com.example.coffee_app_damh.Adapter.CategoryAdapter
import com.example.coffee_app_damh.Adapter.PopularAdapter
import com.example.coffee_app_damh.Adapter.SearchAdapter
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.LoginActivity
import com.example.coffee_app_damh.ViewModel.MainViewModel
import com.example.coffee_app_damh.databinding.ActivityMainBinding
import com.example.coffee_app_damh.databinding.PopupSearchResultBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale


class MainActivity : AppCompatActivity() {
    // ViewBinding giúp truy cập trực tiếp các View trong XML
    // mà không cần findViewById()
    lateinit var binding: ActivityMainBinding //binding là biến dùng để truy cập giao diện và MainBinding tự động sinh ra
    //Lúc này binding “gắn” file XML vào Activity, và bạn có thể gọi trực tiếp mọi View trong XML mà không cần findViewById() nữa.
    private val viewModel= MainViewModel()
    // Khởi tạo ViewModel để lấy dữ liệu Banner, Category, Popular

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //gọi các hàm khởi tạo dữ liệu giao diện
        initBanner()
        initCategory()
        initPopular()
        initCartMenu()
        initProfileButton()
        initHistoryButton()
        initPromotionsButton()
        //Khởi chạy chức năng tìm kiếm
        preloadAllProductsForSearch()
        setupSearchListener()
    }

    private fun initPromotionsButton() {
        binding.explorerBtn.setOnClickListener {
            startActivity(Intent(this, UserPromotionsActivity::class.java))
        }
    }

    private fun initHistoryButton() {
        binding.historyBtn.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }
    }

    private fun initProfileButton() {
        binding.profileBtn.setOnClickListener {
            // TẠM THỜI: luôn mở ProfileActivity để test
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    // --- CÁC HÀM CHO CHỨC NĂNG TÌM KIẾM ---


    // --- CÁC THUỘC TÍNH MỚI CHO CHỨC NĂNG TÌM KIẾM ---
    private var allProducts: List<ItemsModel> = listOf() // Danh sách chứa tất cả sản phẩm
    var searchPopupWindow: PopupWindow? = null // biến để quản lý popup
    private var isDataLoadedForSearch = false // Cờ để chỉ tải dữ liệu tìm kiếm 1 lần
    private fun preloadAllProductsForSearch() {
        if (isDataLoadedForSearch) return //Nếu tải rồi thì không tải dữ liệu lại nữa
        //Quan sát LiveData từ viewModel để lấy tất cả sản phẩm
        viewModel.loadAllItems().observe(this, { items ->
            allProducts = items //lưu dữ liệu
            isDataLoadedForSearch = true //Đánh dấu dữ liệu đã sẵn sàng
        })
    }

    private fun setupSearchListener() {

    //Gắn TextWatcher vào ô tìm kiếm để nghe sự thay đổi
        binding.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            //Hàm được gọi mỗi khi có thay đổi kí tự
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isDataLoadedForSearch) return

                val query = s.toString().trim()
                //Lọc và so sánh với từ khóa không phân biệt hoa thường
                if (query.length > 1) { // Tìm khi có từ 2 ký tự trở lên
                    val filteredList = allProducts.filter { item ->
                        item.title.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
                    }

                    // Cập nhật hoặc hiển thị popup
                    searchPopupWindow = showOrUpdateSearchPopup(filteredList, searchPopupWindow)
                } else {
                    searchPopupWindow?.dismiss() // Ẩn popup nếu từ khóa quá ngắn hoặc không có gì trong ô tìm kiếm
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    /**
     * [Luồng 2 - Giai đoạn Hiển thị] Tạo, định vị và cập nhật nội dung của PopupWindow.
     * * @param filteredList Danh sách sản phẩm đã được lọc.
     * //@param existingPopupWindow Đối tượng PopupWindow hiện tại (nếu có).
     * //@return Trả về đối tượng PopupWindow (mới hoặc đã cập nhật) để MainActivity lưu lại trạng thái.
     */
    private fun showOrUpdateSearchPopup(filteredList: List<ItemsModel>, existingPopupWindow: PopupWindow?): PopupWindow? {
        //Nếu rỗng thì không có gì để hiển thị
        if (filteredList.isEmpty()) {
            existingPopupWindow?.dismiss()
            return null
        }
        //Chuẩn bị view và adapter
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_search_result, null)
        val searchRecyclerView = popupView.findViewById<RecyclerView>(R.id.searchRecyclerView)
        val searchAdapter = SearchAdapter(filteredList) //Mở SearchAdapter và truyền vào danh sách sản phâẩm đã lọc

        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchRecyclerView.adapter = searchAdapter

        val popupWindow: PopupWindow
        // Nếu popup chưa tồn tại hoặc không hiển thị, tạo mới
        if (existingPopupWindow == null || !existingPopupWindow.isShowing) {
            popupWindow = PopupWindow(
                popupView,
                binding.searchEdt.width,
                RecyclerView.LayoutParams.WRAP_CONTENT,
                true // Có thể focus
            )
            popupWindow.showAsDropDown(binding.searchEdt)
        } else {
            // Nếu đã tồn tại, chỉ cập nhật nội dung
            existingPopupWindow.contentView = popupView
            popupWindow = existingPopupWindow
        }

        // Luôn gán lại sự kiện click để đảm bảo popup hiện tại được tắt
        searchAdapter.onItemClick = {
            popupWindow.dismiss()
            binding.searchEdt.text.clear()
        }

        return popupWindow
    }
    private fun initCartMenu() {
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }



    // ------------------------- BANNER -----------------------------
    private fun initBanner() {
        // Hiện progress trong lúc chờ load dữ liệu
        binding.progressBarBanner.visibility = View.VISIBLE

        // Quan sát LiveData từ ViewModel
        viewModel.loadBanner().observeForever { list ->
            // Load ảnh banner đầu tiên bằng Glide
            Glide.with(this@MainActivity)
                .load(list[0].url)
                .into(binding.banner)

            // Tắt progress khi dữ liệu đã trả về
            binding.progressBarBanner.visibility = View.GONE
        }

        // Gửi yêu cầu load banner từ Firebase
        viewModel.loadBanner()
    }

    // ---------------------- CATEGORY ------------------------------
    private fun initCategory() {
        binding.progressCategory.visibility = View.VISIBLE

        // Quan sát dữ liệu category từ ViewModel
        viewModel.loadCategory().observeForever { list ->

            // Hiển thị Category theo dạng danh sách ngang
            binding.recyclerViewCat.layoutManager =
                LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

            // Gán Adapter để hiển thị danh sách Category
            binding.recyclerViewCat.adapter = CategoryAdapter(list)

            // Ẩn progress bar
            binding.progressCategory.visibility = View.GONE
        }

        // Bắt đầu tải dữ liệu Category từ Firebase
        viewModel.loadCategory()
    }

    // ---------------------- POPULAR ITEMS --------------------------
    private fun initPopular() {
        binding.progressBarPupolar.visibility = View.VISIBLE

        // Quan sát danh sách popular từ ViewModel
        viewModel.loadPopular().observeForever { list ->

            // Hiển thị dạng lưới 2 cột
            binding.recyclerViewPopular.layoutManager =
                GridLayoutManager(this, 2)

            // Set Adapter hiển thị danh sách món phổ biến
            binding.recyclerViewPopular.adapter = PopularAdapter(list)

            // Ẩn progress bar khi load xong
            binding.progressBarPupolar.visibility = View.GONE
        }

        // Gọi load dữ liệu từ Firebase
        viewModel.loadPopular()
    }




}


