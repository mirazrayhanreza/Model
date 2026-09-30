<?php
declare(strict_types=1);

// backend/admin/staff.php
// Admin & Staff Role-Based Access Control (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$staffMembers = [
    [
        'name' => 'Miraz Reza',
        'email' => 'admin@modolconnect.com',
        'role' => 'Super Admin',
        'permissions' => 'Full Administrative Access',
        'status' => 'Active',
        'last_login' => 'Just now'
    ],
    [
        'name' => 'Tariqul Islam',
        'email' => 'finance@modolconnect.com',
        'role' => 'Finance Admin',
        'permissions' => 'Wallets, Escrow, Payouts, Commissions',
        'status' => 'Active',
        'last_login' => '2 hours ago'
    ],
    [
        'name' => 'Sabrina Sultana',
        'email' => 'verification@modolconnect.com',
        'role' => 'Model Verification Staff',
        'permissions' => 'KYC Review, Approve/Reject Models',
        'status' => 'Active',
        'last_login' => 'Yesterday'
    ],
    [
        'name' => 'Arman Hossain',
        'email' => 'p2p.ops@modolconnect.com',
        'role' => 'P2P & Cash Agent Manager',
        'permissions' => 'P2P Escrow, Cash Agents, Disputes',
        'status' => 'Active',
        'last_login' => '1 day ago'
    ]
];

renderAdminHeader('Admin & Staff', 'staff');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Admin & Staff Roles</h2>
        <p class="text-secondary mb-0">Role-based access permissions for finance, verification, and P2P operations.</p>
    </div>
    <button class="btn btn-primary fw-semibold px-3 py-2">
        <i class="bi bi-shield-plus me-1"></i> Add Staff Member
    </button>
</div>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Staff Member</th>
                    <th>Role</th>
                    <th>Permissions Scope</th>
                    <th>Last Active</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Action</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($staffMembers as $s): ?>
                <tr>
                    <td class="ps-4">
                        <div class="fw-bold text-dark"><?= htmlspecialchars($s['name']) ?></div>
                        <small class="text-muted"><?= htmlspecialchars($s['email']) ?></small>
                    </td>
                    <td><span class="badge bg-primary-subtle text-primary border fw-semibold"><?= htmlspecialchars($s['role']) ?></span></td>
                    <td class="text-secondary small"><?= htmlspecialchars($s['permissions']) ?></td>
                    <td class="text-muted small"><?= htmlspecialchars($s['last_login']) ?></td>
                    <td><span class="badge-status badge-approved"><?= $s['status'] ?></span></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-outline-secondary">Permissions</button>
                    </td>
                </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    </div>
</div>

<?php
renderAdminFooter();
?>
