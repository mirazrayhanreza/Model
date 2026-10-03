<?php
declare(strict_types=1);

// backend/api/tracking.php
// REST API Endpoint - Live GPS Location Tracking & Telemetry (PHP 8.2+)

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

$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$sampleSessions = [
    [
        'session_id' => 'TRK-84920',
        'booking_id' => 'BK-1024',
        'model_name' => 'Ananya Sen',
        'model_avatar' => 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80',
        'model_phone' => '+880 1822-987654',
        'model_address' => 'Gulshan Avenue, near Shooting Club, Dhaka',
        'model_lat' => 23.7745,
        'model_lng' => 90.4120,
        'model_battery' => 86,
        'user_name' => 'Rahul Verma',
        'user_avatar' => 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80',
        'user_phone' => '+880 1711-234567',
        'destination_studio' => 'Studio Mirage, House 42, Road 11, Banani, Dhaka',
        'user_lat' => 23.7937,
        'user_lng' => 90.4066,
        'user_battery' => 92,
        'distance_km' => 2.4,
        'eta_minutes' => 7,
        'speed_kmh' => 28,
        'status' => 'EN_ROUTE',
        'transport_mode' => 'Car (Uber Ride #849)',
        'gps_accuracy' => 2.5,
        'sos_active' => false
    ],
    [
        'session_id' => 'TRK-77190',
        'booking_id' => 'BK-2058',
        'model_name' => 'Priya Sharma',
        'model_avatar' => 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=150&q=80',
        'model_phone' => '+880 1733-112233',
        'model_address' => 'Uttara Sector 4, Azampur Intersection, Dhaka',
        'model_lat' => 23.8681,
        'model_lng' => 90.3984,
        'model_battery' => 74,
        'user_name' => 'Tanvir Ahmed',
        'user_avatar' => 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=150&q=80',
        'user_phone' => '+880 1912-334455',
        'destination_studio' => 'Radisson Blu Water Garden, Grand Studio 1, Airport Rd',
        'user_lat' => 23.8164,
        'user_lng' => 90.4074,
        'user_battery' => 81,
        'distance_km' => 5.8,
        'eta_minutes' => 14,
        'speed_kmh' => 35,
        'status' => 'IN_TRANSIT',
        'transport_mode' => 'Private Studio Escort Car',
        'gps_accuracy' => 3.1,
        'sos_active' => false
    ]
];

$settingsFile = __DIR__ . '/../config/app_settings.json';
$appSettings = ['show_live_gps_tab' => false];
if (file_exists($settingsFile)) {
    $loaded = json_decode((string)file_get_contents($settingsFile), true);
    if (is_array($loaded)) {
        $appSettings = array_merge($appSettings, $loaded);
    }
}

if ($method === 'GET') {
    $sessionId = $_GET['session_id'] ?? null;
    $bookingId = $_GET['booking_id'] ?? null;

    if ($sessionId) {
        foreach ($sampleSessions as $s) {
            if ($s['session_id'] === $sessionId) {
                echo json_encode([
                    'status' => 'success', 
                    'data' => $s,
                    'show_live_gps_tab' => !empty($appSettings['show_live_gps_tab'])
                ]);
                exit;
            }
        }
    }

    echo json_encode([
        'status' => 'success',
        'count' => count($sampleSessions),
        'show_live_gps_tab' => !empty($appSettings['show_live_gps_tab']),
        'sessions' => $sampleSessions
    ]);
    exit;
}

if ($method === 'POST') {
    $action = $input['action'] ?? 'update_location';
    $sessionId = $input['session_id'] ?? 'TRK-84920';

    if ($action === 'toggle_live_gps_tab') {
        $enabled = !empty($input['enabled']);
        $appSettings['show_live_gps_tab'] = $enabled;
        file_put_contents($settingsFile, json_encode($appSettings, JSON_PRETTY_PRINT));
        echo json_encode([
            'status' => 'success',
            'show_live_gps_tab' => $enabled,
            'message' => $enabled ? 'Live GPS tab enabled in mobile app bottom nav' : 'Live GPS tab hidden from mobile app bottom nav'
        ]);
        exit;
    }

    if ($action === 'ping_location') {
        $lat = floatval($input['lat'] ?? 23.7745);
        $lng = floatval($input['lng'] ?? 90.4120);
        $speed = floatval($input['speed'] ?? 25.0);
        $battery = intval($input['battery'] ?? 85);

        echo json_encode([
            'status' => 'success',
            'message' => 'Telemetry coordinates updated successfully',
            'data' => [
                'session_id' => $sessionId,
                'lat' => $lat,
                'lng' => $lng,
                'speed' => $speed,
                'battery' => $battery,
                'timestamp' => time()
            ]
        ]);
        exit;
    }

    if ($action === 'sos_alert') {
        echo json_encode([
            'status' => 'success',
            'message' => 'Emergency SOS alert activated for session ' . $sessionId,
            'sos' => true,
            'timestamp' => time()
        ]);
        exit;
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'Action ' . $action . ' processed'
    ]);
    exit;
}
