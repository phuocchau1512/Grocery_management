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

if (!isset($_POST['id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu ID công thức"]);
    exit;
}

$recipe_id = intval($_POST['id']);

// Lấy đường dẫn ảnh từ database
$sqlGetImage = "SELECT img FROM recipes WHERE id = ?";
$stmtGet = $connectNow->prepare($sqlGetImage);
$stmtGet->bind_param("i", $recipe_id);
$stmtGet->execute();
$result = $stmtGet->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "Không tìm thấy công thức"]);
    exit;
}

$row = $result->fetch_assoc();
$imagePath = $row['img'];

// Xóa file ảnh
$serverPath = __DIR__ . '/' . basename(dirname(__FILE__)) . '/' . $imagePath;
if (file_exists($serverPath)) {
    unlink($serverPath);
}

// Xóa công thức khỏi database
$sqlDelete = "DELETE FROM recipes WHERE id = ?";
$stmtDelete = $connectNow->prepare($sqlDelete);
$stmtDelete->bind_param("i", $recipe_id);

if (!$stmtDelete->execute()) {
    echo json_encode(["success" => false, "message" => "Lỗi khi xóa công thức: " . $stmtDelete->error]);
    exit;
}

echo json_encode(["success" => true, "message" => "Xóa công thức thành công"]);
$connectNow->close();
?>
