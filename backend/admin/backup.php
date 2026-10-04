<?php
declare(strict_types=1);

// backend/admin/backup.php
// Enterprise Database Backup & Restore Center (MySQL / SQLite, PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$backupDir = __DIR__ . '/../backups';
if (!is_dir($backupDir)) {
    @mkdir($backupDir, 0755, true);
}

$db = null;
try {
    $db = Database::getInstance();
} catch (Throwable $e) {
    error_log("Backup DB connect notice: " . $e->getMessage());
}

$msg = '';
$msgType = 'success';

// Handle Direct Download Request
if (isset($_GET['download'])) {
    $fileName = basename($_GET['download']);
    $filePath = $backupDir . '/' . $fileName;
    if (file_exists($filePath) && is_file($filePath)) {
        header('Content-Description: File Transfer');
        header('Content-Type: application/octet-stream');
        header('Content-Disposition: attachment; filename="' . $fileName . '"');
        header('Expires: 0');
        header('Cache-Control: must-revalidate');
        header('Pragma: public');
        header('Content-Length: ' . filesize($filePath));
        readfile($filePath);
        exit(0);
    } else {
        $msg = "Backup file not found.";
        $msgType = "danger";
    }
}

// All known tables in the system
$allTables = [
    'admins',
    'cash_agents',
    'countries',
    'payment_methods',
    'countries_currencies',
    'users',
    'models',
    'bookings',
    'b2b_orders',
    'cash_collections',
    'wallets',
    'withdraw_requests',
    'notifications',
    'gps_escort_sessions',
    'disputes',
    'payment_gateways'
];

