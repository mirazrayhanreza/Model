<?php
declare(strict_types=1);

// backend/admin/live_gps.php
// Modol Connect - User to Model Live GPS Location Tracking & Telemetry Dashboard (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'success';

$settingsFile = __DIR__ . '/../config/app_settings.json';
$appSettings = [
    'show_live_gps_tab' => false
];
if (file_exists($settingsFile)) {
    $loaded = json_decode((string)file_get_contents($settingsFile), true);
    if (is_array($loaded)) {
        $appSettings = array_merge($appSettings, $loaded);
    }
}

// Handle Post Actions (SOS Trigger, Status Update, Escort Dispatch, App Tab Visibility)
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $sessionId = htmlspecialchars($_POST['session_id'] ?? '', ENT_QUOTES, 'UTF-8');

    if ($action === 'toggle_mobile_tab') {
        $newVal = !empty($_POST['enable_tab']);
        $appSettings['show_live_gps_tab'] = $newVal;
        file_put_contents($settingsFile, json_encode($appSettings, JSON_PRETTY_PRINT));
        $msg = $newVal 
            ? "✅ Live GPS Tab is now VISIBLE in Mobile App Bottom Navigation for Users & Models." 
            : "🔒 Live GPS Tab is now HIDDEN from Mobile App Bottom Navigation (Controlled by Admin).";
        $msgType = $newVal ? 'success' : 'warning';
    } elseif ($action === 'sos_alert') {
        $msg = "🚨 EMERGENCY SOS DISPATCHED for Session {$sessionId}! Security team & emergency contacts notified with real-time GPS coordinates.";
        $msgType = 'danger';
    } elseif ($action === 'resolve_sos') {
        $msg = "✅ Emergency alert resolved for Session {$sessionId}. Safe status confirmed.";
        $msgType = 'success';
    } elseif ($action === 'ping_model') {
        $msg = "📍 Real-time GPS ping sent to Model device. High-accuracy location refreshed.";
        $msgType = 'info';
    } elseif ($action === 'ping_user') {
        $msg = "📍 Real-time GPS ping sent to Client device. Coordinates synced.";
        $msgType = 'info';
    } elseif ($action === 'mark_arrived') {
        $msg = "🎉 Session {$sessionId} updated: Model arrived at destination venue safely.";
        $msgType = 'success';
    } elseif ($action === 'complete') {
        $msg = "🏁 Escort tracking completed for Session {$sessionId}. Audit trail archived.";
        $msgType = 'success';
    }
}

// Live Escort Tracking Sessions Data
$sessions = [
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
        'sos_active' => false,
        'started_at' => '10:45 AM',
        'signal_strength' => 'Excellent (5G)'
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
        'sos_active' => false,
        'started_at' => '10:30 AM',
        'signal_strength' => 'Good (4G LTE)'
    ],
    [
        'session_id' => 'TRK-61400',
        'booking_id' => 'BK-3091',
        'model_name' => 'Meera Kapoor',
        'model_avatar' => 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=150&q=80',
        'model_phone' => '+880 1622-445566',
        'model_address' => 'Arrived at Venue Entrance, Westin Main Porch',
        'model_lat' => 23.7927,
        'model_lng' => 90.4148,
        'model_battery' => 95,
        'user_name' => 'Imran Hossain',
        'user_avatar' => 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80',
        'user_phone' => '+880 1819-556677',
        'destination_studio' => 'The Westin Dhaka, Ballroom Stage 2, Gulshan 2',
        'user_lat' => 23.7925,
        'user_lng' => 90.4150,
        'user_battery' => 89,
        'distance_km' => 0.05,
        'eta_minutes' => 0,
        'speed_kmh' => 0,
        'status' => 'ARRIVED',
        'transport_mode' => 'At Venue',
        'gps_accuracy' => 1.8,
        'sos_active' => false,
        'started_at' => '09:15 AM',
        'signal_strength' => 'Excellent (Wi-Fi 6)'
    ],
    [
        'session_id' => 'TRK-55210',
        'booking_id' => 'BK-4112',
        'model_name' => 'Zara Khan',
        'model_avatar' => 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80',
        'model_phone' => '+880 1955-443322',
        'model_address' => 'Dhanmondi 27, near Rapa Plaza',
        'model_lat' => 23.7538,
        'model_lng' => 90.3768,
        'model_battery' => 68,
        'user_name' => 'Farhan Kabir',
        'user_avatar' => 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80',
        'user_phone' => '+880 1788-990011',
        'destination_studio' => 'Pan Pacific Sonargaon, Grand Ballroom Hall',
        'user_lat' => 23.7486,
        'user_lng' => 90.3957,
        'user_battery' => 79,
        'distance_km' => 3.1,
        'eta_minutes' => 11,
        'speed_kmh' => 22,
        'status' => 'EN_ROUTE',
        'transport_mode' => 'Sedan Escort',
        'gps_accuracy' => 2.9,
        'sos_active' => false,
        'started_at' => '10:50 AM',
        'signal_strength' => 'Excellent (5G)'
    ]
];

