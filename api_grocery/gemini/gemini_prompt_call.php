<?php
function callGenAPI($prompt) {
    $apiKey = 'AIzaSyDoRhVuHtrIgHaFFcYAYx2DO8IQ0AZXCj8'; // ← Thay bằng API Key thật của bạn
    $url = 'https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=' . $apiKey;

    $data = [
        'contents' => [
            [
                'parts' => [
                    ['text' => $prompt]
                ]
            ]
        ]
    ];

    $options = [
        'http' => [
            'header'  => "Content-Type: application/json\r\n",
            'method'  => 'POST',
            'content' => json_encode($data),
            'timeout' => 1000
        ]
    ];

    $context = stream_context_create($options);
    $result = @file_get_contents($url, false, $context); // @ để tránh warning nếu lỗi

    $genResponse = [
        'responseStatus' => 0,
        'responseText' => '',
    ];

    if ($result === false) {
        $genResponse['responseStatus'] = -1;
        $genResponse['responseText'] = 'Không thể kết nối đến Gemini API.';
    } else {
        $response = json_decode($result, true);
        if (isset($response['candidates'][0]['content']['parts'][0]['text'])) {
            $genResponse['responseText'] = $response['candidates'][0]['content']['parts'][0]['text'];
        } else {
            $genResponse['responseStatus'] = 1;
            $genResponse['responseText'] = 'Không nhận được phản hồi hợp lệ từ Gemini.';
        }
    }

    return $genResponse;
}
?>
