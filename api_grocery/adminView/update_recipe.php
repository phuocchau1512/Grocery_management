<?php
include_once "../connection.php";

// Kiểm tra nếu người dùng đã nhấn nút "Update Recipe"
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Lấy dữ liệu từ form
    $recipe_id = intval($_POST['recipe_id']);
    $recipe_title = mysqli_real_escape_string($connectNow, $_POST['r_title']);
    $recipe_ingredients = mysqli_real_escape_string($connectNow, $_POST['r_ingredients']);
    $recipe_instructions = mysqli_real_escape_string($connectNow, $_POST['r_instructions']);
    $likes = intval($_POST['r_likes']);
    $time_minutes = intval($_POST['r_time']);
    $existingImage = $_POST['existingImage'];  // Đường dẫn ảnh cũ

    // Kiểm tra nếu có ảnh mới được tải lên
    if (isset($_FILES['newImage']) && $_FILES['newImage']['error'] == 0) {
        $newImage = $_FILES['newImage'];

        // Tạo tên ảnh duy nhất
        $imageExtension = pathinfo($newImage['name'], PATHINFO_EXTENSION);
        $imageName = uniqid('recipe_', true) . '.' . $imageExtension;

        // Đường dẫn lưu ảnh vào thư mục uploads
        $imagePath = "recipes/uploads/" . $imageName;

        // Xóa ảnh cũ nếu tồn tại
        if ($existingImage && file_exists("../" . $existingImage)) {
            unlink("../" . $existingImage);
        }

        // Di chuyển ảnh mới vào thư mục
        move_uploaded_file($newImage['tmp_name'], "../" . $imagePath);
    } else {
        // Không có ảnh mới, giữ nguyên ảnh cũ
        $imagePath = $existingImage;
    }

    // Cập nhật công thức trong cơ sở dữ liệu
    $update_query = "UPDATE recipes SET 
                        title = '$recipe_title', 
                        ingredients = '$recipe_ingredients', 
                        instructions = '$recipe_instructions', 
                        img = '$imagePath', 
                        likes = '$likes',
                        time_minutes = '$time_minutes'
                    WHERE id = '$recipe_id'";

    if (mysqli_query($connectNow, $update_query)) {
        echo 'Cập nhật công thức thành công!';
    } else {
        echo "Cập nhật công thức thất bại: " . mysqli_error($connectNow);
    }
}
?>
