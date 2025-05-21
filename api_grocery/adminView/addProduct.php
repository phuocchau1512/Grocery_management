<?php
include_once "../connection.php";

// Kiểm tra nếu người dùng đã nhấn nút "Update Item"
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Lấy dữ liệu từ form
    $product_name = mysqli_real_escape_string($connectNow, $_POST['p_name']);
    $product_barcode = mysqli_real_escape_string($connectNow, $_POST['p_barcode']);
    $product_desc = mysqli_real_escape_string($connectNow, $_POST['p_desc']);
    $is_private = 0;
    $imagePath = ''; // Mặc định không có ảnh

    // Kiểm tra nếu có ảnh mới được tải lên
    if (isset($_FILES['p_img']) && $_FILES['p_img']['error'] == 0) {
        $newImage = $_FILES['p_img'];
        
        // Tạo tên ảnh duy nhất (nếu cần thiết)
        $imageExtension = pathinfo($newImage['name'], PATHINFO_EXTENSION);  // Lấy phần mở rộng của ảnh
        $imageName = uniqid('product_', true) . '.' . $imageExtension; // Tạo tên ảnh mới duy nhất
        
        // Đường dẫn lưu ảnh vào thư mục uploads
        $imagePath = "products/uploads/" . $imageName;
        
        // Di chuyển ảnh mới vào thư mục lưu trữ
        move_uploaded_file($newImage['tmp_name'], "../".$imagePath);
    }

    // Câu lệnh INSERT dữ liệu vào cơ sở dữ liệu
    $insert_query = "INSERT INTO products (name, barcode, description, img, is_private) 
                     VALUES ('$product_name', '$product_barcode', '$product_desc', '$imagePath', '$is_private')";

    if (mysqli_query($connectNow, $insert_query)) {
        echo 'Thêm sản phẩm thành công!';
    } else {
        echo "Thêm sản phẩm thất bại: " . mysqli_error($connectNow);
    }
}
?>