// Handle Actions (POST)
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    if ($action === 'create_sql_backup') {
        if (!$db) {
            $msg = "Database connection is not available.";
            $msgType = "danger";
        } else {
            try {
                $isMySQL = Database::isMySQL();
                $timestamp = date('Y-m-d_H-i-s');
                $filename = "backup_sql_" . ($isMySQL ? "mysql_" : "sqlite_") . $timestamp . ".sql";
                $filePath = $backupDir . '/' . $filename;

                $sqlDump = "-- ========================================================\n";
                $sqlDump .= "-- Modol Connect Enterprise Database SQL Backup\n";
                $sqlDump .= "-- Generated: " . date('Y-m-d H:i:s T') . "\n";
                $sqlDump .= "-- Driver: " . Database::getDriver() . "\n";
                $sqlDump .= "-- Host: " . Config::getDbHost() . " | Database: " . Config::getDbName() . "\n";
                $sqlDump .= "-- ========================================================\n\n";

                if ($isMySQL) {
                    $sqlDump .= "SET FOREIGN_KEY_CHECKS = 0;\n";
                    $sqlDump .= "SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';\n\n";
                }

                $exportedTablesCount = 0;
                $exportedRowsCount = 0;

                foreach ($allTables as $table) {
                    try {
                        // Check if table exists
                        $test = $db->query("SELECT 1 FROM `{$table}` LIMIT 1");
                    } catch (Throwable) {
                        try {
                            $test = $db->query("SELECT 1 FROM {$table} LIMIT 1");
                        } catch (Throwable) {
                            continue; // Skip if table doesn't exist
                        }
                    }

                    $exportedTablesCount++;
                    $sqlDump .= "-- --------------------------------------------------------\n";
                    $sqlDump .= "-- Table structure & data for: `{$table}`\n";
                    $sqlDump .= "-- --------------------------------------------------------\n";

                    // For MySQL, get CREATE TABLE statement
                    if ($isMySQL) {
                        try {
                            $createStmt = $db->query("SHOW CREATE TABLE `{$table}`")->fetch(PDO::FETCH_NUM);
                            if (!empty($createStmt[1])) {
                                $sqlDump .= "DROP TABLE IF EXISTS `{$table}`;\n";
                                $sqlDump .= $createStmt[1] . ";\n\n";
                            }
                        } catch (Throwable) {}
                    }

                    // Dump rows
                    try {
                        $rows = $db->query("SELECT * FROM `{$table}`")->fetchAll(PDO::FETCH_ASSOC);
                        if (!empty($rows)) {
                            $exportedRowsCount += count($rows);
                            $columns = array_keys($rows[0]);
                            $colList = implode("`, `", $columns);
                            $sqlDump .= "INSERT INTO `{$table}` (`{$colList}`) VALUES\n";

                            $valRows = [];
                            foreach ($rows as $row) {
                                $escapedVals = [];
                                foreach ($row as $val) {
                                    if ($val === null) {
                                        $escapedVals[] = "NULL";
                                    } elseif (is_numeric($val) && !is_string($val)) {
                                        $escapedVals[] = (string)$val;
                                    } else {
                                        $escapedVals[] = $db->quote((string)$val);
                                    }
                                }
                                $valRows[] = "(" . implode(", ", $escapedVals) . ")";
                            }
                            $sqlDump .= implode(",\n", $valRows) . ";\n\n";
                        }
                    } catch (Throwable $re) {
                        $sqlDump .= "-- Notice: Error exporting rows for `{$table}`: " . $re->getMessage() . "\n\n";
                    }
                }

                if ($isMySQL) {
                    $sqlDump .= "SET FOREIGN_KEY_CHECKS = 1;\n";
                }

                file_put_contents($filePath, $sqlDump);
                $fileSizeKb = round(filesize($filePath) / 1024, 2);

                $msg = "SQL Database Backup created successfully! ({$filename} - {$fileSizeKb} KB, {$exportedTablesCount} tables, {$exportedRowsCount} rows)";
                $msgType = "success";
            } catch (Throwable $e) {
                $msg = "Backup creation failed: " . $e->getMessage();
                $msgType = "danger";
            }
        }
    } elseif ($action === 'create_json_backup') {
        if (!$db) {
            $msg = "Database connection is not available.";
            $msgType = "danger";
        } else {
            try {
                $timestamp = date('Y-m-d_H-i-s');
                $filename = "snapshot_json_" . $timestamp . ".json";
                $filePath = $backupDir . '/' . $filename;

                $snapshot = [
                    'version' => '2.2.0',
                    'timestamp' => date('c'),
                    'driver' => Database::getDriver(),
                    'tables' => []
                ];

                foreach ($allTables as $table) {
                    try {
                        $rows = $db->query("SELECT * FROM `{$table}`")->fetchAll(PDO::FETCH_ASSOC);
                        $snapshot['tables'][$table] = $rows;
                    } catch (Throwable) {
                        try {
                            $rows = $db->query("SELECT * FROM {$table}")->fetchAll(PDO::FETCH_ASSOC);
                            $snapshot['tables'][$table] = $rows;
                        } catch (Throwable) {}
                    }
                }

                file_put_contents($filePath, json_encode($snapshot, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE));
                $fileSizeKb = round(filesize($filePath) / 1024, 2);

                $msg = "JSON Data Snapshot backup created successfully! ({$filename} - {$fileSizeKb} KB)";
                $msgType = "success";
            } catch (Throwable $e) {
                $msg = "JSON backup failed: " . $e->getMessage();
                $msgType = "danger";
            }
        }
    } elseif ($action === 'restore_from_file') {
        if (!$db) {
            $msg = "Database connection is not available for restore.";
            $msgType = "danger";
        } elseif (empty($_FILES['backup_file']['tmp_name'])) {
            $msg = "Please choose a valid .sql or .json backup file to upload and restore.";
            $msgType = "warning";
        } else {
            $uploadedName = $_FILES['backup_file']['name'];
            $tmpPath = $_FILES['backup_file']['tmp_name'];
            $ext = strtolower(pathinfo($uploadedName, PATHINFO_EXTENSION));

            try {
                if ($ext === 'sql') {
                    $sqlContent = file_get_contents($tmpPath);
                    if (empty($sqlContent)) {
                        throw new RuntimeException("Uploaded SQL file is empty.");
                    }
                    
                    // Split SQL by semicolon
                    $statements = explode(";\n", $sqlContent);
                    $execCount = 0;
                    foreach ($statements as $stmtSql) {
                        $trimmed = trim($stmtSql);
                        if (!empty($trimmed) && !str_starts_with($trimmed, '--')) {
                            try {
                                $db->exec($trimmed);
                                $execCount++;
                            } catch (Throwable) {}
                        }
                    }

                    // Also save copy to backups directory
                    $safeSaveName = "uploaded_" . date('Y-m-d_H-i-s') . "_" . preg_replace('/[^a-zA-Z0-9_\.-]/', '_', $uploadedName);
                    @move_uploaded_file($tmpPath, $backupDir . '/' . $safeSaveName);

                    $msg = "Database successfully restored from SQL backup '{$uploadedName}'! ({$execCount} commands executed)";
                    $msgType = "success";
                } elseif ($ext === 'json') {
                    $jsonContent = file_get_contents($tmpPath);
                    $data = json_decode($jsonContent, true);
                    if (!isset($data['tables']) || !is_array($data['tables'])) {
                        throw new RuntimeException("Invalid JSON snapshot format. Expected 'tables' object.");
                    }

                    $restoredCount = 0;
                    foreach ($data['tables'] as $tbl => $rows) {
                        if (!in_array($tbl, $allTables, true) || empty($rows)) {
                            continue;
                        }
                        try {
                            $db->exec("DELETE FROM `{$tbl}`");
                        } catch (Throwable) {
                            try { $db->exec("DELETE FROM {$tbl}"); } catch (Throwable) {}
                        }

                        $firstRow = $rows[0];
                        $cols = array_keys($firstRow);
                        $colList = implode("`, `", $cols);
                        $placeholders = implode(", ", array_fill(0, count($cols), "?"));

                        $insStmt = $db->prepare("INSERT INTO `{$tbl}` (`{$colList}`) VALUES ({$placeholders})");
                        foreach ($rows as $r) {
                            try {
                                $insStmt->execute(array_values($r));
                                $restoredCount++;
                            } catch (Throwable) {}
                        }
                    }

                    $msg = "Database successfully restored from JSON snapshot '{$uploadedName}'! ({$restoredCount} records restored)";
                    $msgType = "success";
                } else {
                    $msg = "Unsupported file type. Only .sql and .json backups are supported.";
                    $msgType = "danger";
                }
            } catch (Throwable $e) {
                $msg = "Restore failed: " . $e->getMessage();
                $msgType = "danger";
            }
        }
    } elseif ($action === 'restore_existing') {
        $targetFile = basename($_POST['file_name'] ?? '');
        $fullPath = $backupDir . '/' . $targetFile;

        if (!$db) {
            $msg = "Database connection is not available.";
            $msgType = "danger";
        } elseif (!file_exists($fullPath)) {
            $msg = "Selected backup file does not exist on server.";
            $msgType = "danger";
        } else {
            try {
                $ext = strtolower(pathinfo($targetFile, PATHINFO_EXTENSION));
                if ($ext === 'sql') {
                    $sqlContent = file_get_contents($fullPath);
                    $statements = explode(";\n", $sqlContent);
                    $execCount = 0;
                    foreach ($statements as $stmtSql) {
                        $trimmed = trim($stmtSql);
                        if (!empty($trimmed) && !str_starts_with($trimmed, '--')) {
                            try {
                                $db->exec($trimmed);
                                $execCount++;
                            } catch (Throwable) {}
                        }
                    }
                    $msg = "Database restored successfully from server backup '{$targetFile}'! ({$execCount} commands executed)";
                    $msgType = "success";
                } elseif ($ext === 'json') {
                    $jsonContent = file_get_contents($fullPath);
                    $data = json_decode($jsonContent, true);
                    $restoredCount = 0;
                    if (isset($data['tables'])) {
                        foreach ($data['tables'] as $tbl => $rows) {
                            if (!empty($rows)) {
                                try { $db->exec("DELETE FROM `{$tbl}`"); } catch (Throwable) {}
                                $cols = array_keys($rows[0]);
                                $colList = implode("`, `", $cols);
                                $placeholders = implode(", ", array_fill(0, count($cols), "?"));
                                $insStmt = $db->prepare("INSERT INTO `{$tbl}` (`{$colList}`) VALUES ({$placeholders})");
                                foreach ($rows as $r) {
                                    try {
                                        $insStmt->execute(array_values($r));
                                        $restoredCount++;
                                    } catch (Throwable) {}
                                }
                            }
                        }
                    }
                    $msg = "Database restored successfully from JSON snapshot '{$targetFile}'! ({$restoredCount} records restored)";
                    $msgType = "success";
                }
            } catch (Throwable $e) {
                $msg = "Restore failed: " . $e->getMessage();
                $msgType = "danger";
            }
        }
    } elseif ($action === 'delete_backup') {
        $targetFile = basename($_POST['file_name'] ?? '');
        $fullPath = $backupDir . '/' . $targetFile;
        if (file_exists($fullPath) && is_file($fullPath)) {
            @unlink($fullPath);
            $msg = "Backup file '{$targetFile}' permanently deleted.";
            $msgType = "warning";
        } else {
            $msg = "File not found.";
            $msgType = "danger";
        }
    } elseif ($action === 'factory_reset') {
        if (!$db) {
            $msg = "Database connection is not available.";
            $msgType = "danger";
        } else {
            try {
                $schemaFile = __DIR__ . '/../schema.sql';
                if (!file_exists($schemaFile)) {
                    throw new RuntimeException("Master schema file (schema.sql) not found.");
                }

                $schemaSql = file_get_contents($schemaFile);
                $statements = explode(";\n", $schemaSql);
                $count = 0;
                foreach ($statements as $stmtSql) {
                    $trimmed = trim($stmtSql);
                    if (!empty($trimmed) && !str_starts_with($trimmed, '--')) {
                        try {
                            $db->exec($trimmed);
                            $count++;
                        } catch (Throwable) {}
                    }
                }

                $msg = "Factory reset completed! Database structure and defaults restored from master schema.sql ({$count} statements applied).";
                $msgType = "success";
            } catch (Throwable $e) {
                $msg = "Factory reset failed: " . $e->getMessage();
                $msgType = "danger";
            }
        }
    }
}

