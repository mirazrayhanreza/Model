<?php
declare(strict_types=1);

// backend/config/auth.php
// Authentication & Security Guard for PHP 8.2+

require_once __DIR__ . '/database.php';
require_once __DIR__ . '/config.php';

function checkAdminAuth(): void
{
    $token = getBearerToken();
    $role = $_SESSION['user_role'] ?? null;

    if ($role !== 'ADMIN' && $role !== 'SUPER_ADMIN' && empty($token)) {
        if (isApiRequest()) {
            sendJsonResponse('error', 'Unauthorized: Administrative access required.', [], 401);
        } else {
            header('Location: ' . Config::BASE_URL . 'admin/login.php');
            exit(0);
        }
    }
}

function checkAgentAuth(): void
{
    $role = $_SESSION['user_role'] ?? null;
    $token = getBearerToken();

    if ($role !== 'CASH_AGENT' && empty($token)) {
        if (isApiRequest()) {
            sendJsonResponse('error', 'Unauthorized: Verified Cash Agent login required.', [], 401);
        } else {
            header('Location: ' . Config::BASE_URL . 'agent/login.php');
            exit(0);
        }
    }
}

function isApiRequest(): bool
{
    $accept = $_SERVER['HTTP_ACCEPT'] ?? '';
    $contentType = $_SERVER['CONTENT_TYPE'] ?? '';
    $uri = $_SERVER['REQUEST_URI'] ?? '';

    return str_contains($accept, 'application/json')
        || str_contains($contentType, 'application/json')
        || str_contains($uri, '/api/');
}

function sanitizeInput(string|null $data): string
{
    if ($data === null) {
        return '';
    }
    return htmlspecialchars(stripslashes(trim($data)), ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}

function getBearerToken(): ?string
{
    $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? null;
    if (!$authHeader && function_exists('getallheaders')) {
        $headers = getallheaders();
        $authHeader = $headers['Authorization'] ?? $headers['authorization'] ?? null;
    }

    if ($authHeader && preg_match('/Bearer\s(\S+)/i', $authHeader, $matches)) {
        return $matches[1];
    }
    return null;
}
