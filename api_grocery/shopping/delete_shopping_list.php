<?php
include '../connection.php';
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

if (!isset($_POST['shopping_list_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin shopping_list_id"]);
    exit;
}

$shopping_list_id = intval($_POST['shopping_list_id']);

$sql = "DELETE FROM shopping_list WHERE id = ?";
$stmt = $connectNow->prepare($sql);

if ($stmt) {
    $stmt->bind_param("i", $shopping_list_id);
    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Xóa danh sách mua sắm và các item thành công"]);
    } else {
        echo json_encode(["success" => false, "message" => "Lỗi khi xóa"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Lỗi khi chuẩn bị truy vấn"]);
}

$connectNow->close();
?>