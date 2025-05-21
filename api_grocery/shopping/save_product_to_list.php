<?php
include '../connection.php';
header('Content-Type: application/json');

// Chỉ cho phép phương thức POST
if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra dữ liệu đầu vào
if (!isset($_POST['shopping_list_id'], $_POST['product_id'], $_POST['quantity'])) {
    echo json_encode(["success" => false, "message" => "Thiếu dữ liệu cần thiết"]);
    exit;
}

$shopping_list_id = intval($_POST['shopping_list_id']);
$product_id = intval($_POST['product_id']);
$quantity = intval($_POST['quantity']);
$is_check = 0; // Mặc định là chưa được chọn

// Thêm sản phẩm vào shopping_list_item
$sql = "INSERT INTO shopping_list_item (shopping_list_id, product_id, quantity, is_check) VALUES (?, ?, ?, ?)";
$stmt = $connectNow->prepare($sql);
if ($stmt) {
    $stmt->bind_param("iiii", $shopping_list_id, $product_id, $quantity, $is_check);
    $stmt->execute();

    // Cập nhật item_count trong bảng shopping_list
    $update_sql = "UPDATE shopping_list SET item_count = item_count + 1 WHERE id = ?";
    $update_stmt = $connectNow->prepare($update_sql);
    if ($update_stmt) {
        $update_stmt->bind_param("i", $shopping_list_id);
        $update_stmt->execute();
    }

    echo json_encode(["success" => true, "message" => "Đã thêm sản phẩm vào danh sách"]);
} else {
    echo json_encode(["success" => false, "message" => "Lỗi khi thêm sản phẩm"]);
}

$connectNow->close();
?>
