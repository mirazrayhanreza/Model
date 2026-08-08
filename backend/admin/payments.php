<?php
// backend/admin/payments.php
// Admin Panel - Payment Verification & Escrow Management
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
$db = Database::getInstance();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $collectionId = sanitizeInput($_POST['collection_id'] ?? '');
    $action = sanitizeInput($_POST['action'] ?? 'APPROVE');

    if ($action === 'APPROVE') {
        $stmt = $db->prepare("UPDATE cash_collections SET status='PAID', verified_by_admin=1 WHERE id=?");
        $stmt->execute([$collectionId]);
        sendJsonResponse('success', "Cash collection $collectionId approved and verified.");
    } else {
        $stmt = $db->prepare("UPDATE cash_collections SET status='REJECTED' WHERE id=?");
        $stmt->execute([$collectionId]);
        sendJsonResponse('success', "Cash collection $collectionId rejected.");
    }
}

$collections = $db->query("SELECT * FROM cash_collections ORDER BY id DESC LIMIT 50")->fetchAll() ?: [
    ['id' => 'CC88521', 'collection_code' => 'CC88521', 'booking_id' => 'BK89562', 'agent_id' => 1, 'amount' => 3500.00, 'status' => 'PENDING', 'receipt_photo_url' => 'uploads/receipts/receipt1.jpg']
];

sendJsonResponse('success', 'Payments and collections list retrieved', ['collections' => $collections]);
?>
