⚙️ Cài đặt & chạy thử
1. Bật môi trường XAMPP
- Mở XAMPP Control Panel
- Bật Apache và MySQL
2. Import cơ sở dữ liệu
- Import file grocery_db.sql vào MySQL (http://localhost/phpmyadmin)
- Chỉ cần đưa nguyên tệp api_grocery và mục htdocs
3. Cấu hình đường dẫn
- Mở file: 'app/src/main/java/com/example/grocerymanagement/data/source/retrofit/RetrofitClient.kt'
- Chỉnh url 'private const val BASE_URL = "http://192.168.x.x/grocery_api/"'
- Thay 192.168.x.x bằng IP nội bộ của máy chạy XAMPP (cùng mạng WiFi với điện thoại giả lập/thật).
- (Có thể lấy bằng cách vào commandPromote nhập Ipconfig thay địa chỉ Ip4 ở phần Wireless LAN adapter Wi-Fi)
4. Bật quyền truy cập HTTP (non-SSL)
- Mở file:
app/src/main/res/xml/network_security_config.xml

-	Đảm bảo có nội dung :

<domain-config cleartextTrafficPermitted="true">
// địa chỉ ip4 của cái đường dẫn phần 3
    <domain includeSubdomains="true">192.168.x.x</domain>
</domain-config>
Lưu ý
