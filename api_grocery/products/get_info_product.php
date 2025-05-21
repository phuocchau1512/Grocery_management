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

// Kiểm tra barcode
if (!isset($_GET['barcode']) || empty($_GET['barcode'])) {
    echo json_encode(["success" => false, "message" => "Thiếu hoặc sai barcode"]);
    exit;
}

$barcode = trim($_GET['barcode']);

// Chuẩn bị truy vấn SQL
$query = "SELECT id, name, barcode, img, description, is_private FROM products WHERE barcode = ?";

$stmt = $connectNow->prepare($query);

if (!$stmt) {
    echo json_encode(["success" => false, "message" => "Lỗi chuẩn bị truy vấn: " . $connectNow->error]);
    exit;
}

// Bind tham số và thực thi truy vấn
$stmt->bind_param("s", $barcode);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode(["success" => true, "product" => $row], JSON_UNESCAPED_UNICODE);
} else {
    echo json_encode(["success" => false, "message" => "Không tìm thấy sản phẩm"]);
}

$connectNow->close();
?>
