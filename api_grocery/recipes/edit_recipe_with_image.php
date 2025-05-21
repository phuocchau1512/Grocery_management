<?php
include '../connection.php';
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

if (!isset($_POST['id'], $_POST['title'], $_POST['ingredients'], $_POST['instructions'], $_POST['time_minutes'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin"]);
    exit;
}

$id = intval($_POST['id']);
$title = $_POST['title'];
$ingredients = $_POST['ingredients'];
$instructions = $_POST['instructions'];
$time_minutes = intval($_POST['time_minutes']);

if (!isset($_FILES['img'])) {
    echo json_encode(["success" => false, "message" => "Chưa chọn ảnh"]);
    exit;
}

// Xoá ảnh cũ
$getOldImg = $connectNow->prepare("SELECT img FROM recipes WHERE id=?");
$getOldImg->bind_param("i", $id);
$getOldImg->execute();
$getOldImg->bind_result($oldImg);
$getOldImg->fetch();
$getOldImg->close();

if ($oldImg && file_exists("../" . $oldImg)) {
    unlink("../" . $oldImg);
}

// Upload ảnh mới
$imageName = time() . '_' . basename($_FILES['img']['name']);
$targetDir = "uploads/";
if (!file_exists($targetDir)) mkdir($targetDir, 0777, true);
$targetFile = $targetDir . $imageName;

if (!move_uploaded_file($_FILES['img']['tmp_name'], $targetFile)) {
    echo json_encode(["success" => false, "message" => "Lỗi upload ảnh"]);
    exit;
}
$imagePath = "recipes/" . $targetFile;

// Cập nhật dữ liệu (KHÔNG có user_id)
$sql = "UPDATE recipes SET title=?, ingredients=?, instructions=?, img=?, time_minutes=? WHERE id=?";
$stmt = $connectNow->prepare($sql);
$stmt->bind_param("ssssii", $title, $ingredients, $instructions, $imagePath, $time_minutes, $id);

if (!$stmt->execute()) {
    echo json_encode(["success" => false, "message" => "Lỗi update: " . $stmt->error]);
} else {
    echo json_encode(["success" => true, "message" => "Cập nhật thành công"]);
}
$connectNow->close();
?>
