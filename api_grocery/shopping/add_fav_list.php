<?php
include '../connection.php';
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

if (!isset($_POST['shopping_list_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu shopping_list_id"]);
    exit;
}

$shopping_list_id = intval($_POST['shopping_list_id']);

// Lấy giá trị hiện tại của is_favorite
$getSql = "SELECT is_favorite FROM shopping_list WHERE id = ?";
$getStmt = $connectNow->prepare($getSql);

if (!$getStmt) {
    echo json_encode(["success" => false, "message" => "Lỗi khi chuẩn bị truy vấn SELECT"]);
    exit;
}

$getStmt->bind_param("i", $shopping_list_id);
$getStmt->execute();
$getResult = $getStmt->get_result();

if ($getResult->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "Không tìm thấy danh sách"]);
    exit;
}

$row = $getResult->fetch_assoc();
$currentFavorite = $row['is_favorite'];

// Đảo giá trị
$newFavorite = $currentFavorite == 1 ? 0 : 1;

// Cập nhật lại
$updateSql = "UPDATE shopping_list SET is_favorite = ? WHERE id = ?";
$updateStmt = $connectNow->prepare($updateSql);

if ($updateStmt) {
    $updateStmt->bind_param("ii", $newFavorite, $shopping_list_id);
    if ($updateStmt->execute()) {
        echo json_encode(["success" => true, "message" => "Cập nhật trạng thái yêu thích thành công", "new_favorite" => $newFavorite]);
    } else {
        echo json_encode(["success" => false, "message" => "Lỗi khi cập nhật"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Lỗi khi chuẩn bị truy vấn UPDATE"]);
}

$connectNow->close();
?>
