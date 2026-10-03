<?php
declare(strict_types=1);

// backend/api/countries.php
// REST API Endpoint - Dynamic Countries, Currencies & Multi-Payment Methods (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';

header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

$db = Database::getInstance();

// Auto create tables if not exists (Driver-aware and exception-safe)
try {
    if (Database::isMySQL()) {
        $db->exec("
        CREATE TABLE IF NOT EXISTS `countries` (
            `id` INT AUTO_INCREMENT PRIMARY KEY,
            `country_name` VARCHAR(100) NOT NULL,
            `iso_code` VARCHAR(10) NOT NULL,
            `phone_code` VARCHAR(15) NOT NULL,
            `currency_code` VARCHAR(10) NOT NULL,
            `flag` VARCHAR(20) DEFAULT '🌐',
            `status` VARCHAR(20) DEFAULT 'Active',
            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
        ");
        $db->exec("
        CREATE TABLE IF NOT EXISTS `payment_methods` (
            `id` INT AUTO_INCREMENT PRIMARY KEY,
            `country_id` INT NOT NULL,
            `method_name` VARCHAR(100) NOT NULL,
            `method_type` VARCHAR(50) DEFAULT 'Mobile Wallet',
            `logo` VARCHAR(255) DEFAULT '',
            `min_amount` DECIMAL(12,2) DEFAULT 100.00,
            `max_amount` DECIMAL(12,2) DEFAULT 500000.00,
            `status` VARCHAR(20) DEFAULT 'Active',
            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
        ");
    } else {
        $db->exec("
        CREATE TABLE IF NOT EXISTS countries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            country_name TEXT NOT NULL,
            iso_code TEXT NOT NULL,
            phone_code TEXT NOT NULL,
            currency_code TEXT NOT NULL,
            flag TEXT DEFAULT '🌐',
            status TEXT DEFAULT 'Active',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS payment_methods (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            country_id INTEGER NOT NULL,
            method_name TEXT NOT NULL,
            method_type TEXT DEFAULT 'Mobile Wallet',
            logo TEXT DEFAULT '',
            min_amount REAL DEFAULT 100.0,
            max_amount REAL DEFAULT 500000.0,
            status TEXT DEFAULT 'Active',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        ");
    }
} catch (Throwable $e) {
    error_log("api/countries.php table notice: " . $e->getMessage());
}

// Check if seeding is required
$countCountries = (int)$db->query("SELECT COUNT(*) FROM countries")->fetchColumn();
if ($countCountries === 0) {
    $seedData = require __DIR__ . '/../config/country_payment_seed.php';
    $insCountry = $db->prepare("INSERT INTO countries (country_name, iso_code, phone_code, currency_code, flag, status) VALUES (?, ?, ?, ?, ?, 'Active')");
    $insMethod = $db->prepare("INSERT INTO payment_methods (country_id, method_name, method_type, logo, min_amount, max_amount, status) VALUES (?, ?, ?, '', ?, ?, 'Active')");

    foreach ($seedData as $c) {
        $insCountry->execute([$c['country_name'], $c['iso_code'], $c['phone_code'], $c['currency_code'], $c['flag']]);
        $cId = (int)$db->lastInsertId();
        foreach ($c['methods'] as $m) {
            $insMethod->execute([$cId, $m['name'], $m['type'], (float)$m['min'], (float)$m['max']]);
        }
    }
}

$queryCountry = trim($_GET['country'] ?? '');
$queryCurrency = strtoupper(trim($_GET['currency'] ?? ''));

$sql = "SELECT * FROM countries WHERE status = 'Active'";
$params = [];

if ($queryCountry !== '') {
    $sql .= " AND (country_name LIKE ? OR iso_code = ?)";
    $params[] = "%$queryCountry%";
    $params[] = strtoupper($queryCountry);
}

if ($queryCurrency !== '') {
    $sql .= " AND currency_code = ?";
    $params[] = $queryCurrency;
}

$sql .= " ORDER BY country_name ASC";
$stmt = $db->prepare($sql);
$stmt->execute($params);
$countryRows = $stmt->fetchAll(PDO::FETCH_ASSOC);

$methodStmt = $db->prepare("SELECT id, country_id, method_name, method_type, logo, min_amount, max_amount, status FROM payment_methods WHERE country_id = ? AND status = 'Active' ORDER BY id ASC");

$responseList = [];
foreach ($countryRows as $row) {
    $cId = (int)$row['id'];
    $methodStmt->execute([$cId]);
    $methods = $methodStmt->fetchAll(PDO::FETCH_ASSOC);

    $responseList[] = [
        'id' => (int)$row['id'],
        'country_name' => $row['country_name'],
        'iso_code' => $row['iso_code'],
        'phone_code' => $row['phone_code'],
        'currency_code' => $row['currency_code'],
        'flag' => $row['flag'],
        'status' => $row['status'],
        'payment_methods' => $methods
    ];
}

echo json_encode([
    'status' => 'success',
    'total_countries' => count($responseList),
    'data' => $responseList
], JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
