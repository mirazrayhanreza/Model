<?php
declare(strict_types=1);

// backend/api/auth/register.php
// Production Mobile App Direct Registration API (PHP 8.2+)

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit(0);
}

require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse('error', 'Only POST method is allowed for registration.', [], 405);
}

$rawInput = file_get_contents('php://input');
$body = json_decode($rawInput, true) ?: $_POST;

$name = trim((string)($body['name'] ?? 'User'));
$email = trim((string)($body['email'] ?? ''));
$password = (string)($body['password'] ?? '');
$phone = trim((string)($body['phone'] ?? ''));
$role = strtoupper(trim((string)($body['role'] ?? 'USER')));
$country = trim((string)($body['country'] ?? 'Bangladesh'));
$city = trim((string)($body['city'] ?? 'Dhaka'));
$uid = trim((string)($body['uid'] ?? ''));

if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendJsonResponse('error', 'A valid email address is required.', [], 400);
}

if (strlen($password) < 6 && empty($uid)) {
    sendJsonResponse('error', 'Password must be at least 6 characters long.', [], 400);
}

$dbPhone = !empty($phone) ? $phone : null;
$hashedPassword = !empty($password) ? password_hash($password, PASSWORD_DEFAULT) : '';

try {
    $db = Database::getInstance();

    // Check if email already registered
    $checkStmt = $db->prepare("SELECT id, uid, name, email, phone, role FROM users WHERE email = ? LIMIT 1");
    $checkStmt->execute([$email]);
    $existing = $checkStmt->fetch(PDO::FETCH_ASSOC);

    if ($existing) {
        // User already exists, update record
        $updateStmt = $db->prepare("
            UPDATE users 
            SET name = COALESCE(NULLIF(?, ''), name),
                phone = COALESCE(?, phone),
                country = COALESCE(NULLIF(?, ''), country),
                city = COALESCE(NULLIF(?, ''), city)
            WHERE id = ?
        ");
        $updateStmt->execute([$name, $dbPhone, $country, $city, $existing['id']]);

        sendJsonResponse('success', 'User profile updated in backend successfully.', [
            'id' => (string)$existing['id'],
            'uid' => $existing['uid'] ?: $uid,
            'name' => $name,
            'email' => $email,
            'phone' => $phone ?: ($existing['phone'] ?? ''),
            'role' => $existing['role'],
            'wallet_balance' => 0.00
        ]);
        exit(0);
    }

    // Insert new user
    $finalUid = !empty($uid) ? $uid : ('user_' . time() . '_' . rand(100, 999));
    $avatarUrl = ($role === 'MODEL') 
        ? 'https://images.unsplash.com/photo-1534528741775-53994a69daeb' 
        : 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d';

    $insertStmt = $db->prepare("
        INSERT INTO users (uid, name, email, phone, password, role, avatar_url, wallet_balance, status, is_verified, country, city)
        VALUES (?, ?, ?, ?, ?, ?, ?, 0.00, 'ACTIVE', 1, ?, ?)
    ");
    $insertStmt->execute([
        $finalUid,
        $name,
        $email,
        $dbPhone,
        $hashedPassword,
        $role,
        $avatarUrl,
        $country,
        $city
    ]);

    $newId = (int)$db->lastInsertId();

    // If model, add to models table as well
    if ($role === 'MODEL') {
        try {
            $modelStmt = $db->prepare("
                INSERT INTO models (uid, user_id, name, hourly_rate, daily_rate, category, location, rating, avatar_url, status)
                VALUES (?, ?, ?, 1500.00, 8000.00, 'Fashion', ?, 5.00, ?, 'AVAILABLE')
            ");
            $modelStmt->execute([$finalUid, $newId, $name, $city, $avatarUrl]);
        } catch (Throwable $me) {
            // Ignore if duplicate
        }
    }

    sendJsonResponse('success', 'User registered in backend MySQL successfully.', [
        'id' => (string)$newId,
        'uid' => $finalUid,
        'name' => $name,
        'email' => $email,
        'phone' => $phone,
        'role' => $role,
        'wallet_balance' => 0.00,
        'avatar_url' => $avatarUrl
    ], 201);

} catch (Throwable $e) {
    sendJsonResponse('error', 'Backend Registration failed: ' . $e->getMessage(), [
        'email' => $email,
        'name' => $name
    ], 500);
}
