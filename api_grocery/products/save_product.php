<?php
include '../connection.php';

header('Content-Type: application/json');

// Kiểm tra phương thức
if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra dữ liệu cần thiết
if (!isset($_POST['user_id'], $_POST['product_id'], $_POST['quantity'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin cần thiết"]);
    exit;
}

$user_id = intval($_POST['user_id']);
$product_id = intval($_POST['product_id']);
$quantity = intval($_POST['quantity']);
$note = isset($_POST['note']) ? $_POST['note'] : null;

// Kiểm tra xem sản phẩm đã có trong kho chưa
$checkSQL = "SELECT quantity FROM inventory WHERE user_id = ? AND product_id = ?";
$stmt = $connectNow->prepare($checkSQL);
$stmt->bind_param("ii", $user_id, $product_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // Đã có sản phẩm -> cập nhật số lượng
    $row = $result->fetch_assoc();
    $newQuantity = $row['quantity'] + $quantity;

    $updateSQL = "UPDATE inventory SET quantity = ?, note = ? WHERE user_id = ? AND product_id = ?";
    $stmt = $connectNow->prepare($updateSQL);
    $stmt->bind_param("isii", $newQuantity, $note, $user_id, $product_id);
    $stmt->execute();

    echo json_encode(["success" => true, "message" => "Cập nhật số lượng thành công"]);
} else {
    // Chưa có sản phẩm -> thêm mới vào kho
    $insertSQL = "INSERT INTO inventory (user_id, product_id, quantity, note) VALUES (?, ?, ?, ?)";
    $stmt = $connectNow->prepare($insertSQL);
    $stmt->bind_param("iiis", $user_id, $product_id, $quantity, $note);
    $stmt->execute();

    echo json_encode(["success" => true, "message" => "Thêm sản phẩm vào kho thành công"]);
}

$connectNow->close();
?>
