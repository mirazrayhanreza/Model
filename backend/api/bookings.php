<?php
declare(strict_types=1);

// backend/api/bookings.php
// REST API Endpoint - Booking Lifecycle & Proof Verification (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../enums/BookingStatus.php';
require_once __DIR__ . '/../enums/PaymentStatus.php';

use App\Enums\BookingStatus;
use App\Enums\PaymentStatus;

$db = Database::getInstance();
$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

if ($method === 'GET') {
    $userId = filter_input(INPUT_GET, 'user_id', FILTER_DEFAULT);
    $status = filter_input(INPUT_GET, 'status', FILTER_DEFAULT);

    $sql = "SELECT * FROM bookings WHERE 1=1";
    $params = [];

    if ($userId) {
        $sql .= " AND user_id = ?";
        $params[] = $userId;
    }
    if ($status) {
        $sql .= " AND status = ?";
        $params[] = $status;
    }
    $sql .= " ORDER BY id DESC";

    $stmt = $db->prepare($sql);
    $stmt->execute($params);
    $bookings = $stmt->fetchAll(PDO::FETCH_ASSOC);

    sendJsonResponse('success', 'Bookings fetched', ['bookings' => $bookings]);
}

if ($method === 'POST') {
    $action = $input['action'] ?? 'CREATE';

    match ($action) {
        'CREATE' => handleCreateBooking($db, $input),
        'SUBMIT_PROOF' => handleSubmitProof($db, $input),
        'CONFIRM_COMPLETION' => handleConfirmCompletion($db, $input),
        'RAISE_DISPUTE' => handleRaiseDispute($db, $input),
        default => sendJsonResponse('error', 'Invalid booking action', [], 400),
    };
}

function handleCreateBooking(PDO $db, array $input): never
{
    $userId = sanitizeInput($input['user_id'] ?? '');
    $modelId = (int)($input['model_id'] ?? 0);
    $modelName = sanitizeInput($input['model_name'] ?? '');
    $date = sanitizeInput($input['date'] ?? date('Y-m-d'));
    $time = sanitizeInput($input['time'] ?? '10:00 AM');
    $serviceType = sanitizeInput($input['service_type'] ?? 'Photo Shoot');
    $durationHours = (int)($input['duration_hours'] ?? 2);
    $location = sanitizeInput($input['location'] ?? 'Dhaka');
    $notes = sanitizeInput($input['notes'] ?? '');
    $totalPrice = (float)($input['total_price'] ?? 0.0);
    $paymentMethod = sanitizeInput($input['payment_method'] ?? 'WALLET');

    if (empty($userId) || $modelId === 0 || $totalPrice <= 0) {
        sendJsonResponse('error', 'Required booking parameters missing', [], 400);
    }

    $platformFee = $totalPrice * (Config::ADMIN_PLATFORM_FEE_PERCENT / 100.0);
    $modelEarnings = $totalPrice - $platformFee;

    $stmt = $db->prepare("INSERT INTO bookings (
        user_id, model_id, model_name, model_photo, date, time, service_type,
        duration_hours, location, notes, total_price, status, payment_status,
        payment_method, model_earnings, platform_fee, timestamp
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

    $stmt->execute([
        $userId, $modelId, $modelName, 'model_' . $modelId, $date, $time, $serviceType,
        $durationHours, $location, $notes, $totalPrice,
        BookingStatus::PAYMENT_RECEIVED->value,
        PaymentStatus::ESCROW_HELD->value,
        $paymentMethod, $modelEarnings, $platformFee, (int)(microtime(true) * 1000)
    ]);

    $newId = (int)$db->lastInsertId();

    sendJsonResponse('success', 'Booking created and held in Escrow safely', [
        'booking_id' => $newId,
        'status' => BookingStatus::PAYMENT_RECEIVED->value,
        'escrow_amount' => $totalPrice
    ], 201);
}

function handleSubmitProof(PDO $db, array $input): never
{
    $bookingId = (int)($input['booking_id'] ?? 0);
    $selfieUrl = sanitizeInput($input['proof_selfie_url'] ?? '');
    $photos = sanitizeInput($input['proof_photos'] ?? '');
    $gps = sanitizeInput($input['proof_gps_location'] ?? '');
    $notes = sanitizeInput($input['proof_notes'] ?? '');

    if ($bookingId <= 0) {
        sendJsonResponse('error', 'Invalid booking ID', [], 400);
    }

    $stmt = $db->prepare("UPDATE bookings SET
        status = ?,
        proof_selfie_url = ?,
        proof_photos = ?,
        proof_gps_location = ?,
        proof_notes = ?,
        proof_submitted_time = ?
        WHERE id = ?");

    $stmt->execute([
        BookingStatus::PROOF_UPLOADED->value,
        $selfieUrl,
        $photos,
        $gps,
        $notes,
        (int)(microtime(true) * 1000),
        $bookingId
    ]);

    sendJsonResponse('success', 'Work proof uploaded successfully for client confirmation', [
        'booking_id' => $bookingId,
        'status' => BookingStatus::PROOF_UPLOADED->value
    ]);
}

function handleConfirmCompletion(PDO $db, array $input): never
{
    $bookingId = (int)($input['booking_id'] ?? 0);
    $rating = (int)($input['rating'] ?? 5);
    $feedback = sanitizeInput($input['feedback'] ?? '');

    $stmt = $db->prepare("UPDATE bookings SET
        status = ?,
        payment_status = ?,
        user_rating = ?,
        user_feedback = ?
        WHERE id = ?");

    $stmt->execute([
        BookingStatus::COMPLETED->value,
        PaymentStatus::RELEASED->value,
        $rating,
        $feedback,
        $bookingId
    ]);

    sendJsonResponse('success', 'Booking finalized and funds released to Model wallet', [
        'booking_id' => $bookingId,
        'status' => BookingStatus::COMPLETED->value
    ]);
}

function handleRaiseDispute(PDO $db, array $input): never
{
    $bookingId = (int)($input['booking_id'] ?? 0);
    $reason = sanitizeInput($input['dispute_reason'] ?? 'Service not fulfilled according to terms');

    $stmt = $db->prepare("UPDATE bookings SET
        status = ?,
        dispute_status = 'RAISED',
        dispute_reason = ?
        WHERE id = ?");

    $stmt->execute([BookingStatus::ADMIN_REVIEW->value, $reason, $bookingId]);

    sendJsonResponse('success', 'Dispute reported. Admin review initiated.', [
        'booking_id' => $bookingId,
        'dispute_status' => 'RAISED'
    ]);
}
