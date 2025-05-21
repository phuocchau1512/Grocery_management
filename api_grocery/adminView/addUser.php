<?php
include_once "../connection.php";


// Kiểm tra nếu người dùng đã nhấn nút "Add User"
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $name = mysqli_real_escape_string($connectNow, $_POST['name']);
    $email = mysqli_real_escape_string($connectNow, $_POST['email']);
    $password = mysqli_real_escape_string($connectNow, $_POST['password']);
    $role = $_POST['role'] === 'admin' ? 1 : 0;

    // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
    $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

    // Câu lệnh SQL để thêm người dùng mới vào cơ sở dữ liệu
    $insert_query = "INSERT INTO users (name, email, password, is_admin) 
                     VALUES ('$name', '$email', '$hashedPassword', '$role')";

    if (mysqli_query($connectNow, $insert_query)) {
        echo "Thêm người dùng thành công!";
    } else {
        echo "Thêm người dùng thất bại: " . mysqli_error($connectNow);
    }

}


?>
