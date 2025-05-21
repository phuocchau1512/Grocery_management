<?php
include './gemini_prompt_call.php';

// Lấy dữ liệu JSON từ POST body
$inputJSON = file_get_contents("php://input");
$data = json_decode($inputJSON, true);

// Kiểm tra dữ liệu
if (!isset($data['prompt'])) {
    echo json_encode([
        'responseStatus' => 1,
        'responseText' => 'Thiếu prompt trong request.'
    ]);
    exit;
}

$prompt = $data['prompt'];

// Gọi API Gemini
$response = callGenAPI($prompt);

// Trả về kết quả JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
