# Coffee_App_Nhom19
Ứng dụng điều hành/ đặt hàng coffee online – đồ án NT118_Nhom19
### PHẦN 1: NHÓM XÁC THỰC (AUTHENTICATION)

1. **LoginActivity.java**
    - Nhiệm vụ: Xử lý đăng nhập bằng Email/Password qua Firebase Auth.
    - Luồng: Nhập thông tin -> Firebase Auth lấy uid -> Truy vấn node "users/{uid}/role" -> Admin vào AdminDashboard, User vào MainActivity.
2. **RegisterActivity.java**
    - Nhiệm vụ: Đăng ký tài khoản mới.
    - Luồng: Validate input -> Tạo user trên Firebase Auth -> Lưu thông tin (name, phone, role="user") vào Firebase Database.
3. **ForgotPasswordActivity.java**
    - Nhiệm vụ: Gửi email đặt lại mật khẩu qua Firebase Auth.

---

### PHẦN 2: NHÓM CLIENT (NGƯỜI DÙNG MUA HÀNG)

1. **MainActivity.java**
    - Nhiệm vụ: Trang chủ hiển thị Category, Banner, món phổ biến (PopularAdapter). Có nút "Explorer" vào xem mã giảm giá.
2. **ItemsListActivity.kt**
    - Nhiệm vụ: Hiển thị danh sách sản phẩm theo từng Danh mục cụ thể (Ví dụ: Cà phê, Trà sữa).
3. **DetailActivity.kt**
    - Nhiệm vụ: Xem chi tiết món (Ảnh URL/Base64, chọn size, số lượng). Nút "Add to Cart" gọi ManagmentCart.insertItems().
4. **CartActivity.kt**
    - Nhiệm vụ: Quản lý giỏ hàng (Tăng/giảm/xóa). Nhập Coupon kiểm tra Firebase (Promotions) -> Lưu % giảm vào ManagmentCart -> Tính tổng thanh toán.
5. **CheckoutActivity.kt**
    - Nhiệm vụ: Nhận data từ Cart, nhập địa chỉ/SĐT. Nút "Đặt hàng" tạo OrderModel -> Push lên Firebase node "Orders" -> Xóa giỏ hàng -> OrderSuccess.
6. **OrderSuccessActivity.kt**
    - Nhiệm vụ: Thông báo thành công và điều hướng về trang chủ.
7. **ProfileActivity.kt** (Thông tin cá nhân, Avatar, Menu điều hướng).
8. **EditProfileActivity.kt** (Cập nhật tên, SĐT vào Firebase).
9. **OrderHistoryActivity.kt** (Xem lịch sử đơn hàng cá nhân lọc theo userId).
10. **UserPromotionsActivity.kt** (Xem danh sách mã giảm giá còn hạn, hỗ trợ copy mã).

---

### PHẦN 3: NHÓM ADMIN (QUẢN TRỊ VIÊN)

1. **AdminDashboardActivity.kt** (Màn hình chính Admin với các Card: Sản phẩm, Đơn hàng, User, Thống kê, Khuyến mãi).
2. **ManageProductsActivity.kt** (Danh sách món, hỗ trợ Thêm/Sửa/Xóa).
3. **AddProductActivity.kt** (Form thêm/sửa, chọn ảnh thư viện -> Convert Base64 -> Lưu Firebase).
4. **ManageOrdersActivity.kt** (Quản lý toàn bộ đơn hàng hệ thống).
5. **OrderDetailAdminActivity.kt** (Chi tiết đơn hàng, cập nhật trạng thái đơn: Đang xử lý -> Hoàn thành).
6. **ManagePromotionsActivity.kt** (Danh sách mã giảm giá).
7. **AddPromotionActivity.kt** (Thêm/Sửa mã, chọn ngày bằng DatePicker).
8. **ManageUsersActivity.kt** (Xem danh sách tài khoản đã đăng ký).
9. **StatisticsActivity.kt** (Báo cáo doanh thu theo ngày/tháng, Top món, Biểu đồ MPAndroidChart).

---

### PHẦN 4: NHÓM ADAPTER (RECYCLERVIEW)

1. **PopularAdapter.kt**: Hiện món ở trang chủ.
2. **CartAdapter.kt**: Hiện món trong giỏ (có nút cộng/trừ/xóa).
3. **CheckoutAdapter.kt**: Tóm tắt món khi thanh toán.
4. **OrderHistoryAdapter.kt**: Hiện lịch sử đơn hàng (User).
5. **ManageOrdersAdapter.kt**: Hiện danh sách đơn hàng (Admin).
6. **OrderDetailAdapter.kt**: Hiện chi tiết món trong 1 đơn hàng cụ thể.
7. **ProductManagementAdapter.kt**: Quản lý món (Sửa/Xóa).
8. **PromotionAdapter.kt**: Hiển thị mã giảm giá (Admin sửa/xóa, User copy).
9. **StatisticsAdapter.kt**: Hiển thị số liệu thống kê.

---

### PHẦN 5: DOMAIN & HELPER (MODEL & UTILS)

1. **ItemsModel.kt**: Data sản phẩm (title, price, description, picUrl, picBase64, stock).
2. **OrderModel.kt**: Data đơn hàng (orderId, userId, items, total, status, date).
3. **OrderItemModel.kt**: Data món lẻ trong đơn hàng (để chốt giá tại thời điểm mua).
4. **PromotionModel.kt**: Data mã giảm giá (code, discountPercent, startDate, endDate).
5. **CategoryModel.kt**: Data danh mục.
6. **StatisticModel.kt**: Data dòng thống kê.
7. **ManagmentCart.kt**: Xử lý giỏ hàng (Lưu TinyDB, tính tổng tiền, quản lý % giảm giá, clearCart).
8. **TinyDB.java**: Hỗ trợ lưu Object/List vào SharedPreferences.

---

### CÁC LUỒNG CHÍNH (KEY FLOWS)

1. **Mua hàng**: Login -> Main -> Detail -> Cart (Áp mã) -> Checkout -> Firebase Orders.
2. **Quản lý đơn**: Admin đổi Status đơn trên Firebase -> User thấy cập nhật trong OrderHistory.
3. **Quản lý ảnh**: Admin chọn ảnh -> Base64 -> Firebase -> User dùng Glide decode hiển thị.
4. **Khuyến mãi**: Admin tạo mã -> User xem ở Explorer -> Áp dụng tại Cart (Check Date logic).
