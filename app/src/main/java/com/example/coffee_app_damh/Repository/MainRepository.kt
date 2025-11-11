package com.example.coffee_app_damh.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coffee_app_damh.Domain.BannerModel
import com.example.coffee_app_damh.Domain.CategoryModel
import com.example.coffee_app_damh.Domain.ItemsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//Repository chính là trung gian giữa Firebase và ứng dụng
//Kết nối với firebase lấy dữ liệu và trả về cho ViewModel
class MainRepository {
    private val firebaseDatabase= FirebaseDatabase.getInstance()//Liên kết với firebase và Truy cập toàn bộ dữ liệu

    fun loadBanner(): LiveData<MutableList<BannerModel>>{
        val listData = MutableLiveData<MutableList<BannerModel>>()
        val bannerRef = firebaseDatabase.getReference("Banner") //lấy data của node banner trong file json

        bannerRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BannerModel>()
                    for(childSnapshot in snapshot.children){
                        val item = childSnapshot.getValue(BannerModel::class.java)
                        item?.let { list.add(it) }
                    }
                listData.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val bannerRef = firebaseDatabase.getReference("Category") //lấy data của node banner trong file json

        bannerRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for(childSnapshot in snapshot.children){
                    val item = childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>>{
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val bannerRef = firebaseDatabase.getReference("Popular") //lấy data của node banner trong file json

        bannerRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for(childSnapshot in snapshot.children){
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }
//MainRepository là tầng dữ liệu (Data Layer) trong mô hình MVVM.
//Nó không trực tiếp hiển thị giao diện,
// mà chỉ lo lấy dữ liệu từ Firebase, xử lý logic dữ liệu, và trả kết quả về cho ViewModel.
//Mục tiêu : Kết nối đến Firebase Realtime Database,
// lấy dữ liệu từ node "Banner", và trả về một LiveData để ViewModel có thể quan sát.
}