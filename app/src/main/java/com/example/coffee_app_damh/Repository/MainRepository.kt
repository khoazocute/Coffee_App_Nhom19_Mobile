package com.example.coffee_app_damh.Repository

import android.R
import android.adservices.adid.AdId
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coffee_app_damh.Domain.BannerModel
import com.example.coffee_app_damh.Domain.CategoryModel
import com.example.coffee_app_damh.Domain.ItemsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
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
//Hàm nhận vào id theo đúng danh mục và trả về Livedata để activity quan sát
    fun loadItemCategory(categoryId: String): LiveData<MutableList<ItemsModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>() //Nơi chứa dữ liệu
        val ref = firebaseDatabase.getReference("Items") //Kết Nối và Truy vấn database
//Tạo query để lọc theo category
        val query: Query = ref.orderByChild("categoryId").equalTo(categoryId)
    
//Firebase Realtime Database → lắng nghe dữ liệu → trả DataSnapshot
//Lắng nghe sự kiện ,Khi firebase trả dữ liệu về => smapshot sẽ chứa toàn bộ danh sách item thuộc category

        query.addListenerForSingleValueEvent(object : ValueEventListener {
//snapshot và bản chụp dữ liệu từ firebase, chứa tất cả các dữ liệu thô lấy từ danh mục người dùng chọn
        override fun onDataChange(snapshot: DataSnapshot) {
            val list = mutableListOf<ItemsModel>()
// Duyệt qua từng giá trị của snapshot, mỗi childsnapshot la 1 san pham
            for(childSnapshot in snapshot.children){
                val item = childSnapshot.getValue(ItemsModel::class.java)
                item?.let { list.add(it) }
            }
            itemsLiveData.value=list
        }
// Gán list vào itemsLiveDate => ViewModel sẽ nhận được list này => Activity đang observe sẽ cập nhật UI
// Chuyển dữ liệu JSON -> object và lưu vào list
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })
    return itemsLiveData
    }

    // Thêm hàm này vào cuối lớp MainRepository
    fun loadAllItems(): LiveData<MutableList<ItemsModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        // Trỏ đến node "Items" để lấy tất cả sản phẩm
        val ref = firebaseDatabase.getReference("Items")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                itemsLiveData.value = list
            }
            override fun onCancelled(error: DatabaseError) {
                // Bạn có thể xử lý lỗi ở đây
            }
        })
        return itemsLiveData
    }

// Repository  trả LiveDate cho ViewModel => ViewModel trả cho Activity => Observe và hiển thị
}
                                                /*
                                                Activity
                                                   ↓ yêu cầu dữ liệu
                                                ViewModel
                                                   ↓ gọi Repository
                                                Repository
                                                   ↓ query Firebase
                                                Firebase
                                                   ↓ trả snapshot (dữ liệu)
                                                Repository
                                                   ↓ convert -> list
                                                   ↓ gán vào LiveData
                                                ViewModel
                                                   ↓ trả LiveData
                                                Activity
                                                   ↓ observe
                                                RecyclerView cập nhật UI
                                                 */
//MainRepository là tầng dữ liệu (Data Layer) trong mô hình MVVM.
//Nó không trực tiếp hiển thị giao diện,
// mà chỉ lo lấy dữ liệu từ Firebase, xử lý logic dữ liệu, và trả kết quả về cho ViewModel.
//Mục tiêu : Kết nối đến Firebase Realtime Database,
// lấy dữ liệu từ node "Banner", và trả về một LiveData để ViewModel có thể quan sát.
/*
✔ Lấy dữ liệu từ Firebase
✔ Xử lý dữ liệu (query, lọc…)
✔ Trả về LiveData hoặc callback
✔ Không làm UI
✔ Không quan sát LiveData

=> Repository chỉ lo data layer.
 */

/* Cách hoạt động chuẩn MVVM
    Activity -> gọi ViewModel
    ViewModel -> gọi Repository
    Repository -> Query Firebase, trả LiveData
    ViewModel -> trả LiveData
    Activity -> observe LiveData -> update RecyclerView
 */