<?php
include '../connection.php'; // Đường dẫn tùy thuộc cấu trúc thư mục của bạn

header('Content-Type: application/json');

// Kiểm tra phương thức POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["success" => false, "message" => "Phải sử dụng phương thức POST"]);
    exit;
}

// Kiểm tra các trường bắt buộc
if (!isset($_POST['user_id'], $_POST['product_id'], $_POST['quantity'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin user_id, product_id hoặc quantity"]);
    exit;
}

$user_id = intval($_POST['user_id']);
$product_id = intval($_POST['product_id']);
$quantity = intval($_POST['quantity']);
$note = null; // Mặc định là null

// Kiểm tra xem sản phẩm đã có trong kho chưa
$checkSQL = "SELECT quantity FROM inventory WHERE user_id = ? AND product_id = ?";
$stmt = $connectNow->prepare($checkSQL);
$stmt->bind_param("ii", $user_id, $product_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // Nếu đã có, thì cập nhật số lượng
    $row = $result->fetch_assoc();
    $newQuantity = $row['quantity'] + $quantity;

    $updateSQL = "UPDATE inventory SET quantity = ?, note = ? WHERE user_id = ? AND product_id = ?";
    $stmt = $connectNow->prepare($updateSQL);
    $stmt->bind_param("isii", $newQuantity, $note, $user_id, $product_id);
    $stmt->execute();

    echo json_encode(["success" => true, "message" => "Cập nhật số lượng sản phẩm trong kho thành công"]);
} else {
    // Nếu chưa có, thì thêm mới
    $insertSQL = "INSERT INTO inventory (user_id, product_id, quantity, note) VALUES (?, ?, ?, ?)";
    $stmt = $connectNow->prepare($insertSQL);
    $stmt->bind_param("iiis", $user_id, $product_id, $quantity, $note);
    $stmt->execute();

    echo json_encode(["success" => true, "message" => "Đã thêm sản phẩm mới vào kho"]);
}

$connectNow->close();
?>
