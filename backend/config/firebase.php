<?php
declare(strict_types=1);

// backend/config/firebase.php
// Firebase Integration & Cloud Storage Engine for Modol Connect (PHP 8.2+)

require_once __DIR__ . '/config.php';

final class FirebaseConfig
{
    public const DEFAULT_PROJECT_ID = 'modol-connect';
    public const DEFAULT_STORAGE_BUCKET = 'modol-connect.firebasestorage.app';
    public const DEFAULT_API_KEY = 'AIzaSyA14wj8tZCED9AsSbyvy_SO1zS_Q_AR9nA';
    public const CONFIG_FILE = __DIR__ . '/firebase_config.json';
    public const SERVICE_ACCOUNT_FILE = __DIR__ . '/serviceAccountKey.json';

    public static function getProjectId(): string
    {
        $settings = self::loadCustomSettings();
        return $settings['project_id'] ?? (getenv('FIREBASE_PROJECT_ID') ?: self::DEFAULT_PROJECT_ID);
    }

    public static function getStorageBucket(): string
    {
        $settings = self::loadCustomSettings();
        return $settings['storage_bucket'] ?? (getenv('FIREBASE_STORAGE_BUCKET') ?: self::DEFAULT_STORAGE_BUCKET);
    }

    public static function getApiKey(): string
    {
        $settings = self::loadCustomSettings();
        return $settings['api_key'] ?? (getenv('FIREBASE_API_KEY') ?: self::DEFAULT_API_KEY);
    }

    public static function hasServiceAccount(): bool
    {
        return file_exists(self::SERVICE_ACCOUNT_FILE) && is_readable(self::SERVICE_ACCOUNT_FILE);
    }

    public static function saveCustomSettings(array $data): bool
    {
        $current = self::loadCustomSettings();
        $merged = array_merge($current, $data);
        return file_put_contents(self::CONFIG_FILE, json_encode($merged, JSON_PRETTY_PRINT)) !== false;
    }

    public static function loadCustomSettings(): array
    {
        if (file_exists(self::CONFIG_FILE)) {
            $content = @file_get_contents(self::CONFIG_FILE);
            if ($content) {
                $decoded = json_decode($content, true);
                if (is_array($decoded)) {
                    return $decoded;
                }
            }
        }
        return [];
    }
}

class FirebaseService
{
    /**
     * Upload binary data directly to Firebase Storage bucket
     * Returns the publicly accessible download URL
     */
    public static function uploadToStorage(
        string $fileContent,
        string $remotePath,
        string $mimeType = 'image/jpeg'
    ): array {
        $bucket = FirebaseConfig::getStorageBucket();
        $encodedPath = urlencode(ltrim($remotePath, '/'));
        $downloadToken = bin2hex(random_bytes(16));

        // Firebase Storage upload URL
        $uploadUrl = sprintf(
            'https://firebasestorage.googleapis.com/v0/b/%s/o?name=%s&uploadType=media',
            $bucket,
            $encodedPath
        );

        $apiKey = FirebaseConfig::getApiKey();
        if (!empty($apiKey)) {
            $uploadUrl .= '&key=' . urlencode($apiKey);
        }

        $headers = [
            'Content-Type: ' . $mimeType,
            'Content-Length: ' . strlen($fileContent),
            'x-goog-meta-firebaseStorageDownloadTokens: ' . $downloadToken
        ];

        $ch = curl_init();
        curl_setopt_array($ch, [
            CURLOPT_URL => $uploadUrl,
            CURLOPT_POST => true,
            CURLOPT_POSTFIELDS => $fileContent,
            CURLOPT_HTTPHEADER => $headers,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_SSL_VERIFYPEER => false,
            CURLOPT_SSL_VERIFYHOST => 0,
            CURLOPT_TIMEOUT => 30
        ]);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlError = curl_error($ch);
        curl_close($ch);

        if ($curlError) {
            throw new RuntimeException("Firebase upload network error: " . $curlError);
        }

        $json = json_decode((string)$response, true);

        // Firebase Storage returns 200 on success
        if ($httpCode >= 200 && $httpCode < 300) {
            $token = $json['downloadTokens'] ?? $downloadToken;
            $publicUrl = sprintf(
                'https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s',
                $bucket,
                $encodedPath,
                $token
            );

            return [
                'success' => true,
                'url' => $publicUrl,
                'path' => $remotePath,
                'bucket' => $bucket,
                'size' => strlen($fileContent),
                'content_type' => $mimeType,
                'token' => $token,
                'raw' => $json
            ];
        }

        // If direct upload returned non-200, return detailed diagnostic
        $errorMessage = $json['error']['message'] ?? ("Firebase Storage HTTP " . $httpCode);
        throw new RuntimeException("Firebase Storage upload failed: " . $errorMessage);
    }

