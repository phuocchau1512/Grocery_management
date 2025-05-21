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


$sql = "UPDATE recipes SET title=?, ingredients=?, instructions=?, time_minutes=? WHERE id=?";
$stmt = $connectNow->prepare($sql);
$stmt->bind_param("sssii", $title, $ingredients, $instructions, $time_minutes, $id);

if (!$stmt->execute()) {
    echo json_encode(["success" => false, "message" => "Lỗi update: " . $stmt->error]);
} else {
    echo json_encode(["success" => true, "message" => "Cập nhật thành công"]);
}
$connectNow->close();
?>
