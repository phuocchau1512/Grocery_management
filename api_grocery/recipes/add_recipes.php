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

// Kiểm tra dữ liệu đầu vào
if (!isset($_POST['title'], $_POST['ingredients'], $_POST['instructions'], $_POST['time_minutes'], $_POST['user_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin công thức"]);
    exit;
}

$title = $_POST['title'];
$ingredients = $_POST['ingredients'];
$instructions = $_POST['instructions'];
$time_minutes = intval($_POST['time_minutes']);
$user_id = intval($_POST['user_id']);
$likes = 0;

// Xử lý ảnh
if (!isset($_FILES['img'])) {
    echo json_encode(["success" => false, "message" => "Chưa chọn ảnh"]);
    exit;
}

$imageName = time() . '_' . basename($_FILES['img']['name']);
$targetDir = "uploads/";
if (!file_exists($targetDir)) {
    mkdir($targetDir, 0777, true);
}
$targetFile = $targetDir . $imageName;

if (!move_uploaded_file($_FILES['img']['tmp_name'], $targetFile)) {
    echo json_encode(["success" => false, "message" => "Lỗi khi tải ảnh lên"]);
    exit;
}

$imagePath = "recipes/uploads/" . $imageName;

// Thêm vào bảng recipes (KHÔNG có description nữa)
$sql = "INSERT INTO recipes (title, ingredients, instructions, img, likes, time_minutes, user_id)
        VALUES (?, ?, ?, ?, ?, ?, ?)";

$stmt = $connectNow->prepare($sql);
if (!$stmt) {
    die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
}

$stmt->bind_param("ssssiii", $title, $ingredients, $instructions, $imagePath, $likes, $time_minutes, $user_id);

if (!$stmt->execute()) {
    die(json_encode(["success" => false, "message" => "Lỗi khi thêm công thức: " . $stmt->error]));
}

echo json_encode(["success" => true, "message" => "Thêm công thức thành công"]);
$connectNow->close();
?>
