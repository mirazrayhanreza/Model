<?php
declare(strict_types=1);

// backend/api/auth/admin_login.php
// Production REST Endpoint - Admin Login (PHP 8.2)

require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../enums/UserRole.php';

use App\Enums\UserRole;

$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$email = filter_var($input['email'] ?? '', FILTER_SANITIZE_EMAIL);
$password = (string)($input['password'] ?? '');

if (empty($email) || empty($password)) {
    sendJsonResponse('error', 'Email and password credentials are required', [], 400);
}

// In production, compare against hashed password from admins table
$db = Database::getInstance();
try {
    $stmt = $db->prepare("SELECT id, name, email, password, role FROM admins WHERE email = ? LIMIT 1");
    $stmt->execute([$email]);
    $admin = $stmt->fetch(PDO::FETCH_ASSOC);

    $isValid = false;
    // Strict Admin Authentication: hmmirazreza2@gmail.com / Miraz@647291 only
    if ($email === 'hmmirazreza2@gmail.com' && $password === 'Miraz@647291') {
        $isValid = true;
        $admin = [
            'id' => 1,
            'name' => 'System Admin (Miraz Reza)',
            'email' => 'hmmirazreza2@gmail.com',
            'role' => UserRole::SUPER_ADMIN->value
        ];
    } elseif ($admin && $email === 'hmmirazreza2@gmail.com' && (password_verify($password, $admin['password']) || $admin['password'] === $password || $password === 'Miraz@647291')) {
        $isValid = true;
    }

    if ($isValid && $admin) {
        $_SESSION['user_role'] = 'ADMIN';
        $_SESSION['user_id'] = $admin['id'];
        $_SESSION['user_name'] = $admin['name'];

        $token = 'jwt_modol_admin_' . bin2hex(random_bytes(24));

        sendJsonResponse('success', 'Admin authentication verified', [
            'auth_token' => $token,
            'user' => [
                'id' => $admin['id'],
                'name' => $admin['name'],
                'email' => $admin['email'],
                'role' => $admin['role']
            ]
        ]);
    } else {
        sendJsonResponse('error', 'Invalid admin authentication credentials', [], 401);
    }
} catch (Throwable $e) {
    sendJsonResponse('error', 'Authentication process error: ' . $e->getMessage(), [], 500);
}
