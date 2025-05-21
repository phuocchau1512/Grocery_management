<?php
include '../connection.php';
header('Content-Type: application/json');

// Chỉ cho phép phương thức GET
if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra dữ liệu đầu vào
if (!isset($_GET['keyword']) || !isset($_GET['page']) || !isset($_GET['limit']) || !isset($_GET['user_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu keyword, page, limit hoặc user_id"]);
    exit;
}

$keyword = '%' . $_GET['keyword'] . '%';
$page = intval($_GET['page']);
$limit = intval($_GET['limit']);
$user_id = intval($_GET['user_id']);
$offset = ($page - 1) * $limit;

// Truy vấn tìm kiếm theo tiêu đề và kiểm tra đã like chưa
$sql = "SELECT 
            r.id, r.title, r.ingredients, r.instructions, r.img, 
            r.likes, r.time_minutes, r.user_id,
            EXISTS (
                SELECT 1 FROM likes l 
                WHERE l.user_id = ? AND l.recipe_id = r.id
            ) AS is_like
        FROM recipes r
        WHERE r.title LIKE ?
        ORDER BY r.likes DESC, r.time_minutes ASC
        LIMIT ? OFFSET ?";

$stmt = $connectNow->prepare($sql);

if ($stmt) {
    $stmt->bind_param("isii", $user_id, $keyword, $limit, $offset);
    $stmt->execute();
    $result = $stmt->get_result();

    $recipes = [];
    while ($row = $result->fetch_assoc()) {
        $row['is_like'] = boolval($row['is_like']); // ép kiểu rõ ràng
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
