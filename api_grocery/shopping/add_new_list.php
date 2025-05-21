<?php
include '../connection.php';

header('Content-Type: application/json');

// Chỉ cho phép phương thức POST
if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra dữ liệu đầu vào
if (!isset($_POST['user_id'], $_POST['name'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin user_id hoặc name"]);
    exit;
}

$user_id = intval($_POST['user_id']);
$name = trim($_POST['name']);

// Mặc định các giá trị khác
$is_favorite = 0;
$item_count = 0;
$created_at = date('Y-m-d H:i:s');
$updated_at = $created_at;

// Chuẩn bị và thực thi câu lệnh thêm
$sql = "INSERT INTO shopping_list (user_id, name) VALUES (?, ?)";
$stmt = $connectNow->prepare($sql);
if ($stmt) {
    $stmt->bind_param("is", $user_id, $name);
    $stmt->execute();

    echo json_encode(["success" => true, "message" => "Tạo danh sách mua sắm thành công"]);
} else {
    echo json_encode(["success" => false, "message" => "Lỗi khi chuẩn bị truy vấn"]);
}

$connectNow->close();
?>