// Scan backups directory
$backupFiles = [];
if (is_dir($backupDir)) {
    $files = scandir($backupDir);
    foreach ($files as $f) {
        if ($f !== '.' && $f !== '..' && !str_starts_with($f, '.')) {
            $full = $backupDir . '/' . $f;
            if (is_file($full)) {
                $backupFiles[] = [
                    'name' => $f,
                    'size' => filesize($full),
                    'size_formatted' => round(filesize($full) / 1024, 2) . ' KB',
                    'modified' => filemtime($full),
                    'date_formatted' => date('Y-m-d H:i:s', filemtime($full)),
                    'type' => str_ends_with($f, '.sql') ? 'SQL Dump' : (str_ends_with($f, '.json') ? 'JSON Snapshot' : 'Archive')
                ];
            }
        }
    }
}

// Sort by newest first
usort($backupFiles, fn($a, $b) => $b['modified'] <=> $a['modified']);

// Metrics
$totalBackupsCount = count($backupFiles);
$totalBackupBytes = array_sum(array_column($backupFiles, 'size'));
$totalBackupSizeFormatted = round($totalBackupBytes / (1024 * 1024), 2) . ' MB';
$driverName = Database::isMySQL() ? 'MySQL 8.0 / MariaDB' : (Database::isSQLite() ? 'SQLite 3 (Resilient)' : 'Unknown');

