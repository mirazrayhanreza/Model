<?php
// backend/middleware/agent.php
// Middleware - Cash Agent RBAC Guard
require_once __DIR__ . '/../config/auth.php';

function enforceAgentGuard() {
    checkAgentAuth();
}
?>
