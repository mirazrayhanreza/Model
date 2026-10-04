<?php
declare(strict_types=1);

// backend/api/calls.php
// Audio & Voice WebRTC Calling System API (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';

header('Content-Type: application/json; charset=utf-8');

$db = null;
try {
    $db = Database::getInstance();
} catch (Throwable $e) {
    error_log("Calls API DB Error: " . $e->getMessage());
}

$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

if ($method === 'GET') {
    $action = filter_input(INPUT_GET, 'action', FILTER_DEFAULT) ?? 'list';
    $userId = filter_input(INPUT_GET, 'user_id', FILTER_DEFAULT);

    if ($action === 'webrtc_config') {
        sendJsonResponse('success', 'WebRTC configuration retrieved', [
            'iceServers' => [
                ['urls' => 'stun:stun.l.google.com:19302'],
                ['urls' => 'stun:stun1.l.google.com:19302'],
                ['urls' => 'stun:stun2.l.google.com:19302']
            ],
            'audioCodec' => 'Opus',
            'sampleRate' => 48000,
            'channels' => 2,
            'bitrate' => 64000
        ]);
        exit;
    }

    if ($db) {
        try {
            if ($userId) {
                $stmt = $db->prepare("SELECT * FROM calls WHERE caller_id = ? OR receiver_id = ? ORDER BY id DESC LIMIT 50");
                $stmt->execute([$userId, $userId]);
            } else {
                $stmt = $db->query("SELECT * FROM calls ORDER BY id DESC LIMIT 50");
            }
            $calls = $stmt->fetchAll(PDO::FETCH_ASSOC);
            sendJsonResponse('success', 'Calls retrieved successfully', ['calls' => $calls]);
        } catch (Throwable $e) {
            sendJsonResponse('error', 'Database query failed: ' . $e->getMessage(), ['calls' => []]);
        }
    } else {
        sendJsonResponse('success', 'Fallback calls list', [
            'calls' => [
                ['call_id' => 'CALL-8821', 'caller_name' => 'Rahim Uddin', 'receiver_name' => 'Jessica Chowdhury', 'call_type' => 'Audio Call (WebRTC)', 'duration_text' => '14m 20s', 'quality' => 'HD Opus 48kHz', 'status' => 'Completed', 'created_at' => date('Y-m-d H:i:s')]
            ]
        ]);
    }
}

if ($method === 'POST') {
    $action = sanitizeInput($input['action'] ?? 'initiate');

    if ($action === 'initiate') {
        $callerRole = strtoupper(sanitizeInput($input['caller_role'] ?? 'USER'));
        $receiverRole = strtoupper(sanitizeInput($input['receiver_role'] ?? 'MODEL'));
        $bookingStatus = strtoupper(sanitizeInput($input['booking_status'] ?? 'ACCEPTED'));

        // Policy Rule 1: CALL MODEL TO USER ONLY
        $isModelUserInteraction = ($callerRole === 'MODEL' && ($receiverRole === 'USER' || $receiverRole === 'CLIENT')) || 
                                  (($callerRole === 'USER' || $callerRole === 'CLIENT') && $receiverRole === 'MODEL') ||
                                  ($callerRole === 'ADMIN');
        if (!$isModelUserInteraction) {
            sendJsonResponse('error', 'Audio calls are strictly restricted between Model and User only.', [], 403);
            exit;
        }

        // Policy Rule 2: MODEL ORDER ACCEPT KORAR POR CALL DEWA JABE
        $allowedStatuses = ['ACCEPTED', 'IN_PROGRESS', 'ONGOING', 'CONFIRMED', 'PROOF_UPLOADED', 'USER_CONFIRMED'];
        if ($callerRole !== 'ADMIN' && !in_array($bookingStatus, $allowedStatuses, true)) {
            sendJsonResponse('error', 'Call locked: Model must accept the booking order first.', [
                'code' => 'ORDER_NOT_ACCEPTED',
                'current_status' => $bookingStatus
            ], 403);
            exit;
        }

        $callId = 'CALL-' . date('Ymd') . '-' . rand(1000, 9999);
        $callerId = sanitizeInput($input['caller_id'] ?? 'usr_1012');
        $callerName = sanitizeInput($input['caller_name'] ?? 'Client');
        $receiverId = sanitizeInput($input['receiver_id'] ?? 'partner_1');
        $receiverName = sanitizeInput($input['receiver_name'] ?? 'Jessica Chowdhury');
        $callType = sanitizeInput($input['call_type'] ?? 'Audio Call (WebRTC)');
        $quality = sanitizeInput($input['quality'] ?? 'HD Voice (Opus 48kHz)');

        if ($db) {
            try {
                $stmt = $db->prepare("INSERT INTO calls (call_id, caller_id, caller_name, receiver_id, receiver_name, call_type, duration_seconds, duration_text, quality, status) VALUES (?, ?, ?, ?, ?, ?, 0, '00:00', ?, 'RINGING')");
                $stmt->execute([$callId, $callerId, $callerName, $receiverId, $receiverName, $callType, $quality]);
            } catch (Throwable $e) {
                error_log("Failed to insert call: " . $e->getMessage());
            }
        }

        sendJsonResponse('success', 'Call initiated', [
            'call_id' => $callId,
            'status' => 'RINGING',
            'channel' => 'room_' . md5($callId),
            'webrtc' => [
                'iceServers' => [
                    ['urls' => 'stun:stun.l.google.com:19302'],
                    ['urls' => 'stun:stun1.l.google.com:19302']
                ],
                'audioCodec' => 'Opus 48kHz'
            ]
        ], 201);
    }

    if ($action === 'update_status' || $action === 'end') {
        $callId = sanitizeInput($input['call_id'] ?? '');
        $status = sanitizeInput($input['status'] ?? 'Completed');
        $durationSeconds = (int)($input['duration_seconds'] ?? 0);
        $mins = floor($durationSeconds / 60);
        $secs = $durationSeconds % 60;
        $durationText = sprintf("%02d:%02d", $mins, $secs);

        if ($db && !empty($callId)) {
            try {
                $stmt = $db->prepare("UPDATE calls SET status = ?, duration_seconds = ?, duration_text = ? WHERE call_id = ?");
                $stmt->execute([$status, $durationSeconds, $durationText, $callId]);
            } catch (Throwable $e) {
                error_log("Failed to update call: " . $e->getMessage());
            }
        }

        sendJsonResponse('success', 'Call status updated', [
            'call_id' => $callId,
            'status' => $status,
            'duration' => $durationText
        ]);
    }

    sendJsonResponse('error', 'Invalid action', [], 400);
}
