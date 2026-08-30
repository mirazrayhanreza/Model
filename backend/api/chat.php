<?php
declare(strict_types=1);

// backend/api/chat.php
// REST API Endpoint - Live Messaging & B2B Order Chat (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';

$db = Database::getInstance();
$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

if ($method === 'GET') {
    $orderId = filter_input(INPUT_GET, 'order_id', FILTER_DEFAULT);
    $senderId = filter_input(INPUT_GET, 'sender_id', FILTER_DEFAULT);
    $receiverId = filter_input(INPUT_GET, 'receiver_id', FILTER_DEFAULT);

    if ($orderId) {
        $stmt = $db->prepare("SELECT * FROM b2b_chat_messages WHERE order_id = ? ORDER BY timestamp ASC");
        $stmt->execute([$orderId]);
        sendJsonResponse('success', 'Order chat loaded', ['messages' => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
    }

    if ($senderId && $receiverId) {
        $stmt = $db->prepare("SELECT * FROM chat_messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp ASC");
        $stmt->execute([$senderId, $receiverId, $receiverId, $senderId]);
        sendJsonResponse('success', 'Direct chat loaded', ['messages' => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
    }

    sendJsonResponse('error', 'Missing chat filters', [], 400);
}

if ($method === 'POST') {
    $orderId = sanitizeInput($input['order_id'] ?? '');

    if (!empty($orderId)) {
        $id = 'MSG_' . (int)(microtime(true) * 1000) . '_' . rand(100, 999);
        $senderId = sanitizeInput($input['sender_id'] ?? '');
        $senderName = sanitizeInput($input['sender_name'] ?? '');
        $senderRole = sanitizeInput($input['sender_role'] ?? 'USER');
        $content = sanitizeInput($input['content'] ?? '');
        $imageUrl = sanitizeInput($input['image_url'] ?? null);

        $stmt = $db->prepare("INSERT INTO b2b_chat_messages (id, order_id, sender_id, sender_name, sender_role, content, image_url, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        $now = (int)(microtime(true) * 1000);
        $stmt->execute([$id, $orderId, $senderId, $senderName, $senderRole, $content, $imageUrl, $now]);

        sendJsonResponse('success', 'Order message sent', ['message_id' => $id], 201);
    }

    // Direct User-to-Model Chat
    $senderId = sanitizeInput($input['sender_id'] ?? '');
    $receiverId = sanitizeInput($input['receiver_id'] ?? '');
    $senderName = sanitizeInput($input['sender_name'] ?? 'User');
    $content = sanitizeInput($input['content'] ?? '');

    $stmt = $db->prepare("INSERT INTO chat_messages (sender_id, receiver_id, sender_name, content, timestamp, is_read) VALUES (?, ?, ?, ?, ?, 0)");
    $stmt->execute([$senderId, $receiverId, $senderName, $content, (int)(microtime(true) * 1000)]);

    sendJsonResponse('success', 'Message sent', ['message_id' => (int)$db->lastInsertId()], 201);
}
