<?php
declare(strict_types=1);

// backend/api/firebase_upload.php
// Production Firebase Storage Upload Endpoint for Modol Connect App & Web (PHP 8.2+)

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit(0);
}

require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/firebase.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse('error', 'Only POST method is accepted for file upload', [], 405);
}

try {
    $folder = $_POST['folder'] ?? 'uploads';
    // Sanitize folder path
    $folder = preg_replace('/[^a-zA-Z0-9_\-\/]/', '', $folder);
    if (empty($folder)) {
        $folder = 'uploads';
    }

    $fileContent = null;
    $originalName = 'upload.jpg';
    $mimeType = 'image/jpeg';

    // 1. Check if multipart file upload
    if (isset($_FILES['file']) && is_uploaded_file($_FILES['file']['tmp_name'])) {
        $fileError = $_FILES['file']['error'];
        if ($fileError !== UPLOAD_ERR_OK) {
            sendJsonResponse('error', 'File upload error code: ' . $fileError, [], 400);
        }

        $tmpPath = $_FILES['file']['tmp_name'];
        $originalName = $_FILES['file']['name'];
        $fileContent = file_get_contents($tmpPath);

        $finfo = finfo_open(FILEINFO_MIME_TYPE);
        $detectedMime = finfo_file($finfo, $tmpPath);
        finfo_close($finfo);

        if ($detectedMime) {
            $mimeType = $detectedMime;
        }
    } 
    // 2. Check if base64 file data provided in POST
    elseif (!empty($_POST['base64_data'])) {
        $rawBase64 = (string)$_POST['base64_data'];
        if (preg_match('/^data:(image\/[a-zA-Z0-9\+\-]+);base64,/', $rawBase64, $matches)) {
            $mimeType = $matches[1];
            $rawBase64 = substr($rawBase64, strpos($rawBase64, ',') + 1);
        }
        $fileContent = base64_decode($rawBase64);
        $originalName = $_POST['file_name'] ?? ('upload_' . time() . '.jpg');
    }
    // 3. Check raw php://input
    else {
        $rawInput = file_get_contents('php://input');
        if (!empty($rawInput)) {
            $jsonData = json_decode($rawInput, true);
            if (isset($jsonData['base64_data'])) {
                $rawBase64 = $jsonData['base64_data'];
                if (preg_match('/^data:(image\/[a-zA-Z0-9\+\-]+);base64,/', $rawBase64, $matches)) {
                    $mimeType = $matches[1];
                    $rawBase64 = substr($rawBase64, strpos($rawBase64, ',') + 1);
                }
                $fileContent = base64_decode($rawBase64);
                $originalName = $jsonData['file_name'] ?? ('upload_' . time() . '.jpg');
                $folder = $jsonData['folder'] ?? $folder;
            }
        }
    }

    if ($fileContent === null || strlen($fileContent) === 0) {
        sendJsonResponse('error', 'No valid file content received. Send multipart file as `file` or `base64_data`.', [], 400);
    }

    // Determine extension
    $extension = pathinfo($originalName, PATHINFO_EXTENSION);
    if (empty($extension)) {
        $extension = ($mimeType === 'image/png') ? 'png' : 'jpg';
    }

    // Generate unique file path in Firebase Storage
    $uniqueName = time() . '_' . bin2hex(random_bytes(6)) . '.' . $extension;
    $remotePath = trim($folder, '/') . '/' . $uniqueName;

    // Upload to Firebase Storage
    $uploadResult = FirebaseService::uploadToStorage($fileContent, $remotePath, $mimeType);

    sendJsonResponse('success', 'File uploaded to Firebase Storage successfully', [
        'url' => $uploadResult['url'],
        'remote_path' => $uploadResult['path'],
        'bucket' => $uploadResult['bucket'],
        'size' => $uploadResult['size'],
        'content_type' => $uploadResult['content_type'],
        'storage_provider' => 'Firebase Storage'
    ], 200);

} catch (Throwable $e) {
    sendJsonResponse('error', 'Firebase upload failed: ' . $e->getMessage(), [
        'project_id' => FirebaseConfig::getProjectId(),
        'storage_bucket' => FirebaseConfig::getStorageBucket()
    ], 500);
}
