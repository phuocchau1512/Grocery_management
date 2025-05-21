<?php
include_once "../connection.php";

// Kiểm tra nếu người dùng đã nhấn nút "Update Item"
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Lấy dữ liệu từ form
    $product_id = intval($_POST['product_id']);
    $product_name = mysqli_real_escape_string($connectNow, $_POST['p_name']);
    $product_barcode = mysqli_real_escape_string($connectNow, $_POST['p_barcode']);
    $product_desc = mysqli_real_escape_string($connectNow, $_POST['p_desc']);
    $is_private = intval($_POST['is_private']);
    $existingImage = $_POST['existingImage'];  // Đường dẫn ảnh cũ

    // Kiểm tra nếu có ảnh mới được tải lên
    if (isset($_FILES['newImage']) && $_FILES['newImage']['error'] == 0) {
        $newImage = $_FILES['newImage'];
        
        // Tạo tên ảnh duy nhất (nếu cần thiết)
        $imageExtension = pathinfo($newImage['name'], PATHINFO_EXTENSION);  // Lấy phần mở rộng của ảnh
        $imageName = uniqid('product_', true) . '.' . $imageExtension; // Tạo tên ảnh mới duy nhất
        
        // Đường dẫn lưu ảnh vào thư mục uploads
        $imagePath = "products/uploads/" . $imageName;
        
        // Nếu có ảnh cũ, xóa ảnh cũ trước khi lưu ảnh mới
        if ($existingImage && file_exists("../".$existingImage)) {
            unlink("../".$existingImage);  // Xóa ảnh cũ
        }
        
        // Di chuyển ảnh mới vào thư mục lưu trữ
        move_uploaded_file($newImage['tmp_name'], "../".$imagePath);
    } else {
        // Nếu không có ảnh mới, giữ nguyên ảnh cũ
        $imagePath = $existingImage;
    }

    // Cập nhật thông tin sản phẩm trong cơ sở dữ liệu
    $update_query = "UPDATE products SET 
                        name = '$product_name', 
                        barcode = '$product_barcode', 
                        description = '$product_desc', 
                        img = '$imagePath', 
                        is_private = '$is_private' 
                    WHERE id = '$product_id'";

    if (mysqli_query($connectNow, $update_query)) {
        echo 'Cập nhật sản phẩm thành công!';
    } else {
        echo "Cập nhật sản phẩm thất bại: " . mysqli_error($connectNow);
    }
}
?>
