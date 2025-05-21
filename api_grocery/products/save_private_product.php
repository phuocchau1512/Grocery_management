<?php
include '../connection.php';

header('Content-Type: application/json');

if (!isset($connectNow)) {
    die(json_encode(["success" => false, "message" => "Lỗi kết nối database"]));
}

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

if (!isset($_POST['user_id'], $_POST['name'], $_POST['barcode'], $_POST['description'], $_POST['quantity'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin sản phẩm"]);
    exit;
}

$user_id = $_POST['user_id'];
$name = $_POST['name'];
$barcode = $_POST['barcode'];
$description = $_POST['description'];
$quantity = intval($_POST['quantity']);
$note = isset($_POST['note']) && $_POST['note'] !== '' ? $_POST['note'] : null;

// Kiểm tra barcode đã tồn tại trong bảng sản phẩm và xem is_private
$checkProduct = "SELECT id, is_private FROM products WHERE barcode = ?";
$stmt = $connectNow->prepare($checkProduct);

if (!$stmt) {
    die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
}

$stmt->bind_param("s", $barcode);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        if ($row['is_private'] == 0) {
            echo json_encode(["success" => false, "message" => "Barcode bị trùng với sản phẩm đã tồn tại"]);
            exit;
        }
    }
}

// Kiểm tra barcode đã tồn tại trong kho của người dùng hiện tại
$checkInventory = "SELECT id FROM inventory WHERE user_id = ? AND product_id = (SELECT id FROM products WHERE barcode = ?)";
$stmt = $connectNow->prepare($checkInventory);

if (!$stmt) {
    die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
}

$stmt->bind_param("is", $user_id, $barcode);
$stmt->execute();
$resultInv = $stmt->get_result();

if ($resultInv->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "Barcode bị trùng với sản phẩm trong kho của bạn"]);
    exit;
}

// Nếu chưa có sản phẩm, tiếp tục thêm
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

$imagePath = "products/uploads/" . $imageName;

// Thêm sản phẩm mới, gán is_private = 1
$sql = "INSERT INTO products (name, barcode, img, description, is_private) VALUES (?, ?, ?, ?, 1)";
$stmt = $connectNow->prepare($sql);

if (!$stmt) {
    die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
}

$stmt->bind_param("ssss", $name, $barcode, $imagePath, $description);
if (!$stmt->execute()) {
    die(json_encode(["success" => false, "message" => "Lỗi khi thêm sản phẩm: " . $stmt->error]));
}

$product_id = $connectNow->insert_id;

// Kiểm tra sản phẩm trong kho
$checkInventory = "SELECT id, quantity FROM inventory WHERE user_id = ? AND product_id = ?";
$stmt = $connectNow->prepare($checkInventory);

if (!$stmt) {
    die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
}

$stmt->bind_param("ii", $user_id, $product_id);
$stmt->execute();
$resultInv = $stmt->get_result();

if ($resultInv->num_rows > 0) {
    $row = $resultInv->fetch_assoc();
    $newQuantity = $row['quantity'] + $quantity;

    $updateSQL = "UPDATE inventory SET quantity = ?, note = ? WHERE user_id = ? AND product_id = ?";
    $stmt = $connectNow->prepare($updateSQL);

    if (!$stmt) {
        die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
    }

    $stmt->bind_param("isii", $newQuantity, $note, $user_id, $product_id);
    if (!$stmt->execute()) {
        die(json_encode(["success" => false, "message" => "Lỗi khi cập nhật số lượng: " . $stmt->error]));
    }
} else {
    $insertSQL = "INSERT INTO inventory (user_id, product_id, quantity, note) VALUES (?, ?, ?, ?)";
    $stmt = $connectNow->prepare($insertSQL);

    if (!$stmt) {
        die(json_encode(["success" => false, "message" => "Lỗi prepare: " . $connectNow->error]));
    }

    $stmt->bind_param("iiis", $user_id, $product_id, $quantity, $note);
    if (!$stmt->execute()) {
        die(json_encode(["success" => false, "message" => "Lỗi khi thêm vào kho: " . $stmt->error]));
    }
}

echo json_encode(["success" => true, "message" => "Cập nhật thành công"]);

$connectNow->close();
?>