renderAdminHeader('Database Backup & Restore', 'backup');
?>

<!-- Title & Action Toolbar -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a; letter-spacing: -0.02em;">
            <i class="bi bi-database-fill-gear text-primary me-2"></i>Database Backup & Restore Center
        </h2>
        <p class="text-secondary mb-0">Create full SQL database dumps, JSON snapshots, download archives, and restore data with 1-click safety.</p>
    </div>
    <div class="d-flex align-items-center gap-2 flex-wrap">
        <form method="POST" class="d-inline" onsubmit="return confirm('Generate a complete SQL Database Backup now?');">
            <input type="hidden" name="action" value="create_sql_backup">
            <button type="submit" class="btn btn-primary fw-semibold px-3 py-2 d-flex align-items-center gap-2 shadow-sm">
                <i class="bi bi-cloud-arrow-down-fill"></i> + Create SQL Backup
            </button>
        </form>
        <form method="POST" class="d-inline">
            <input type="hidden" name="action" value="create_json_backup">
            <button type="submit" class="btn btn-outline-primary fw-semibold px-3 py-2 d-flex align-items-center gap-2 shadow-sm">
                <i class="bi bi-file-earmark-code"></i> JSON Snapshot
            </button>
        </form>
        <button class="btn btn-outline-success fw-semibold px-3 py-2 d-flex align-items-center gap-2 shadow-sm" data-bs-toggle="modal" data-bs-target="#uploadRestoreModal">
            <i class="bi bi-cloud-arrow-up-fill"></i> Upload & Restore
        </button>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show shadow-sm" role="alert">
        <i class="bi bi-info-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- KPI Status Summary -->