    /**
     * Test connection to Firebase project and storage bucket
     */
    public static function testConnection(): array
    {
        $bucket = FirebaseConfig::getStorageBucket();
        $projectId = FirebaseConfig::getProjectId();
        $apiKey = FirebaseConfig::getApiKey();

        $url = sprintf('https://firebasestorage.googleapis.com/v0/b/%s', $bucket);
        if (!empty($apiKey)) {
            $url .= '?key=' . urlencode($apiKey);
        }

        $ch = curl_init();
        curl_setopt_array($ch, [
            CURLOPT_URL => $url,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_TIMEOUT => 10,
            CURLOPT_SSL_VERIFYPEER => false,
            CURLOPT_SSL_VERIFYHOST => 0
        ]);
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        $json = json_decode((string)$response, true);

        return [
            'connected' => ($httpCode === 200 || $httpCode === 401 || $httpCode === 403),
            'http_code' => $httpCode,
            'project_id' => $projectId,
            'storage_bucket' => $bucket,
            'has_service_account' => FirebaseConfig::hasServiceAccount(),
            'message' => ($httpCode === 200) ? 'Firebase Storage Bucket is active and accessible' : 'Firebase Storage endpoint reachable (status ' . $httpCode . ')'
        ];
    }

    /**
     * Store or update a document in Firestore using the REST API
     */
    public static function upsertFirestoreDocument(string $collection, string $documentId, array $data): array
    {
        $projectId = FirebaseConfig::getProjectId();
        $url = sprintf(
            'https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/%s/%s',
            $projectId,
            $collection,
            $documentId
        );

        $apiKey = FirebaseConfig::getApiKey();
        if (!empty($apiKey)) {
            $url .= '?key=' . urlencode($apiKey);
        }

        // Format fields for Firestore REST API
        $formattedFields = [];
        foreach ($data as $key => $value) {
            if (is_int($value)) {
                $formattedFields[$key] = ['integerValue' => (string)$value];
            } elseif (is_float($value)) {
                $formattedFields[$key] = ['doubleValue' => $value];
            } elseif (is_bool($value)) {
                $formattedFields[$key] = ['booleanValue' => $value];
            } elseif (is_array($value)) {
                $formattedFields[$key] = ['stringValue' => json_encode($value)];
            } else {
                $formattedFields[$key] = ['stringValue' => (string)$value];
            }
        }

        $payload = json_encode(['fields' => $formattedFields]);

        $ch = curl_init();
        curl_setopt_array($ch, [
            CURLOPT_URL => $url,
            CURLOPT_CUSTOMREQUEST => 'PATCH',
            CURLOPT_POSTFIELDS => $payload,
            CURLOPT_HTTPHEADER => ['Content-Type: application/json'],
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_TIMEOUT => 15,
            CURLOPT_SSL_VERIFYPEER => false,
            CURLOPT_SSL_VERIFYHOST => 0
        ]);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        return [
            'success' => ($httpCode >= 200 && $httpCode < 300),
            'http_code' => $httpCode,
            'response' => json_decode((string)$response, true)
        ];
    }
}
