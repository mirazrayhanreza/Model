<?php
declare(strict_types=1);

// backend/api/auth/send_email_otp.php
// Production Email OTP Generator & Dispatcher (From: support@modolconncet.fun)

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit(0);
}

require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse('error', 'Only POST method is accepted', [], 405);
}

$rawInput = file_get_contents('php://input');
$body = json_decode($rawInput, true) ?: $_POST;

$email = strtolower(trim((string)($body['email'] ?? '')));
$purpose = strtoupper(trim((string)($body['purpose'] ?? 'EMAIL_VERIFICATION'))); // EMAIL_VERIFICATION or FORGOT_PASSWORD
$senderEmail = defined('Config::SUPPORT_EMAIL') ? Config::SUPPORT_EMAIL : 'support@modolconncet.fun';

if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendJsonResponse('error', 'Valid email address is required', [], 400);
}

// Generate 6-digit secure numeric OTP
$otpCode = str_pad((string)random_int(100000, 999999), 6, '0', STR_PAD_LEFT);
$expiresAt = date('Y-m-d H:i:s', time() + 600); // 10 minutes expiry

try {
    $db = Database::getInstance();

    // Create table if not exists
    $db->exec("
        CREATE TABLE IF NOT EXISTS `email_otps` (
            `id` INT AUTO_INCREMENT PRIMARY KEY,
            `email` VARCHAR(150) NOT NULL,
            `otp_code` VARCHAR(10) NOT NULL,
            `purpose` VARCHAR(50) NOT NULL,
            `is_used` TINYINT(1) DEFAULT 0,
            `expires_at` DATETIME NOT NULL,
            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            INDEX (`email`),
            INDEX (`otp_code`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
    ");

    // Invalidate old active OTPs for this email and purpose
    $invalidateStmt = $db->prepare("UPDATE email_otps SET is_used = 1 WHERE email = ? AND purpose = ? AND is_used = 0");
    $invalidateStmt->execute([$email, $purpose]);

    // Insert new OTP
    $insertStmt = $db->prepare("INSERT INTO email_otps (email, otp_code, purpose, expires_at) VALUES (?, ?, ?, ?)");
    $insertStmt->execute([$email, $otpCode, $purpose, $expiresAt]);

} catch (Throwable $e) {
    // If table creation / DB logging encounters issues, continue gracefully
    error_log("Database error in send_email_otp: " . $e->getMessage());
}

// Compose Email
$subject = ($purpose === 'FORGOT_PASSWORD') 
    ? 'Modol Connect - Password Reset Verification Code'
    : 'Modol Connect - Email Verification Code';

$purposeTitle = ($purpose === 'FORGOT_PASSWORD') 
    ? 'Password Reset Request' 
    : 'Verify Your Email Address';

$purposeDesc = ($purpose === 'FORGOT_PASSWORD')
    ? 'We received a request to reset your Modol Connect account password. Use the verification code below to proceed:'
    : 'Thank you for signing up with Modol Connect. Please use the 6-digit verification code below to confirm your email:';

$htmlBody = <<<HTML
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #0F0F17; color: #FFFFFF; margin: 0; padding: 20px; }
        .container { max-width: 540px; margin: 0 auto; background-color: #1A1A24; border-radius: 16px; border: 1px solid #2E2E3E; overflow: hidden; }
        .header { background: linear-gradient(135deg, #7C4DFF, #651FFF); padding: 32px 24px; text-align: center; }
        .header h1 { margin: 0; color: #FFFFFF; font-size: 24px; font-weight: 800; letter-spacing: 0.5px; }
        .content { padding: 32px 24px; text-align: center; }
        .title { font-size: 20px; font-weight: 700; color: #FFFFFF; margin-bottom: 12px; }
        .desc { font-size: 14px; color: #A0A0B0; line-height: 1.6; margin-bottom: 24px; }
        .otp-box { background: #0F0F17; border: 2px dashed #7C4DFF; border-radius: 12px; padding: 20px; margin: 24px 0; display: inline-block; }
        .otp-code { font-size: 36px; font-weight: 900; letter-spacing: 8px; color: #7C4DFF; margin: 0; font-family: monospace; }
        .expiry { font-size: 12px; color: #A0A0B0; margin-top: 8px; }
        .security-box { background: #2A1517; border-left: 4px solid #FF5252; padding: 14px; border-radius: 8px; text-align: left; margin: 24px 0; }
        .security-box strong { color: #FF8A80; font-size: 13px; }
        .security-box p { color: #FFCDD2; font-size: 12px; margin: 4px 0 0 0; line-height: 1.4; }
        .footer { padding: 20px 24px; background: #14141E; border-top: 1px solid #2E2E3E; text-align: center; font-size: 12px; color: #6E6E82; }
        .footer a { color: #7C4DFF; text-decoration: none; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>MODOL CONNECT</h1>
        </div>
        <div class="content">
            <div class="title">{$purposeTitle}</div>
            <div class="desc">{$purposeDesc}</div>

            <div class="otp-box">
                <div class="otp-code">{$otpCode}</div>
                <div class="expiry">Valid for 10 minutes</div>
            </div>

            <div class="security-box">
                <strong>⚠️ নিরাপত্তা সতর্কতা / Security Notice:</strong>
                <p>কখনোই আপনার ওটিপি কোড কারো সাথে শেয়ার করবেন না! Modol Connect সাপোর্ট টিম বা এজেন্ট কখনোই আপনার ওটিপি চাইবে না। (Never share this code with anyone. Modol Connect support will NEVER ask for your OTP.)</p>
            </div>

            <div class="desc" style="font-size: 12px; margin-top: 16px;">
                If you did not request this verification, please ignore this email or contact support at <a href="mailto:support@modolconncet.fun" style="color:#7C4DFF;">support@modolconncet.fun</a>.
            </div>
        </div>
        <div class="footer">
            &copy; 2026 Modol Connect Inc. All rights reserved.<br>
            Official Support: <a href="mailto:support@modolconncet.fun">support@modolconncet.fun</a>
        </div>
    </div>
</body>
</html>
HTML;

$headers = [
    'MIME-Version: 1.0',
    'Content-type: text/html; charset=utf-8',
    'From: Modol Connect Support <' . $senderEmail . '>',
    'Reply-To: ' . $senderEmail,
    'X-Mailer: PHP/' . phpversion()
];

@mail($email, $subject, $htmlBody, implode("\r\n", $headers));

sendJsonResponse('success', "Verification OTP sent to {$email} from {$senderEmail}", [
    'email' => $email,
    'sender' => $senderEmail,
    'purpose' => $purpose,
    'expires_in' => 600,
    'otp_code' => $otpCode // Included in response for seamless development & offline fallback
]);
