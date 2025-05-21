<?php
include '../connection.php';

header('Content-Type: application/json');

// Chỉ cho phép phương thức POST
if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra dữ liệu đầu vào
if (!isset($_POST['user_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin user_id"]);
    exit;
}

$user_id = intval($_POST['user_id']);

// Truy vấn danh sách shopping_list theo user_id, sắp xếp theo yêu thích rồi ngày tạo mới nhất
$sql = "SELECT id, name, is_favorite, item_count, created_at, updated_at 
        FROM shopping_list 
        WHERE user_id = ? 
        ORDER BY is_favorite DESC, created_at DESC";

$stmt = $connectNow->prepare($sql);

if ($stmt) {
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $shoppingLists = [];
    while ($row = $result->fetch_assoc()) {
        $shoppingLists[] = $row;
    }

    echo json_encode([
        "success" => true,
        "data" => $shoppingLists
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Lỗi khi chuẩn bị truy vấn"
    ]);
}

$connectNow->close();
?>
