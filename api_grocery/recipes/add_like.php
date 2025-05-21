<?php
include '../connection.php';
header('Content-Type: application/json');

if (!isset($connectNow)) {
    die(json_encode(["success" => false, "message" => "Lỗi kết nối database"]));
}

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

if (!isset($_POST['user_id'], $_POST['recipe_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin người dùng hoặc công thức"]);
    exit;
}

$user_id = intval($_POST['user_id']);
$recipe_id = intval($_POST['recipe_id']);

// Kiểm tra xem người dùng đã like công thức này chưa
$check_sql = "SELECT * FROM likes WHERE user_id = ? AND recipe_id = ?";
$check_stmt = $connectNow->prepare($check_sql);
$check_stmt->bind_param("ii", $user_id, $recipe_id);
$check_stmt->execute();
$check_result = $check_stmt->get_result();

if ($check_result->num_rows > 0) {
    // Đã like → Bỏ like
    $delete_sql = "DELETE FROM likes WHERE user_id = ? AND recipe_id = ?";
    $delete_stmt = $connectNow->prepare($delete_sql);
    $delete_stmt->bind_param("ii", $user_id, $recipe_id);
    $delete_stmt->execute();

    // Trừ lượt like
    $update_sql = "UPDATE recipes SET likes = GREATEST(likes - 1, 0) WHERE id = ?";
    $update_stmt = $connectNow->prepare($update_sql);
    $update_stmt->bind_param("i", $recipe_id);
    $update_stmt->execute();

    echo json_encode(["success" => true, "liked" => false, "message" => "Đã bỏ like"]);
} else {
    // Chưa like → Thêm like
    $insert_sql = "INSERT INTO likes (user_id, recipe_id) VALUES (?, ?)";
    $insert_stmt = $connectNow->prepare($insert_sql);
    $insert_stmt->bind_param("ii", $user_id, $recipe_id);
    $insert_stmt->execute();

    // Cộng lượt like
    $update_sql = "UPDATE recipes SET likes = likes + 1 WHERE id = ?";
    $update_stmt = $connectNow->prepare($update_sql);
    $update_stmt->bind_param("i", $recipe_id);
    $update_stmt->execute();

    echo json_encode(["success" => true, "liked" => true, "message" => "Đã like"]);
}

$connectNow->close();
?>