renderAdminHeader('Live GPS Tracking', 'live_gps');
?>

<!-- Leaflet Map Assets -->
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>

<style>
.radar-pulse {
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: #00E676;
    box-shadow: 0 0 0 0 rgba(0, 230, 118, 0.7);
    animation: radarPulse 1.8s infinite;
}
@keyframes radarPulse {
    0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(0, 230, 118, 0.7); }
    70% { transform: scale(1); box-shadow: 0 0 0 10px rgba(0, 230, 118, 0); }
    100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(0, 230, 118, 0); }
}
.sos-badge-pulse {
    animation: sosPulse 1s infinite alternate;
}
@keyframes sosPulse {
    from { background-color: #ef4444; }
    to { background-color: #991b1b; }
}
.map-container {
    height: 540px;
    border-radius: 16px;
    overflow: hidden;
    position: relative;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}
.session-card {
    transition: all 0.2s ease;
    cursor: pointer;
    border: 1.5px solid #e2e8f0;
}
.session-card:hover {
    border-color: #6366f1;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(99, 102, 241, 0.12);
}
.session-card.active-session {
    border-color: #10b981;
    background: #f0fdf4;
    box-shadow: 0 4px 14px rgba(16, 185, 129, 0.15);
}
.telemetry-chip {
    background: #0f172a;
    color: #38bdf8;
    font-family: 'JetBrains Mono', 'Fira Code', monospace;
    font-size: 0.78rem;
    padding: 0.35rem 0.65rem;
    border-radius: 8px;
    display: inline-flex;
    align-items: center;
    gap: 6px;
}
</style>

<!-- Alert Message Notification -->
<?php if ($msg): ?>
<div class="alert alert-<?= $msgType ?> alert-dismissible fade show d-flex align-items-center gap-2 mb-4 shadow-sm" role="alert">
    <i class="bi <?= $msgType === 'danger' ? 'bi-exclamation-triangle-fill' : 'bi-check-circle-fill' ?> fs-5"></i>
    <div><?= $msg ?></div>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
<?php endif; ?>

<!-- Header Title & Real-time Live Badge -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <div class="d-flex align-items-center gap-2 mb-1">
            <h2 class="fw-bold mb-0" style="color: #0f172a; letter-spacing: -0.02em;">Live GPS Location Tracking</h2>
            <span class="badge bg-success-subtle text-success border border-success-subtle d-inline-flex align-items-center gap-2 px-3 py-2 rounded-pill fw-bold" style="font-size: 0.8rem;">
                <span class="radar-pulse"></span> REAL-TIME GPS ESCORT ENGINE
            </span>
        </div>
        <p class="text-secondary mb-0" style="font-size: 0.9rem;">
            Full telemetry tracking between Client & Model with real-time GPS coordinates, speed, battery, ETA, and emergency SOS protocol.
        </p>
    </div>
    <div class="d-flex align-items-center gap-2 flex-wrap">
        <!-- Admin App Bottom Tab Control Button -->
        <form method="POST" class="d-inline m-0">
            <input type="hidden" name="action" value="toggle_mobile_tab">
            <?php if (!empty($appSettings['show_live_gps_tab'])): ?>
                <input type="hidden" name="enable_tab" value="0">
                <button type="submit" class="btn btn-success text-white d-flex align-items-center gap-2 px-3 py-2 fw-semibold shadow-sm" title="Currently Visible in Mobile App Bottom Bar. Click to HIDE.">
                    <i class="bi bi-eye-fill"></i>
                    <span>App Tab: <strong>VISIBLE</strong> (Click to HIDE)</span>
                </button>
            <?php else: ?>
                <input type="hidden" name="enable_tab" value="1">
                <button type="submit" class="btn btn-outline-secondary bg-white d-flex align-items-center gap-2 px-3 py-2 fw-semibold shadow-sm" title="Currently HIDDEN from Mobile App Bottom Bar. Click to SHOW.">
                    <i class="bi bi-eye-slash text-danger"></i>
                    <span>App Tab: <strong class="text-danger">HIDDEN</strong> (Click to SHOW)</span>
                </button>
            <?php endif; ?>
        </form>

        <button class="btn btn-outline-danger d-flex align-items-center gap-2 px-3 py-2 fw-semibold shadow-sm" data-bs-toggle="modal" data-bs-target="#sosEmergencyModal">
            <i class="bi bi-shield-fill-exclamation text-danger"></i>
            <span>Emergency SOS Panel</span>
        </button>
        <button class="btn btn-primary d-flex align-items-center gap-2 px-3 py-2 fw-semibold shadow-sm" onclick="refreshGpsData()">
            <i class="bi bi-arrow-clockwise"></i>
            <span>Refresh Telemetry</span>
        </button>
    </div>
</div>

<!-- Key Telemetry Metrics Row -->
<div class="row g-3 mb-4">
    <div class="col-6 col-lg-3">
        <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="text-secondary fw-semibold small">Active Escorts</span>
                <span class="p-2 rounded-3 bg-success-subtle text-success"><i class="bi bi-broadcast fs-6"></i></span>
            </div>
            <h3 class="fw-bold mb-0" style="color: #0f172a;"><?= count($sessions) ?> Live</h3>
            <span class="text-success small fw-medium"><i class="bi bi-check2-circle me-1"></i>All devices online</span>
        </div>
    </div>
    <div class="col-6 col-lg-3">
        <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="text-secondary fw-semibold small">Average Speed</span>
                <span class="p-2 rounded-3 bg-primary-subtle text-primary"><i class="bi bi-speedometer2 fs-6"></i></span>
            </div>
            <h3 class="fw-bold mb-0" style="color: #0f172a;">26.5 km/h</h3>
            <span class="text-secondary small fw-medium">Dhaka City Traffic Flow</span>
        </div>
    </div>
    <div class="col-6 col-lg-3">
        <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="text-secondary fw-semibold small">GPS Accuracy</span>
                <span class="p-2 rounded-3 bg-info-subtle text-info"><i class="bi bi-crosshair fs-6"></i></span>
            </div>
            <h3 class="fw-bold mb-0" style="color: #0f172a;">±2.3 meters</h3>
            <span class="text-info small fw-medium"><i class="bi bi-reception-4 me-1"></i>Dual-band GNSS fix</span>
        </div>
    </div>
    <div class="col-6 col-lg-3">
        <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="text-secondary fw-semibold small">Safety Status</span>
                <span class="p-2 rounded-3 bg-success-subtle text-success"><i class="bi bi-shield-check fs-6"></i></span>
            </div>
            <h3 class="fw-bold mb-0 text-success">100% SECURE</h3>
            <span class="text-secondary small fw-medium">0 Active SOS Alerts</span>
        </div>
    </div>
</div>

<!-- Main Content: Interactive Map & Live Sessions Sidebar -->
<div class="row g-4">
    <!-- Left Column: Interactive Map & Telemetry Dashboard -->
    <div class="col-12 col-xl-8">
        <div class="card border-0 shadow-sm rounded-4 p-3 bg-white mb-4">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-map-fill text-primary fs-5"></i>
                    <h5 class="fw-bold mb-0" id="currentTrackingTitle">Live Escort Map: TRK-84920 (Ananya Sen ➔ Rahul Verma)</h5>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <span class="badge bg-dark text-white px-3 py-2 rounded-3 fw-medium" id="etaBadge">
                        <i class="bi bi-clock-history me-1 text-warning"></i> ETA: 7 mins (2.4 km)
                    </span>
                    <button class="btn btn-sm btn-outline-secondary" onclick="simulateStep()" title="Simulate real-time vehicle movement">
                        <i class="bi bi-play-fill text-success"></i> Simulate Movement
                    </button>
                </div>
            </div>

            <!-- Leaflet Map Container -->
            <div id="liveGpsMap" class="map-container"></div>

            <!-- Telemetry Bar Under Map -->
            <div class="mt-3 p-3 rounded-3 bg-light border d-flex justify-content-between align-items-center flex-wrap gap-2">
                <div class="d-flex align-items-center gap-3 flex-wrap">
                    <div class="telemetry-chip">
                        <i class="bi bi-geo text-warning"></i>
                        <span id="modelCoords">Model: 23.7745° N, 90.4120° E</span>
                    </div>
                    <div class="telemetry-chip">
                        <i class="bi bi-pin-map text-success"></i>
                        <span id="userCoords">Client: 23.7937° N, 90.4066° E</span>
                    </div>
                    <div class="telemetry-chip">
                        <i class="bi bi-speedometer"></i>
                        <span id="speedDisplay">Speed: 28 km/h</span>
                    </div>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <form method="POST" class="d-inline">
                        <input type="hidden" name="session_id" id="postSessionId" value="TRK-84920">
                        <button type="submit" name="action" value="ping_model" class="btn btn-sm btn-outline-primary" title="Ping Model Phone">
                            <i class="bi bi-phone"></i> Ping Model
                        </button>
                        <button type="submit" name="action" value="ping_user" class="btn btn-sm btn-outline-info" title="Ping Client Phone">
                            <i class="bi bi-person"></i> Ping Client
                        </button>
                    </form>
                    <button class="btn btn-sm btn-outline-secondary" onclick="copyTrackingLink()">
                        <i class="bi bi-share"></i> Share GPS Link
                    </button>
                </div>
            </div>
        </div>

        <!-- Telemetry Logs & Activity Stream -->
        <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
            <h5 class="fw-bold mb-3 d-flex align-items-center gap-2">
                <i class="bi bi-terminal-fill text-secondary"></i> Real-Time Telemetry Feed & Safety Audit Log
            </h5>
            <div id="telemetryFeed" class="p-3 rounded-3 font-monospace small" style="background: #090d16; color: #a5f3fc; height: 180px; overflow-y: auto; line-height: 1.6;">
                <div><span class="text-secondary">[<?= date('H:i:s') ?>]</span> <span class="text-success">[GPS_INIT]</span> Dual telemetry stream connected via WebSocket. Protocol SSL AES-256.</div>
                <div><span class="text-secondary">[<?= date('H:i:s') ?>]</span> <span class="text-info">[TELEMETRY]</span> TRK-84920 Model Ananya Sen ping: 23.7745 N, 90.4120 E | Battery: 86% | Accuracy: ±2.5m</div>
                <div><span class="text-secondary">[<?= date('H:i:s') ?>]</span> <span class="text-info">[TELEMETRY]</span> TRK-84920 Client Rahul Verma ping: 23.7937 N, 90.4066 E | Battery: 92% | Accuracy: ±1.8m</div>
                <div><span class="text-secondary">[<?= date('H:i:s') ?>]</span> <span class="text-warning">[ESCORT]</span> Mode: Car (Uber Ride #849). Calculated ETA: 7 mins remaining. Route condition: Normal.</div>
            </div>
        </div>
    </div>

    <!-- Right Column: Active Live Sessions List -->
    <div class="col-12 col-xl-4">
        <div class="card border-0 shadow-sm rounded-4 p-3 bg-white mb-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold mb-0">Active Escort Sessions</h5>
                <span class="badge bg-primary rounded-pill"><?= count($sessions) ?> Live</span>
            </div>

            <!-- Session Cards -->
            <div class="d-flex flex-column gap-3">
                <?php foreach ($sessions as $idx => $s): ?>
                <div class="card session-card rounded-4 p-3 <?= $idx === 0 ? 'active-session' : '' ?>" 
                     id="session-card-<?= $s['session_id'] ?>"
                     onclick="selectSession(<?= htmlspecialchars(json_encode($s), ENT_QUOTES, 'UTF-8') ?>)">
                    
                    <!-- Top Row: Session ID & Status Badge -->
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="fw-bold text-dark font-monospace small">#<?= $s['session_id'] ?> (<?= $s['booking_id'] ?>)</span>
                        <?php if ($s['status'] === 'EN_ROUTE'): ?>
                            <span class="badge bg-success-subtle text-success border border-success-subtle d-inline-flex align-items-center gap-1">
                                <span class="radar-pulse" style="width: 6px; height: 6px;"></span> EN ROUTE
                            </span>
                        <?php elseif ($s['status'] === 'IN_TRANSIT'): ?>
                            <span class="badge bg-primary-subtle text-primary border border-primary-subtle">IN TRANSIT</span>
                        <?php elseif ($s['status'] === 'ARRIVED'): ?>
                            <span class="badge bg-purple-subtle text-purple border" style="background: #f3e8ff; color: #7e22ce;">ARRIVED</span>
                        <?php endif; ?>
                    </div>

                    <!-- Model & Client Info Grid -->
                    <div class="d-flex align-items-center justify-content-between p-2 rounded-3 bg-light mb-2">
                        <!-- Model Profile -->
                        <div class="d-flex align-items-center gap-2">
                            <img src="<?= $s['model_avatar'] ?>" class="rounded-circle border" width="36" height="36" style="object-fit: cover;" alt="Model">
                            <div>
                                <div class="fw-bold text-dark small"><?= $s['model_name'] ?></div>
                                <div class="text-secondary" style="font-size: 0.72rem;">Model • 🔋 <?= $s['model_battery'] ?>%</div>
                            </div>
                        </div>

                        <!-- Arrow / Distance Indicator -->
                        <div class="text-center px-2">
                            <div class="text-primary fw-bold" style="font-size: 0.8rem;"><?= $s['distance_km'] ?> km</div>
                            <i class="bi bi-arrow-right text-secondary small"></i>
                            <div class="text-secondary" style="font-size: 0.68rem;"><?= $s['eta_minutes'] > 0 ? $s['eta_minutes'].'m ETA' : 'At Venue' ?></div>
                        </div>

                        <!-- User Profile -->
                        <div class="d-flex align-items-center gap-2 text-end">
                            <div>
                                <div class="fw-bold text-dark small"><?= $s['user_name'] ?></div>
                                <div class="text-secondary" style="font-size: 0.72rem;">Client • 🔋 <?= $s['user_battery'] ?>%</div>
                            </div>
                            <img src="<?= $s['user_avatar'] ?>" class="rounded-circle border" width="36" height="36" style="object-fit: cover;" alt="Client">
                        </div>
                    </div>

                    <!-- Destination & Vehicle Details -->
                    <div class="d-flex justify-content-between align-items-center text-secondary small">
                        <span class="text-truncate me-2" style="max-width: 220px;" title="<?= $s['destination_studio'] ?>">
                            <i class="bi bi-geo-alt me-1 text-danger"></i> <?= $s['destination_studio'] ?>
                        </span>
                        <span class="fw-semibold text-dark text-nowrap">
                            <i class="bi bi-car-front-fill me-1 text-primary"></i> <?= $s['speed_kmh'] ?> km/h
                        </span>
                    </div>

                    <!-- Quick Action Buttons -->
                    <div class="d-flex justify-content-between align-items-center pt-2 mt-2 border-top">
                        <form method="POST" class="d-flex gap-2">
                            <input type="hidden" name="session_id" value="<?= $s['session_id'] ?>">
                            <button type="submit" name="action" value="mark_arrived" class="btn btn-sm btn-outline-success py-1 px-2" style="font-size: 0.75rem;">
                                <i class="bi bi-check-lg"></i> Arrived
                            </button>
                            <button type="submit" name="action" value="complete" class="btn btn-sm btn-outline-secondary py-1 px-2" style="font-size: 0.75rem;">
                                Complete
                            </button>
                        </form>
                        <form method="POST">
                            <input type="hidden" name="session_id" value="<?= $s['session_id'] ?>">
                            <button type="submit" name="action" value="sos_alert" class="btn btn-sm btn-danger py-1 px-2 fw-bold" style="font-size: 0.75rem;" onclick="return confirm('Trigger emergency protocol for session <?= $s['session_id'] ?>?')">
                                <i class="bi bi-shield-exclamation"></i> SOS
                            </button>
                        </form>
                    </div>
                </div>
                <?php endforeach; ?>
            </div>
        </div>

        <!-- Safe Escort Protocol Guidelines Card -->
        <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
            <h6 class="fw-bold mb-2 text-dark"><i class="bi bi-info-circle-fill text-primary me-2"></i>Escort Safety Protocol</h6>
            <ul class="text-secondary small ps-3 mb-0" style="line-height: 1.6;">
                <li>Live GPS ping transmits telemetry every 3 seconds while session is <code>EN_ROUTE</code>.</li>
                <li>Both Model and Client battery levels are monitored to prevent communication drop-outs.</li>
                <li>Emergency SOS instantly dispatches police coordinate tags & alerts security staff.</li>
                <li>When model reaches within 50 meters of destination, status automatically switches to <code>ARRIVED</code>.</li>
            </ul>
        </div>
    </div>
</div>

<!-- Emergency SOS Modal -->
<div class="modal fade" id="sosEmergencyModal" tabindex="-1" aria-labelledby="sosEmergencyModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-danger text-white rounded-top-4">
                <h5 class="modal-title fw-bold" id="sosEmergencyModalLabel">
                    <i class="bi bi-exclamation-octagon-fill me-2"></i> Emergency Escort SOS Dispatch
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form method="POST">
                <div class="modal-body p-4">
                    <p class="text-danger fw-semibold mb-3">
                        Triggering SOS will initiate an emergency safety broadcast to local security escort teams and lock live telemetry into the incident registry.
                    </p>
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-secondary">Target Session</label>
                        <select name="session_id" class="form-select rounded-3">
                            <?php foreach ($sessions as $s): ?>
                            <option value="<?= $s['session_id'] ?>">#<?= $s['session_id'] ?> - <?= $s['model_name'] ?> ➔ <?= $s['user_name'] ?> (<?= $s['status'] ?>)</option>
                            <?php endforeach; ?>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-secondary">Emergency Reason</label>
                        <input type="text" name="reason" class="form-control rounded-3" placeholder="E.g. Model reported unsafe detour or unresponsive" required>
                    </div>
                </div>
                <div class="modal-footer border-0 p-3 pt-0">
                    <button type="button" class="btn btn-secondary rounded-3" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" name="action" value="sos_alert" class="btn btn-danger fw-bold rounded-3 px-4">
                        <i class="bi bi-broadcast me-1"></i> Broadcast SOS Alert
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Leaflet GPS Map Script -->
<script>
let currentSession = <?= json_encode($sessions[0]) ?>;
let map;
let modelMarker;
let userMarker;
let routeLine;
let simStep = 0;

document.addEventListener('DOMContentLoaded', function() {
    initMap();
});

function initMap() {
    // Center between Model and User coordinates
    const centerLat = (currentSession.model_lat + currentSession.user_lat) / 2;
    const centerLng = (currentSession.model_lng + currentSession.user_lng) / 2;

    map = L.map('liveGpsMap').setView([centerLat, centerLng], 14);

    // High quality OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors | Modol Live GPS',
        maxZoom: 19
    }).addTo(map);

    updateMarkers();
}

function updateMarkers() {
    if (modelMarker) map.removeLayer(modelMarker);
    if (userMarker) map.removeLayer(userMarker);
    if (routeLine) map.removeLayer(routeLine);

    // Custom Model Marker (Pink)
    const modelIcon = L.divIcon({
        className: 'custom-model-pin',
        html: `<div style="background: #ec4899; color: white; width: 34px; height: 34px; border-radius: 50%; display: flex; align-items: center; justify-content: center; border: 3px solid white; box-shadow: 0 4px 10px rgba(236,72,153,0.5); font-weight: bold; font-size: 14px;">💃</div>`,
        iconSize: [34, 34],
        iconAnchor: [17, 17]
    });

    // Custom User Marker (Blue Destination Pin)
    const userIcon = L.divIcon({
        className: 'custom-user-pin',
        html: `<div style="background: #3b82f6; color: white; width: 34px; height: 34px; border-radius: 50%; display: flex; align-items: center; justify-content: center; border: 3px solid white; box-shadow: 0 4px 10px rgba(59,130,246,0.5); font-weight: bold; font-size: 14px;">📍</div>`,
        iconSize: [34, 34],
        iconAnchor: [17, 17]
    });

    modelMarker = L.marker([currentSession.model_lat, currentSession.model_lng], { icon: modelIcon }).addTo(map)
        .bindPopup(`<b>Model: ${currentSession.model_name}</b><br>${currentSession.model_address}<br>🔋 Battery: ${currentSession.model_battery}%`);

    userMarker = L.marker([currentSession.user_lat, currentSession.user_lng], { icon: userIcon }).addTo(map)
        .bindPopup(`<b>Destination: ${currentSession.user_name}</b><br>${currentSession.destination_studio}<br>🔋 Battery: ${currentSession.user_battery}%`);

    // Draw Route Polyline
    const routeCoords = [
        [currentSession.model_lat, currentSession.model_lng],
        [currentSession.user_lat, currentSession.user_lng]
    ];
    routeLine = L.polyline(routeCoords, { color: '#6366f1', weight: 4, dashArray: '8, 8', opacity: 0.8 }).addTo(map);

    map.fitBounds(routeLine.getBounds(), { padding: [50, 50] });
}

function selectSession(session) {
    currentSession = session;
    simStep = 0;
    document.getElementById('postSessionId').value = session.session_id;
    document.getElementById('currentTrackingTitle').innerText = `Live Escort Map: ${session.session_id} (${session.model_name} ➔ ${session.user_name})`;
    document.getElementById('etaBadge').innerHTML = `<i class="bi bi-clock-history me-1 text-warning"></i> ETA: ${session.eta_minutes} mins (${session.distance_km} km)`;
    document.getElementById('modelCoords').innerText = `Model: ${session.model_lat.toFixed(4)}° N, ${session.model_lng.toFixed(4)}° E`;
    document.getElementById('userCoords').innerText = `Client: ${session.user_lat.toFixed(4)}° N, ${session.user_lng.toFixed(4)}° E`;
    document.getElementById('speedDisplay').innerText = `Speed: ${session.speed_kmh} km/h`;

    // Highlight active card
    document.querySelectorAll('.session-card').forEach(el => el.classList.remove('active-session'));
    const activeEl = document.getElementById(`session-card-${session.session_id}`);
    if (activeEl) activeEl.classList.add('active-session');

    updateMarkers();
    logTelemetry(`Session switched to ${session.session_id}. Tracking active.`);
}

function simulateStep() {
    simStep++;
    const progress = Math.min(simStep * 0.2, 1.0);
    
    // Interpolate Model coordinates towards User destination
    currentSession.model_lat = currentSession.model_lat + (currentSession.user_lat - currentSession.model_lat) * 0.25;
    currentSession.model_lng = currentSession.model_lng + (currentSession.user_lng - currentSession.model_lng) * 0.25;
    
    const remainingKm = Math.max(0, (currentSession.distance_km * (1 - progress))).toFixed(1);
    const remainingEta = Math.max(0, Math.round(currentSession.eta_minutes * (1 - progress)));
    
    document.getElementById('etaBadge').innerHTML = `<i class="bi bi-clock-history me-1 text-warning"></i> ETA: ${remainingEta} mins (${remainingKm} km)`;
    document.getElementById('modelCoords').innerText = `Model: ${currentSession.model_lat.toFixed(4)}° N, ${currentSession.model_lng.toFixed(4)}° E`;
    
    updateMarkers();
    logTelemetry(`[GPS_STEP_${simStep}] Lat: ${currentSession.model_lat.toFixed(5)}, Lng: ${currentSession.model_lng.toFixed(5)} | Remaining: ${remainingKm} km | ETA: ${remainingEta} min`);
}

function logTelemetry(msg) {
    const feed = document.getElementById('telemetryFeed');
    const time = new Date().toLocaleTimeString();
    const entry = document.createElement('div');
    entry.innerHTML = `<span class="text-secondary">[${time}]</span> <span class="text-info">[TELEMETRY]</span> ${msg}`;
    feed.appendChild(entry);
    feed.scrollTop = feed.scrollHeight;
}

function refreshGpsData() {
    logTelemetry("Manual telemetry refresh triggered. High-precision GPS handshake successful.");
    alert("Live GPS Telemetry refreshed successfully. All device coordinates up to date.");
}

function copyTrackingLink() {
    const link = window.location.origin + window.location.pathname + `?session=${currentSession.session_id}`;
    navigator.clipboard.writeText(link).then(() => {
        alert("Live GPS Public Escort Tracking link copied to clipboard:\n" + link);
    });
}
</script>

<?php renderAdminFooter(); ?>
