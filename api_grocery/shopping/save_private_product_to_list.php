<?php
include '../connection.php';

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

if (!isset($_POST['shopping_list_id'], $_POST['name'], $_POST['barcode'], $_POST['description'], $_POST['quantity'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin"]);
    exit;
}

$list_id = $_POST['shopping_list_id'];
$name = $_POST['name'];
$barcode = $_POST['barcode'];
$description = $_POST['description'];
$quantity = intval($_POST['quantity']);
$is_check = 0;

// Upload ảnh
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
$imagePath = "shopping/uploads/" . $imageName;

// Kiểm tra nếu sản phẩm đã tồn tại với barcode
$checkProduct = "SELECT id FROM products WHERE barcode = ?";
$stmt = $connectNow->prepare($checkProduct);
$stmt->bind_param("s", $barcode);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows > 0) {
    $productRow = $res->fetch_assoc();
    $product_id = $productRow['id'];
} else {
    $insertProduct = "INSERT INTO products (name, barcode, img, description, is_private) VALUES (?, ?, ?, ?, 1)";
    $stmt = $connectNow->prepare($insertProduct);
    $stmt->bind_param("ssss", $name, $barcode, $imagePath, $description);
    if (!$stmt->execute()) {
        echo json_encode(["success" => false, "message" => "Lỗi khi thêm sản phẩm"]);
        exit;
    }
    $product_id = $connectNow->insert_id;
}

// Kiểm tra trùng trong danh sách
$checkDuplicate = "SELECT id FROM shopping_list_item WHERE shopping_list_id = ? AND product_id = ?";
$stmt = $connectNow->prepare($checkDuplicate);
$stmt->bind_param("ii", $list_id, $product_id);
$stmt->execute();
$dupResult = $stmt->get_result();

if ($dupResult->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "Sản phẩm đã tồn tại trong danh sách"]);
    exit;
}

// Thêm vào bảng shopping_list_item
$insertItem = "INSERT INTO shopping_list_item (shopping_list_id, product_id, quantity, is_check) VALUES (?, ?, ?, ?)";
$stmt = $connectNow->prepare($insertItem);
$stmt->bind_param("iiii", $list_id, $product_id, $quantity, $is_check);
if (!$stmt->execute()) {
    echo json_encode(["success" => false, "message" => "Lỗi khi thêm vào danh sách"]);
    exit;
}

// Cập nhật item_count trong bảng shopping_list
$update_sql = "UPDATE shopping_list SET item_count = item_count + 1 WHERE id = ?";
$update_stmt = $connectNow->prepare($update_sql);
if ($update_stmt) {
    $update_stmt->bind_param("i", $list_id);
    $update_stmt->execute();
}

echo json_encode(["success" => true, "message" => "Đã thêm sản phẩm vào danh sách"]);
?>
