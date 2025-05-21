<?php
include '../connection.php';
header('Content-Type: application/json');

// Kiểm tra phương thức
if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra tham số truyền vào
if (!isset($_POST['id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu id"]);
    exit;
}

$id = intval($_POST['id']);

// Lấy giá trị hiện tại của is_check
$getSql = "SELECT is_check FROM shopping_list_item WHERE id = ?";
$getStmt = $connectNow->prepare($getSql);

if (!$getStmt) {
    echo json_encode(["success" => false, "message" => "Lỗi khi chuẩn bị truy vấn SELECT"]);
    exit;
}

$getStmt->bind_param("i", $id);
$getStmt->execute();
$getResult = $getStmt->get_result();

if ($getResult->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "Không tìm thấy mục trong danh sách"]);
    exit;
}

$row = $getResult->fetch_assoc();
$currentCheck = $row['is_check'];

// Đảo giá trị: nếu đã check thì bỏ check, chưa check thì check
$newCheck = $currentCheck == 1 ? 0 : 1;

// Cập nhật lại giá trị is_check
$updateSql = "UPDATE shopping_list_item SET is_check = ? WHERE id = ?";
$updateStmt = $connectNow->prepare($updateSql);

if ($updateStmt) {
    $updateStmt->bind_param("ii", $newCheck, $id);
    if ($updateStmt->execute()) {
        echo json_encode(["success" => true, "message" => "Cập nhật trạng thái is_check thành công", "new_is_check" => $newCheck]);
    } else {
        echo json_encode(["success" => false, "message" => "Lỗi khi cập nhật dữ liệu"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Lỗi khi chuẩn bị truy vấn UPDATE"]);
}

$connectNow->close();
?>
