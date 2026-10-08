<?php
declare(strict_types=1);

// backend/config/mailer.php
// Production SMTP & Native Mailer Engine for Modol Connect
// Handles Authenticated SMTP (SSL/TLS) with fallback to mail() and diagnostics

require_once __DIR__ . '/config.php';

final class Mailer
{
    private static string $logFile = __DIR__ . '/../mail.log';

    public static function getSettings(): array
    {
        $default = [
            'smtp_enabled' => false,
            'smtp_host' => 'mail.modolconncet.fun',
            'smtp_port' => 465,
            'smtp_encryption' => 'ssl', // 'ssl' (port 465) or 'tls' (port 587)
            'smtp_username' => 'support@modolconncet.fun',
            'smtp_password' => '',
            'smtp_from_email' => 'support@modolconncet.fun',
            'smtp_from_name' => 'Modol Connect Support',
            'support_email' => 'support@modolconncet.fun'
        ];

        $settingsFile = __DIR__ . '/app_settings.json';
        if (file_exists($settingsFile)) {
            $loaded = json_decode((string)file_get_contents($settingsFile), true);
            if (is_array($loaded)) {
                $default = array_merge($default, $loaded);
            }
        }

        return $default;
    }

    /**
     * Send HTML Email via SMTP or PHP mail()
     * 
     * @return array [success => bool, message => string, method => string, error => string|null]
     */
    public static function send(string $to, string $subject, string $htmlBody, ?string $fromEmail = null, ?string $fromName = null): array
    {
        $settings = self::getSettings();
        $from = $fromEmail ?: ($settings['smtp_from_email'] ?: 'support@modolconncet.fun');
        $name = $fromName ?: ($settings['smtp_from_name'] ?: 'Modol Connect Support');

        // 1. Try Authenticated SMTP if enabled and configured
        if (!empty($settings['smtp_enabled']) && !empty($settings['smtp_host']) && !empty($settings['smtp_password'])) {
            $smtpResult = self::sendViaSmtp(
                $to,
                $subject,
                $htmlBody,
                $from,
                $name,
                $settings
            );

            if ($smtpResult['success']) {
                self::log("SUCCESS (SMTP): Sent to {$to} [Subject: {$subject}]");
                return $smtpResult;
            }

            self::log("WARNING (SMTP Failed): " . ($smtpResult['error'] ?? 'Unknown error') . ". Falling back to PHP mail()...");
        }

        // 2. Fallback to PHP native mail()
        $headers = [
            'MIME-Version: 1.0',
            'Content-type: text/html; charset=utf-8',
            'From: ' . sprintf('=?UTF-8?B?%s?= <%s>', base64_encode($name), $from),
            'Reply-To: ' . $from,
            'X-Mailer: ModolConnect/PHP/' . phpversion()
        ];

        $mailSent = @mail($to, '=?UTF-8?B?' . base64_encode($subject) . '?=', $htmlBody, implode("\r\n", $headers));

        if ($mailSent) {
            self::log("SUCCESS (PHP mail): Sent to {$to} [Subject: {$subject}]");
            return [
                'success' => true,
                'method' => 'PHP_MAIL',
                'message' => "Email dispatched via PHP mail() to {$to}",
                'error' => null
            ];
        }

        $lastError = error_get_last()['message'] ?? 'PHP mail() returned false. Port 25 might be blocked by hosting provider, or Postfix/Sendmail is not running.';
        self::log("ERROR (PHP mail failed): {$lastError}");

        return [
            'success' => false,
            'method' => 'PHP_MAIL',
            'message' => 'Failed to dispatch email via PHP mail(). Please configure SMTP credentials in Admin Settings.',
            'error' => $lastError
        ];
    }

