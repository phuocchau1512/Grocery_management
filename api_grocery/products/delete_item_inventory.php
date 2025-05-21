<?php
include '../connection.php';

header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

$user_id = $_POST['user_id'] ?? '';
$product_id = $_POST['product_id'] ?? '';

if (empty($user_id) || empty($product_id)) {
    echo json_encode(["success" => false, "message" => "Thiếu user_id hoặc product_id"]);
    exit;
}

// Xóa khỏi inventory
$query = "DELETE FROM inventory WHERE user_id = ? AND product_id = ?";
$stmt = $connectNow->prepare($query);
$stmt->bind_param("ii", $user_id, $product_id);

if (!$stmt->execute()) {
    echo json_encode(["success" => false, "message" => "Lỗi khi xóa khỏi kho"]);
    $connectNow->close();
    exit;
}

// Kiểm tra is_private trong bảng products
$checkQuery = "SELECT img, is_private FROM products WHERE id = ?";
$stmt = $connectNow->prepare($checkQuery);
$stmt->bind_param("i", $product_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();

    if ($row['is_private'] == 1) {
        // Xóa ảnh nếu có
        if (!empty($row['img'])) {
            $imagePath = $row['img']; // ví dụ: "products/uploads/abc.jpg"
            $filePath = __DIR__ . '/' . str_replace("products/", "", $imagePath); // ra "uploads/abc.jpg"
        
            if (file_exists($filePath)) {
                unlink($filePath);
            }
        }


        // Xóa khỏi bảng products
        $deleteProduct = "DELETE FROM products WHERE id = ?";
        $stmt = $connectNow->prepare($deleteProduct);
        $stmt->bind_param("i", $product_id);
        $stmt->execute();
    }
}

echo json_encode(["success" => true, "message" => "Xóa thành công"]);

$connectNow->close();
?>
