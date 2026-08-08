<?php
// backend/middleware/admin.php
// Middleware - Admin RBAC Guard
require_once __DIR__ . '/../config/auth.php';

function enforceAdminGuard() {
    checkAdminAuth();
}
?>
