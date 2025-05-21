<?php
include '../connection.php';

header('Content-Type: application/json');

// Kiểm tra kết nối database
if (!isset($connectNow)) {
    die(json_encode(["success" => false, "message" => "Lỗi kết nối database"]));
}

// Kiểm tra phương thức request
if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra dữ liệu đầu vào
if (!isset($_POST['product_id'], $_POST['name'], $_POST['barcode'], $_POST['description'], $_POST['quantity'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin sản phẩm"]);
    exit;
}

$user_id = $_POST['user_id'] ?? '';
$product_id = intval($_POST['product_id']);
$name = $_POST['name'];
$barcode = $_POST['barcode'];
$description = $_POST['description'];
$quantity = intval($_POST['quantity']);
$note = isset($_POST['note']) ? $_POST['note'] : null;

// Lấy thông tin sản phẩm cũ
$getOldProduct = "SELECT img FROM products WHERE id = ?";
$stmt = $connectNow->prepare($getOldProduct);

if (!$stmt) {
    die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
}

$stmt->bind_param("i", $product_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "Sản phẩm không tồn tại"]);
    exit;
}

$oldProduct = $result->fetch_assoc();
$oldImagePath = $oldProduct['img'];
$imagePath = $oldImagePath; // Mặc định giữ nguyên ảnh cũ

// Nếu có ảnh mới, xử lý ảnh
if (isset($_FILES['img'])) {
    // Xóa ảnh cũ
    if (!empty($oldImagePath) && file_exists($oldImagePath)) {
        unlink($oldImagePath);
    }

    // Upload ảnh mới
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

    $imagePath = "products/uploads/" . $imageName;
}

// Cập nhật sản phẩm
$updateProduct = "UPDATE products SET name = ?, barcode = ?, img = ?, description = ? WHERE id = ?";
$stmt = $connectNow->prepare($updateProduct);

if (!$stmt) {
    die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
}

$stmt->bind_param("ssssi", $name, $barcode, $imagePath, $description, $product_id);

if (!$stmt->execute()) {
    die(json_encode(["success" => false, "message" => "Lỗi khi cập nhật sản phẩm: " . $stmt->error]));
}

// Cập nhật số lượng trong kho
$updateInventory = "UPDATE inventory SET quantity = ?, note = ? WHERE product_id = ? AND user_id=?";
$stmt = $connectNow->prepare($updateInventory);

if (!$stmt) {
    die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
}

$stmt->bind_param("isii", $quantity, $note, $product_id,$user_id);

if (!$stmt->execute()) {
    die(json_encode(["success" => false, "message" => "Lỗi khi cập nhật kho: " . $stmt->error]));
}

echo json_encode(["success" => true, "message" => "Cập nhật sản phẩm thành công"]);

$connectNow->close();
?>
