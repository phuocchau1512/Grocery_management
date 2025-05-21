<?php
include '../connection.php';

header('Content-Type: application/json');

// Chỉ cho phép phương thức GET
if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra dữ liệu đầu vào
if (!isset($_GET['user_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin user_id"]);
    exit;
}

$user_id = intval($_GET['user_id']);

// Truy vấn danh sách recipe theo user_id, sắp xếp theo lượt thích giảm dần rồi theo thời gian nấu tăng dần
$sql = "SELECT id, title, ingredients, instructions, img, likes, time_minutes, user_id 
        FROM recipes 
        WHERE user_id = ? 
        ORDER BY likes DESC, time_minutes ASC";

$stmt = $connectNow->prepare($sql);

if ($stmt) {
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $recipes = [];
    while ($row = $result->fetch_assoc()) {
        $recipes[] = $row;
    }

    echo json_encode([
        "success" => true,
        "data" => $recipes
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Lỗi khi chuẩn bị truy vấn"
    ]);
}

$connectNow->close();
?>
