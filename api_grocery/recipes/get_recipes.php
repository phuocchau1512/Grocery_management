<?php
include '../connection.php';
header('Content-Type: application/json');

// Chỉ cho phép phương thức GET
if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra dữ liệu đầu vào
if (!isset($_GET['user_id']) || !isset($_GET['page']) || !isset($_GET['limit'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin user_id, page hoặc limit"]);
    exit;
}

$user_id = intval($_GET['user_id']);
$page = intval($_GET['page']);
$limit = intval($_GET['limit']);

// Tính toán offset cho phân trang
$offset = ($page - 1) * $limit;

// Lấy danh sách công thức của user đó, đồng thời kiểm tra xem user đó đã like chưa (is_like)
$sql = "SELECT 
            r.id, r.title, r.ingredients, r.instructions, r.img, 
            r.likes, r.time_minutes, r.user_id,
            EXISTS (
                SELECT 1 FROM likes l 
                WHERE l.user_id = ? AND l.recipe_id = r.id
            ) AS is_like
        FROM recipes r
        ORDER BY r.likes DESC, r.time_minutes ASC
        LIMIT ? OFFSET ?";


$stmt = $connectNow->prepare($sql);

if ($stmt) {
    $stmt->bind_param("iii", $user_id, $limit, $offset);
    $stmt->execute();
    $result = $stmt->get_result();

    $recipes = [];
    while ($row = $result->fetch_assoc()) {
        // is_like trả về 1 hoặc 0, ép kiểu rõ ràng cho client dễ dùng
        $row['is_like'] = boolval($row['is_like']);
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
