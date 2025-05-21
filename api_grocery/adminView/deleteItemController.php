<?php
// Kết nối cơ sở dữ liệu
include_once "../connection.php";

// Kiểm tra nếu có giá trị 'record' được gửi qua POST
if (isset($_POST['record'])) {
    $id = $_POST['record'];  // Lấy ID sản phẩm cần xóa

    // Chuẩn bị câu lệnh SQL để xóa sản phẩm
    $sql = "DELETE FROM products WHERE id = ?";
    
    if ($stmt = $connectNow->prepare($sql)) {
        // Liên kết biến với tham số
        $stmt->bind_param("i", $id);

        // Thực thi câu lệnh SQL
        if ($stmt->execute()) {
            echo "Xóa sản phẩm thành công";  // Trả về thông báo thành công
        } else {
            echo "Lỗi khi xóa sản phẩm";  // Trả về thông báo lỗi
        }
        
        // Đóng statement
        $stmt->close();
    } else {
        echo "Lỗi khi chuẩn bị câu lệnh";  // Thông báo nếu có lỗi trong câu lệnh SQL
    }
} else {
    echo "Không nhận được ID sản phẩm";  // Thông báo nếu không có ID sản phẩm
}

// Đóng kết nối cơ sở dữ liệu
$connectNow->close();
?>
