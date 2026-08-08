<?php
// backend/api/auth/admin_login.php
// REST API Endpoint - Admin Login
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../config/config.php';

header('Content-Type: application/json');
$input = json_decode(file_get_contents('php://input'), true);

$email = filter_var($input['email'] ?? '', FILTER_SANITIZE_EMAIL);
$password = $input['password'] ?? '';

if (empty($email) || empty($password)) {
    sendJsonResponse('error', 'Email and password are required', [], 400);
}

// Check Admin credentials
if ($email === 'admin@modolconnect.com' && $password === 'admin123') {
    $_SESSION['user_role'] = 'ADMIN';
    $_SESSION['user_id'] = 1;
    $_SESSION['user_name'] = 'System Admin';

    sendJsonResponse('success', 'Admin login successful', [
        'auth_token' => 'jwt_modol_admin_' . bin2hex(random_bytes(16)),
        'user' => [
            'id' => 1,
            'name' => 'System Admin',
            'email' => 'admin@modolconnect.com',
            'role' => 'SUPER_ADMIN'
        ]
    ]);
} else {
    sendJsonResponse('error', 'Invalid admin credentials', [], 401);
}
?>