    /**
     * Pure PHP Socket SMTP Client (No Composer / PHPMailer dependencies required)
     */
    private static function sendViaSmtp(string $to, string $subject, string $htmlBody, string $from, string $fromName, array $settings): array
    {
        $host = (string)$settings['smtp_host'];
        $port = (int)($settings['smtp_port'] ?: 465);
        $encryption = strtolower((string)($settings['smtp_encryption'] ?: 'ssl'));
        $username = (string)$settings['smtp_username'];
        $password = (string)$settings['smtp_password'];

        $timeout = 15;
        $prefix = ($encryption === 'ssl') ? 'ssl://' : 'tcp://';
        $target = $prefix . $host . ':' . $port;

        $context = stream_context_create([
            'ssl' => [
                'verify_peer' => false,
                'verify_peer_name' => false,
                'allow_self_signed' => true
            ]
        ]);

        $socket = @stream_socket_client($target, $errno, $errstr, $timeout, STREAM_CLIENT_CONNECT, $context);
        if (!$socket) {
            return [
                'success' => false,
                'error' => "Cannot connect to SMTP server {$host}:{$port} ({$errno}: {$errstr})"
            ];
        }

        stream_set_timeout($socket, $timeout);

        $readResponse = function ($sock, $expectedCode = 250) {
            $response = '';
            while ($line = fgets($sock, 515)) {
                $response .= $line;
                if (substr($line, 3, 1) === ' ') {
                    break;
                }
            }
            $code = (int)substr($response, 0, 3);
            if (!empty($expectedCode) && $code !== $expectedCode) {
                return ['ok' => false, 'code' => $code, 'msg' => trim($response)];
            }
            return ['ok' => true, 'code' => $code, 'msg' => trim($response)];
        };

        // 1. Initial Greeting Banner
        $r = $readResponse($socket, 220);
        if (!$r['ok']) {
            fclose($socket);
            return ['success' => false, 'error' => "SMTP greeting error: " . $r['msg']];
        }

        // 2. EHLO
        fputs($socket, "EHLO " . ($_SERVER['SERVER_NAME'] ?? 'localhost') . "\r\n");
        $r = $readResponse($socket, 250);
        if (!$r['ok']) {
            fclose($socket);
            return ['success' => false, 'error' => "EHLO failed: " . $r['msg']];
        }

        // 3. STARTTLS if required
        if ($encryption === 'tls') {
            fputs($socket, "STARTTLS\r\n");
            $r = $readResponse($socket, 220);
            if (!$r['ok']) {
                fclose($socket);
                return ['success' => false, 'error' => "STARTTLS failed: " . $r['msg']];
            }
            $crypto = @stream_socket_enable_crypto($socket, true, STREAM_CRYPTO_METHOD_TLS_CLIENT);
            if (!$crypto) {
                fclose($socket);
                return ['success' => false, 'error' => "Failed to negotiate TLS encryption"];
            }
            fputs($socket, "EHLO " . ($_SERVER['SERVER_NAME'] ?? 'localhost') . "\r\n");
            $readResponse($socket, 250);
        }

        // 4. AUTH LOGIN
        fputs($socket, "AUTH LOGIN\r\n");
        $r = $readResponse($socket, 334);
        if (!$r['ok']) {
            fclose($socket);
            return ['success' => false, 'error' => "AUTH LOGIN command failed: " . $r['msg']];
        }

        // Send Username
        fputs($socket, base64_encode($username) . "\r\n");
        $r = $readResponse($socket, 334);
        if (!$r['ok']) {
            fclose($socket);
            return ['success' => false, 'error' => "SMTP Username rejected: " . $r['msg']];
        }

        // Send Password
        fputs($socket, base64_encode($password) . "\r\n");
        $r = $readResponse($socket, 235);
        if (!$r['ok']) {
            fclose($socket);
            return ['success' => false, 'error' => "SMTP Authentication failed (Incorrect Password): " . $r['msg']];
        }

        // 5. MAIL FROM
        fputs($socket, "MAIL FROM: <{$from}>\r\n");
        $r = $readResponse($socket, 250);
        if (!$r['ok']) {
            fclose($socket);
            return ['success' => false, 'error' => "MAIL FROM rejected: " . $r['msg']];
        }

        // 6. RCPT TO
        fputs($socket, "RCPT TO: <{$to}>\r\n");
        $r = $readResponse($socket, 250);
        if (!$r['ok']) {
            fclose($socket);
            return ['success' => false, 'error' => "RCPT TO rejected: " . $r['msg']];
        }

        // 7. DATA
        fputs($socket, "DATA\r\n");
        $r = $readResponse($socket, 354);
        if (!$r['ok']) {
            fclose($socket);
            return ['success' => false, 'error' => "DATA rejected: " . $r['msg']];
        }

        // 8. Headers & Body Payload
        $messageId = sprintf('<%s.%s@%s>', time(), bin2hex(random_bytes(6)), parse_url($settings['smtp_host'], PHP_URL_HOST) ?: 'modolconncet.fun');
        $date = date('r');
        $subjectEncoded = '=?UTF-8?B?' . base64_encode($subject) . '?=';
        $fromEncoded = sprintf('=?UTF-8?B?%s?= <%s>', base64_encode($fromName), $from);

        $payload = "Date: {$date}\r\n";
        $payload .= "From: {$fromEncoded}\r\n";
        $payload .= "To: <{$to}>\r\n";
        $payload .= "Subject: {$subjectEncoded}\r\n";
        $payload .= "Message-ID: {$messageId}\r\n";
        $payload .= "MIME-Version: 1.0\r\n";
        $payload .= "Content-Type: text/html; charset=UTF-8\r\n";
        $payload .= "Content-Transfer-Encoding: base64\r\n";
        $payload .= "X-Mailer: ModolConnect SMTP Client\r\n";
        $payload .= "\r\n";
        $payload .= chunk_split(base64_encode($htmlBody)) . "\r\n";
        $payload .= ".\r\n";

        fputs($socket, $payload);
        $r = $readResponse($socket, 250);

        fputs($socket, "QUIT\r\n");
        fclose($socket);

        if (!$r['ok']) {
            return ['success' => false, 'error' => "Message body transmission rejected: " . $r['msg']];
        }

        return [
            'success' => true,
            'method' => 'SMTP',
            'message' => "Email sent successfully via SMTP ({$host}) to {$to}",
            'error' => null
        ];
    }

    private static function log(string $msg): void
    {
        $entry = '[' . date('Y-m-d H:i:s') . '] ' . $msg . PHP_EOL;
        @file_put_contents(self::$logFile, $entry, FILE_APPEND);
        @error_log("Mailer: " . $msg);
    }
}
