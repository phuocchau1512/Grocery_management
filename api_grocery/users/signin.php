<?php
include '../connection.php';


// Đọc dữ liệu JSON từ request
$inputJSON = file_get_contents("php://input");
$data = json_decode($inputJSON, true); // Chuyển JSON thành mảng PHP

// Kiểm tra dữ liệu hợp lệ
if (!isset( $data['email'], $data['password'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin đăng ký"]);
    exit;
}


$userEmail = $data['email'];
$userPass = $data['password'];

$sql = "SELECT id, name, email, password FROM users WHERE email = ?";
$stmt = $connectNow->prepare($sql);
$stmt->bind_param("s", $userEmail);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $userData = $result->fetch_assoc();

    if (password_verify($userPass, $userData['password'])) { // Sửa lại thành 'password'

        echo json_encode(array(
            "success" => true,
            "userID" => $userData['id'],
            "userName" => $userData['name'],
            "email" => $userData['email'],
            "message" => "Đăng nhập thành công"
        ));
    } else {
        echo json_encode(array(
            "success" => false,
            "message" => "Sai tài khoản hoặc mật khẩu"
        ));
    }
} else {
    echo json_encode(array(
        "success" => false,
        "message" => "Sai tài khoản hoặc mật khẩu"
    ));
}

$stmt->close();
$connectNow->close();

?>
