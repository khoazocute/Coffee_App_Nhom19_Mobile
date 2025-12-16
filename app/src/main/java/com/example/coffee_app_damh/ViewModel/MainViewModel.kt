package com.example.coffee_app_damh.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.coffee_app_damh.Domain.BannerModel
import com.example.coffee_app_damh.Domain.CategoryModel
import com.example.coffee_app_damh.Domain.ItemsModel
import com.example.coffee_app_damh.Repository.MainRepository

class MainViewModel: ViewModel() {
    private val repository = MainRepository()

    fun loadBanner(): LiveData<MutableList<BannerModel>>{
        return repository.loadBanner()
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        return repository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>>{
        return repository.loadPopular()
    }
//Nhận categoryId từ Activity => trả về livedata => chuyển tiếp cho Repository xử lý
//Gọi hàm từ Repository và trả LiveData lên cho Activity
    fun loadItems(categoryId:String): LiveData<MutableList<ItemsModel>>{
        return repository.loadItemCategory(categoryId) // Gọi hàm và chuyển tiếp yêu cầu xuống Repository
    //ViewModel sẽ nhận LiveData từ Repository va tra nó về Activity
    }

    // Thêm hàm này vào cuối lớp MainViewModel
    fun loadAllItems(): LiveData<MutableList<ItemsModel>> {
        return repository.loadAllItems()}
}
                /*
                Activity
                  ↓ gọi loadItems(categoryId)
                ViewModel
                  ↓ gọi repository.loadItemCategory(categoryId)
                Repository
                  ↓ query Firebase
                  ↓ tạo list ItemsModel
                  ↓ gán LiveData.value
                ViewModel
                  ↓ trả LiveData
                Activity
                  ↓ observe LiveData
                RecyclerView cập nhật UI
                 */