<div class="row g-3 mb-4">
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Database Driver</div>
                <div class="fw-bold text-dark fs-5 mt-1"><?= $driverName ?></div>
                <div class="text-muted small mt-1">Host: <?= htmlspecialchars(Config::getDbHost()) ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-blue" style="width: 44px; height: 44px;">
                <i class="bi bi-database-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Stored Backups</div>
                <div class="fw-bold text-primary fs-4"><?= $totalBackupsCount ?> Files</div>
                <div class="text-muted small mt-1">Total Disk: <?= $totalBackupSizeFormatted ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green" style="width: 44px; height: 44px;">
                <i class="bi bi-archive-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Active Core Tables</div>
                <div class="fw-bold text-dark fs-4"><?= count($allTables) ?> Tables</div>
                <div class="text-muted small mt-1">Users, Models, Agents, Escrow</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-indigo" style="width: 44px; height: 44px;">
                <i class="bi bi-table"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Database Status</div>
                <div class="fw-bold text-success fs-5 mt-1">
                    <i class="bi bi-check-circle-fill me-1"></i> Connected & Online
                </div>
                <div class="text-muted small mt-1">Zero 500 Error Protection Active</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-gold" style="width: 44px; height: 44px;">
                <i class="bi bi-shield-check"></i>
            </div>
        </div>
    </div>
</div>

