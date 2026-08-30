<?php
declare(strict_types=1);

// backend/api/models.php
// REST API Endpoint - Models Directory & Search (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';

$db = Database::getInstance();
$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';

if ($method === 'GET') {
    $search = filter_input(INPUT_GET, 'q', FILTER_DEFAULT) ?? '';
    $category = filter_input(INPUT_GET, 'category', FILTER_DEFAULT) ?? '';
    $country = filter_input(INPUT_GET, 'country', FILTER_DEFAULT) ?? 'Bangladesh';
    $minRating = (float)(filter_input(INPUT_GET, 'min_rating', FILTER_VALIDATE_FLOAT) ?: 0.0);

    try {
        $sql = "SELECT id, name, rating, review_count, location, is_online, is_verified, bio, skills, languages, services, hourly_rate, availability_days, gender, age, height_cm, image_res_name, country FROM models WHERE 1=1";
        $params = [];

        if (!empty($search)) {
            $sql .= " AND (name LIKE ? OR bio LIKE ? OR skills LIKE ?)";
            $searchWild = "%{$search}%";
            $params[] = $searchWild;
            $params[] = $searchWild;
            $params[] = $searchWild;
        }

        if (!empty($category)) {
            $sql .= " AND services LIKE ?";
            $params[] = "%{$category}%";
        }

        if (!empty($country)) {
            $sql .= " AND country = ?";
            $params[] = $country;
        }

        if ($minRating > 0) {
            $sql .= " AND rating >= ?";
            $params[] = $minRating;
        }

        $sql .= " ORDER BY is_online DESC, rating DESC, review_count DESC";

        $stmt = $db->prepare($sql);
        $stmt->execute($params);
        $models = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Fallback default dataset if database table is currently empty
        if (empty($models)) {
            $models = [
                [
                    'id' => 1,
                    'name' => 'Ayesha Rahman',
                    'rating' => 4.9,
                    'review_count' => 128,
                    'location' => 'Gulshan, Dhaka',
                    'is_online' => true,
                    'is_verified' => true,
                    'bio' => 'Professional fashion and commercial runway model with 6+ years international experience.',
                    'skills' => 'Commercial, Editorial, Runway, Traditional Wear',
                    'languages' => 'Bangla, English, Hindi',
                    'services' => 'Fashion Shoot, Commercial Ad, Event Host',
                    'hourly_rate' => 3500,
                    'availability_days' => 'Fri, Sat, Sun, Mon',
                    'gender' => 'Female',
                    'age' => 24,
                    'height_cm' => 175,
                    'image_res_name' => 'model_ayesha',
                    'country' => 'Bangladesh'
                ],
                [
                    'id' => 2,
                    'name' => 'Tanvir Ahmed',
                    'rating' => 4.8,
                    'review_count' => 94,
                    'location' => 'Banani, Dhaka',
                    'is_online' => true,
                    'is_verified' => true,
                    'bio' => 'Fitness & lifestyle brand model, actor, and promotional talent.',
                    'skills' => 'Fitness, Lifestyle, Casual Brand, TV Commercial',
                    'languages' => 'Bangla, English',
                    'services' => 'Fitness Shoot, Brand Ambassador, Music Video',
                    'hourly_rate' => 3000,
                    'availability_days' => 'All Days',
                    'gender' => 'Male',
                    'age' => 26,
                    'height_cm' => 183,
                    'image_res_name' => 'model_tanvir',
                    'country' => 'Bangladesh'
                ]
            ];
        }

        sendJsonResponse('success', 'Models retrieved successfully', [
            'total' => count($models),
            'models' => $models
        ]);
    } catch (Throwable $e) {
        sendJsonResponse('error', 'Query execution failed: ' . $e->getMessage(), [], 500);
    }
}

sendJsonResponse('error', 'Method Not Allowed', [], 405);
