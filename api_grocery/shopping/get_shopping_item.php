<?php
include '../connection.php';
header('Content-Type: application/json; charset=UTF-8');

// Kiểm tra kết nối
if (!$connectNow) {
    die(json_encode(["success" => false, "message" => "Lỗi kết nối database"]));
}

// Kiểm tra phương thức
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

// Kiểm tra tham số shopping_list_id
if (!isset($_GET['shopping_list_id']) || empty($_GET['shopping_list_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu shopping_list_id"]);
    exit;
}

$shoppingListId = intval($_GET['shopping_list_id']);

// Chuẩn bị truy vấn SQL JOIN
$query = "
    SELECT 
        sli.id,
        sli.shopping_list_id,
        sli.product_id,
        sli.quantity,
        sli.is_check,
        p.name AS product_name,
        p.barcode,
        p.img
    FROM shopping_list_item sli
    JOIN products p ON sli.product_id = p.id
    WHERE sli.shopping_list_id = ?
";

$stmt = $connectNow->prepare($query);
if (!$stmt) {
    echo json_encode(["success" => false, "message" => "Lỗi chuẩn bị truy vấn: " . $connectNow->error]);
    exit;
}

// Bind và thực thi truy vấn
$stmt->bind_param("i", $shoppingListId);
$stmt->execute();
$result = $stmt->get_result();

// Xử lý kết quả
$items = [];
while ($row = $result->fetch_assoc()) {
    $items[] = [
        "id" => (int)$row['id'],
        "shoppingListId" => (int)$row['shopping_list_id'],
        "productId" => (int)$row['product_id'],
        "quantity" => (int)$row['quantity'],
        "isChecked" => (int)$row['is_check'],
        "productName" => $row['product_name'],
        "barcode" => $row['barcode'],
        "img" => $row['img']
    ];
}

// Luôn trả về success = true và mảng items (có thể rỗng)
echo json_encode([
    "success" => true,
    "items" => $items
], JSON_UNESCAPED_UNICODE);

$connectNow->close();
?>
