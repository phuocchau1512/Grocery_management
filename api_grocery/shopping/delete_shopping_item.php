<?php
include '../connection.php';
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ"]);
    exit;
}

if (!isset($_POST['id'], $_POST['product_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu thông tin item_id hoặc product_id"]);
    exit;
}

$item_id = intval($_POST['id']);
$product_id = intval($_POST['product_id']);

$connectNow->begin_transaction();

try {
    // Lấy shopping_list_id từ item_id
    $getListId = "SELECT shopping_list_id FROM shopping_list_item WHERE id = ?";
    $stmt = $connectNow->prepare($getListId);
    if (!$stmt) {
        throw new Exception("Lỗi khi chuẩn bị truy vấn lấy shopping_list_id");
    }
    $stmt->bind_param("i", $item_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    if (!$row) {
        throw new Exception("Không tìm thấy item tương ứng");
    }

    $shopping_list_id = $row['shopping_list_id'];

    // Xóa item khỏi shopping_list_item
    $deleteItem = "DELETE FROM shopping_list_item WHERE id = ? AND product_id = ?";
    $stmt = $connectNow->prepare($deleteItem);
    if (!$stmt) {
        throw new Exception("Lỗi khi chuẩn bị truy vấn xóa item");
    }
    $stmt->bind_param("ii", $item_id, $product_id);
    if (!$stmt->execute()) {
        throw new Exception("Lỗi khi xóa item khỏi danh sách");
    }

    // Trừ item_count trong bảng shopping_list
    $updateCount = "UPDATE shopping_list SET item_count = item_count - 1 WHERE id = ?";
    $stmt = $connectNow->prepare($updateCount);
    if (!$stmt) {
        throw new Exception("Lỗi khi chuẩn bị truy vấn cập nhật item_count");
    }
    $stmt->bind_param("i", $shopping_list_id);
    $stmt->execute();

    // Kiểm tra nếu sản phẩm là riêng tư
    $checkPrivate = "SELECT is_private FROM products WHERE id = ?";
    $stmt = $connectNow->prepare($checkPrivate);
    if (!$stmt) {
        throw new Exception("Lỗi khi chuẩn bị truy vấn kiểm tra sản phẩm");
    }
    $stmt->bind_param("i", $product_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    if ($row && intval($row['is_private']) === 1) {
        $deleteProduct = "DELETE FROM products WHERE id = ?";
        $stmt = $connectNow->prepare($deleteProduct);
        if (!$stmt) {
            throw new Exception("Lỗi khi chuẩn bị truy vấn xóa sản phẩm");
        }
        $stmt->bind_param("i", $product_id);
        if (!$stmt->execute()) {
            throw new Exception("Lỗi khi xóa sản phẩm riêng tư");
        }
    }

    $connectNow->commit();
    echo json_encode(["success" => true, "message" => "Xóa item thành công"]);

} catch (Exception $e) {
    $connectNow->rollback();
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}

$connectNow->close();
?>
