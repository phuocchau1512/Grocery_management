<?php
include '../connection.php';

header('Content-Type: application/json; charset=UTF-8');

// Kiểm tra kết nối database
if (!$connectNow) {
    die(json_encode(["success" => false, "message" => "Lỗi kết nối database"]));
}

// Kiểm tra phương thức request
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra user_id
if (!isset($_GET['user_id']) || !is_numeric($_GET['user_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu hoặc sai user_id"]);
    exit;
}

$user_id = intval($_GET['user_id']);

// Chuẩn bị truy vấn SQL
$query = "SELECT p.id, p.name, p.barcode, p.img, p.description, i.quantity, i.note, p.is_private
          FROM inventory i 
          JOIN products p ON i.product_id = p.id 
          WHERE i.user_id = ?";

$stmt = $connectNow->prepare($query);

if (!$stmt) {
    echo json_encode(["success" => false, "message" => "Lỗi chuẩn bị truy vấn: " . $connectNow->error]);
    exit;
}

// Bind tham số và thực thi truy vấn
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$products = [];

while ($row = $result->fetch_assoc()) {
    $products[] = $row;
}

// Trả về kết quả JSON
echo json_encode(["success" => true, "products" => $products], JSON_UNESCAPED_UNICODE);

$connectNow->close();
?>
