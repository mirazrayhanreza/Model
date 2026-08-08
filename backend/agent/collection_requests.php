<?php
// backend/agent/collection_requests.php
// Cash Agent - New Collection Request & Accept/Reject
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();
$agentId = $_SESSION['user_id'] ?? 1;
$db = Database::getInstance();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $bookingId = sanitizeInput($_POST['booking_id'] ?? '');
    $clientEmail = sanitizeInput($_POST['client_email'] ?? '');
    $amount = (float)($_POST['amount'] ?? 0.0);
    $receipt = $_FILES['receipt_photo']['name'] ?? 'receipt_scanned.jpg';

    $code = 'CC' . rand(10000, 99999);
    $stmt = $db->prepare("INSERT INTO cash_collections (collection_code, booking_id, agent_id, client_email, amount, status, receipt_photo_url) VALUES (?, ?, ?, ?, ?, 'PENDING', ?)");
    $stmt->execute([$code, $bookingId, $agentId, $clientEmail, $amount, "uploads/receipts/" . $receipt]);

    sendJsonResponse('success', 'Cash collection recorded and sent for Admin verification', ['collection_code' => $code]);
}

$pendingRequests = [
    ['booking_id' => 'BK89562', 'client_name' => 'Al Amin', 'amount' => 3500.00, 'location' => 'Dhanmondi, Dhaka', 'status' => 'READY_TO_COLLECT'],
    ['booking_id' => 'BK89561', 'client_name' => 'Rashid Khan', 'amount' => 4000.00, 'location' => 'Gulshan 2, Dhaka', 'status' => 'READY_TO_COLLECT']
];

sendJsonResponse('success', 'Pending cash collection tasks retrieved', ['requests' => $pendingRequests]);
?>
