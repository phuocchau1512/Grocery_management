<?php
include '../connection.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

if (!isset($_POST['product_id'], $_POST['name'], $_POST['barcode'], $_POST['description'], $_POST['quantity'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin sản phẩm"]);
    exit;
}

$user_id = $_POST['user_id'] ?? '';
$product_id = intval($_POST['product_id']);
$name = $_POST['name'];
$barcode = $_POST['barcode'];
$description = $_POST['description'];
$quantity = intval($_POST['quantity']);
$note = isset($_POST['note']) ? $_POST['note'] : null;

// Chỉ cập nhật dữ liệu, không thay đổi ảnh
$updateProduct = "UPDATE products SET name = ?, barcode = ?, description = ? WHERE id = ?";
$stmt = $connectNow->prepare($updateProduct);
$stmt->bind_param("sssi", $name, $barcode, $description, $product_id);
$stmt->execute();

// Cập nhật số lượng trong kho
$updateInventory = "UPDATE inventory SET quantity = ?, note = ? WHERE product_id = ? AND user_id = ?";
$stmt = $connectNow->prepare($updateInventory);
$stmt->bind_param("isii", $quantity, $note, $product_id,$user_id);
$stmt->execute();

echo json_encode(["success" => true, "message" => "Cập nhật sản phẩm thành công"]);

$connectNow->close();
?>
