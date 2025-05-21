<?php
include '../connection.php';

// Đọc dữ liệu JSON từ request
$inputJSON = file_get_contents("php://input");
$data = json_decode($inputJSON, true); // Chuyển JSON thành mảng PHP

// Kiểm tra dữ liệu hợp lệ
if (!isset($data['name'], $data['email'], $data['password'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin đăng ký"]);
    exit;
}

$userName = $data['name'];
$userEmail = $data['email'];
$userPass = $data['password'];

// Kiểm tra xem email đã tồn tại chưa
$query = "SELECT * FROM users WHERE email = ?";
$stmt = $connectNow->prepare($query);
$stmt->bind_param("s", $userEmail);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "Email đã được sử dụng để đăng ký"]);
    exit;
}

// Hash mật khẩu trước khi lưu
$hashedPassword = password_hash($userPass, PASSWORD_DEFAULT);

// Tạo câu lệnh SQL dùng prepare
$sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
$stmt = $connectNow->prepare($sql);
$stmt->bind_param("sss", $userName, $userEmail, $hashedPassword);

// Thực thi
if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Đăng ký thành công"]);
} else {
    echo json_encode(["success" => false, "message" => "Lỗi khi đăng ký, vui lòng thử lại"]);
}

// Đóng statement và kết nối
$stmt->close();
$connectNow->close();
?>