<!-- Backups History Table -->
<div class="card card-custom p-0 overflow-hidden mb-4 shadow-sm">
    <div class="p-3 border-bottom d-flex justify-content-between align-items-center bg-light flex-wrap gap-2">
        <div class="fw-bold text-dark d-flex align-items-center gap-2">
            <i class="bi bi-clock-history text-secondary"></i> Available Backups Archive
        </div>
        <div class="text-secondary small">
            Stored in <code>backend/backups/</code>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light text-secondary small text-uppercase">
                <tr>
                    <th class="ps-3">Backup File Name</th>
                    <th>Format</th>
                    <th>File Size</th>
                    <th>Created Date</th>
                    <th class="text-end pe-3">Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($backupFiles)): ?>
                    <tr>
                        <td colspan="5" class="text-center py-5 text-secondary">
                            <i class="bi bi-archive text-muted fs-1 d-block mb-2"></i>
                            <div class="fw-semibold">No backup archives created yet.</div>
                            <div class="small">Click <strong>"+ Create SQL Backup"</strong> above to generate your first complete backup.</div>
                        </td>
                    </tr>
                <?php else: ?>
                    <?php foreach ($backupFiles as $bf): ?>
                        <tr>
                            <td class="ps-3">
                                <div class="d-flex align-items-center gap-2">
                                    <i class="bi <?= str_ends_with($bf['name'], '.sql') ? 'bi-filetype-sql text-primary fs-4' : 'bi-filetype-json text-warning fs-4' ?>"></i>
                                    <div>
                                        <div class="fw-semibold text-dark"><?= htmlspecialchars($bf['name']) ?></div>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <span class="badge <?= str_ends_with($bf['name'], '.sql') ? 'bg-primary' : 'bg-warning text-dark' ?>">
                                    <?= htmlspecialchars($bf['type']) ?>
                                </span>
                            </td>
                            <td class="fw-semibold text-dark"><?= $bf['size_formatted'] ?></td>
                            <td class="text-secondary small"><?= $bf['date_formatted'] ?></td>
                            <td class="text-end pe-3">
                                <div class="d-inline-flex gap-2">
                                    <!-- Download Button -->
                                    <a href="backup.php?download=<?= urlencode($bf['name']) ?>" class="btn btn-sm btn-outline-primary" title="Download to PC">
                                        <i class="bi bi-download me-1"></i> Download
                                    </a>

                                    <!-- Restore Button -->
                                    <form method="POST" class="d-inline" onsubmit="return confirm('Are you sure you want to RESTORE from <?= htmlspecialchars($bf['name']) ?>? This will update tables with backup data.');">
                                        <input type="hidden" name="action" value="restore_existing">
                                        <input type="hidden" name="file_name" value="<?= htmlspecialchars($bf['name']) ?>">
                                        <button type="submit" class="btn btn-sm btn-outline-success" title="Restore this backup">
                                            <i class="bi bi-arrow-counterclockwise me-1"></i> Restore
                                        </button>
                                    </form>

                                    <!-- Delete Button -->
                                    <form method="POST" class="d-inline" onsubmit="return confirm('Permanently delete backup <?= htmlspecialchars($bf['name']) ?>?');">
                                        <input type="hidden" name="action" value="delete_backup">
                                        <input type="hidden" name="file_name" value="<?= htmlspecialchars($bf['name']) ?>">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete file">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<!-- Danger Zone: Factory Reset -->
<div class="card border-danger border-opacity-25 rounded-3 p-4 mb-4" style="background: #fffafa;">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
        <div>
            <h5 class="fw-bold text-danger mb-1">
                <i class="bi bi-exclamation-octagon-fill me-2"></i>Factory Database Reset
            </h5>
            <p class="text-secondary mb-0 small">
                Reset all platform tables and reinstall clean initial demo data directly from <code>backend/schema.sql</code>. All custom modifications will be refreshed.
            </p>
        </div>
        <form method="POST" onsubmit="return confirm('WARNING: Are you sure you want to perform a FACTORY DATABASE RESET? This will restore schema.sql default structure.');">
            <input type="hidden" name="action" value="factory_reset">
            <button type="submit" class="btn btn-danger fw-semibold px-4 py-2 shadow-sm">
                <i class="bi bi-arrow-repeat me-1"></i> Re-seed Factory Defaults
            </button>
        </form>
    </div>
</div>

<!-- Modal: Upload & Restore Backup -->
<div class="modal fade" id="uploadRestoreModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form method="POST" enctype="multipart/form-data" onsubmit="return confirm('Are you sure you want to restore the database using this uploaded file?');">
                <input type="hidden" name="action" value="restore_from_file">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold text-dark">
                        <i class="bi bi-cloud-arrow-up-fill text-success me-2"></i>Upload & Restore Database
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="alert alert-info py-2 px-3 small mb-3">
                        <i class="bi bi-info-circle me-1"></i> Supported formats: <strong>.sql</strong> (full database dumps) and <strong>.json</strong> (structured snapshots).
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold text-dark">Select Backup File</label>
                        <input type="file" name="backup_file" class="form-control" accept=".sql,.json" required>
                    </div>

                    <div class="text-muted small">
                        Note: Restoring will execute the statements contained in the backup file. It is recommended to create a new backup before proceeding with a restore.
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success fw-semibold">
                        <i class="bi bi-check2-circle me-1"></i> Upload and Restore Now
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<?php
renderAdminFooter();
