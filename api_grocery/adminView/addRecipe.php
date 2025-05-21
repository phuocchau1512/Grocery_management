<?php
include_once "../connection.php";

// Lấy dữ liệu từ form
$title = $_POST['title'];
$ingredients = $_POST['ingredients'];
$instructions = $_POST['instructions'];
$time_minutes = $_POST['time_minutes'];
$user_id = 0; // Giả sử người dùng là admin

// Đường dẫn thư mục lưu ảnh (tuyệt đối trên server)
$uploadDir = __DIR__ . "/../recipes/uploads/";
$imagePath = ""; // Đường dẫn ảnh sẽ lưu trong CSDL (dùng tương đối)

// Tạo thư mục nếu chưa tồn tại
if (!file_exists($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

// Xử lý ảnh nếu có upload
if (isset($_FILES['r_img']) && $_FILES['r_img']['error'] == 0) {
    $newImage = $_FILES['r_img'];

    // Lấy phần mở rộng và tạo tên file mới
    $imageExtension = pathinfo($newImage['name'], PATHINFO_EXTENSION);
    $imageName = uniqid('recipes_', true) . '.' . $imageExtension;

    // Gán đường dẫn lưu trong CSDL (tương đối)
    $imagePath = "recipes/uploads/" . $imageName;

    // Di chuyển file ảnh vào đúng thư mục
    move_uploaded_file($newImage['tmp_name'], $uploadDir . $imageName);
}

// Thêm vào CSDL
$sql = "INSERT INTO recipes (title, ingredients, instructions, img, time_minutes, user_id)
        VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $connectNow->prepare($sql);
$stmt->bind_param("ssssii", $title, $ingredients, $instructions, $imagePath, $time_minutes, $user_id);

if ($stmt->execute()) {
    echo "Thêm công thức thành công!";
} else {
    echo "Lỗi: " . $stmt->error;
}
?>
