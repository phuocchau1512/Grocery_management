<?php
include 'connection.php';
session_start();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = trim($_POST['email']);
    $password = $_POST['password'];

    $stmt = $connectNow->prepare("SELECT id, name, password FROM users WHERE email = ? AND is_admin = 1 LIMIT 1");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result && $row = $result->fetch_assoc()) {
        if (password_verify($password, $row['password'])) {
            // Đăng nhập thành công
            $_SESSION['admin_id'] = $row['id'];
            $_SESSION['admin_name'] = $row['name'];
            header("Location: admin_dashboard.php"); // điều hướng sau khi đăng nhập thành công
            exit;
        } else {
            $error = "Mật khẩu không đúng.";
        }
    } else {
        $error = "Không tìm thấy tài khoản admin.";
    }
}
?>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập Admin</title>
    <link rel="stylesheet" href="assets/css/dangnhap.css">
</head>
<body>
    <div class="login-container">
        <h2>Đăng nhập Admin</h2>
        <?php if (isset($error)) echo '<p class="error">' . $error . '</p>'; ?>
        <form method="POST" id="adminLoginForm">
            <input type="email" name="email" placeholder="Email" required><br>
            <input type="password" name="password" placeholder="Mật khẩu" required><br>
            <button type="submit">Đăng nhập</button>
        </form>
    </div>

    <script src="assets/js/dangnhapadmin.js"></script>
</body>
</html>
