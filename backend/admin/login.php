<?php
declare(strict_types=1);

// backend/admin/login.php
// Production Admin Web Login Interface & API (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';

$error = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = filter_var($_POST['email'] ?? '', FILTER_SANITIZE_EMAIL);
    $password = (string)($_POST['password'] ?? '');

    if (empty($email) || empty($password)) {
        $error = 'Email and password are required.';
    } else {
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
                    'role' => 'SUPER_ADMIN'
                ];
            } elseif ($admin && $email === 'hmmirazreza2@gmail.com' && (password_verify($password, $admin['password']) || $admin['password'] === $password || $password === 'Miraz@647291')) {
                $isValid = true;
            }

            if ($isValid && $admin) {
                $_SESSION['user_role'] = 'ADMIN';
                $_SESSION['user_id'] = $admin['id'];
                $_SESSION['user_name'] = $admin['name'];
                header('Location: dashboard.php');
                exit(0);
            } else {
                $error = 'Invalid admin email or password.';
            }
        } catch (Throwable $e) {
            $error = 'Database error: ' . $e->getMessage();
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Portal Login - Modol Connect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #0d1117;
            color: #c9d1d9;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }
        .card {
            background-color: #161b22;
            border: 1px solid #30363d;
            border-radius: 12px;
            width: 100%;
            max-width: 420px;
            box-shadow: 0 16px 32px rgba(0,0,0,0.4);
        }
        .form-control {
            background-color: #0d1117;
            border: 1px solid #30363d;
            color: #f0f6fc;
        }
        .form-control:focus {
            background-color: #0d1117;
            border-color: #58a6ff;
            color: #f0f6fc;
            box-shadow: 0 0 0 0.25rem rgba(88,166,255,0.25);
        }
        .btn-primary {
            background-color: #238636;
            border-color: #238636;
        }
        .btn-primary:hover {
            background-color: #2ea043;
            border-color: #2ea043;
        }
    </style>
</head>
<body>
    <div class="card p-4">
        <div class="text-center mb-4">
            <h4 class="fw-bold text-white mb-1">Modol Connect</h4>
            <span class="badge bg-danger text-uppercase px-3 py-1">Administrator Portal</span>
        </div>

        <?php if ($error): ?>
            <div class="alert alert-danger py-2" role="alert"><?= htmlspecialchars($error) ?></div>
        <?php endif; ?>

        <form method="POST" action="">
            <div class="mb-3">
                <label class="form-label text-secondary small">Email Address</label>
                <input type="email" name="email" class="form-control" placeholder="admin@example.com" required value="<?= htmlspecialchars($_POST['email'] ?? '') ?>">
            </div>

            <div class="mb-4">
                <label class="form-label text-secondary small">Password</label>
                <input type="password" name="password" class="form-control" placeholder="••••••••" required>
            </div>

            <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">Sign In to Admin Dashboard</button>
        </form>
    </div>
</body>
</html>
