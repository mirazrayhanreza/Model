<?php
declare(strict_types=1);

// backend/api/firebase_sync.php
// Production Firestore Synchronization API (PHP 8.2+)

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit(0);
}

require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/firebase.php';

$action = $_GET['action'] ?? ($_POST['action'] ?? 'status');

if ($action === 'status') {
    $connStatus = FirebaseService::testConnection();
    sendJsonResponse('success', 'Firebase status', [
        'firebase' => $connStatus,
        'config' => [
            'project_id' => FirebaseConfig::getProjectId(),
            'storage_bucket' => FirebaseConfig::getStorageBucket(),
            'has_service_account' => FirebaseConfig::hasServiceAccount()
        ]
    ]);
}

if ($action === 'sync_models') {
    try {
        $db = Database::getInstance();
        $stmt = $db->query("SELECT * FROM models LIMIT 50");
        $models = $stmt->fetchAll(PDO::FETCH_ASSOC);

        $synced = 0;
        foreach ($models as $m) {
            $docId = 'model_' . $m['id'];
            $res = FirebaseService::upsertFirestoreDocument('models', $docId, [
                'name' => $m['name'],
                'hourly_rate' => (float)$m['hourly_rate'],
                'category' => $m['category'] ?? 'Fashion',
                'location' => $m['location'] ?? 'Dhaka',
                'rating' => (float)($m['rating'] ?? 5.0),
                'is_verified' => (bool)($m['is_verified'] ?? true)
            ]);
            if ($res['success']) {
                $synced++;
            }
        }

        sendJsonResponse('success', "Synced {$synced} models to Firebase Firestore", [
            'synced_count' => $synced,
            'total_models' => count($models)
        ]);
    } catch (Throwable $e) {
        sendJsonResponse('error', 'Sync failed: ' . $e->getMessage(), [], 500);
    }
}

sendJsonResponse('error', 'Invalid action specified. Supported: status, sync_models', [], 400);
