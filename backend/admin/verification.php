<?php
declare(strict_types=1);

// backend/admin/verification.php
// Model Verification & KYC Review Screen (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'success';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $modelName = $_POST['model_name'] ?? 'Model';
    $notes = trim($_POST['verification_notes'] ?? '');

    if ($action === 'approve') {
        $msg = "Model {$modelName} has been approved and verified successfully!";
        $msgType = 'success';
    } elseif ($action === 'reject') {
        $msg = "Model {$modelName} application has been rejected. Reason: " . ($notes ?: 'Incomplete documents');
        $msgType = 'danger';
    } elseif ($action === 'request_update') {
        $msg = "Update request sent to {$modelName}: " . ($notes ?: 'Please re-upload a clear selfie.');
        $msgType = 'warning';
    }
}

$pendingModels = [
    [
        'id' => 101,
        'name' => 'Jessica A.',
        'country' => 'UAE',
        'city' => 'Dubai',
        'dob' => '15 May 2000',
        'phone' => '+971 50 123 4567',
        'email' => 'jessica.a@modolconnect.com',
        'submitted' => 'Today, 10:15 AM',
        'status' => 'Pending',
        'hourly_rate' => 250,
        'services' => 'Fashion, Editorial, Commercial Photoshoot',
        'bio' => 'Professional fashion and runway model with 4 years experience in Dubai and Milan.',
        'profile_photo' => 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&h=300&fit=crop&crop=faces',
        'verification_selfie' => 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop&crop=faces',
        'nid_photo' => 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=400&h=250&fit=crop'
    ],
    [
        'id' => 102,
        'name' => 'Maria K.',
        'country' => 'Malaysia',
        'city' => 'Kuala Lumpur',
        'dob' => '22 Aug 1999',
        'phone' => '+60 12 345 6789',
        'email' => 'maria.k@modolconnect.com',
        'submitted' => '26 Sep 2025',
        'status' => 'Pending',
        'hourly_rate' => 180,
        'services' => 'Commercial, Fitness, High Fashion',
        'bio' => 'Fitness influencer & commercial model featured in regional magazine covers.',
        'profile_photo' => 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop&crop=faces',
        'verification_selfie' => 'https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=300&h=300&fit=crop&crop=faces',
        'nid_photo' => 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=400&h=250&fit=crop'
    ],
    [
        'id' => 103,
        'name' => 'Ayesha Rahman',
        'country' => 'Bangladesh',
        'city' => 'Dhaka',
        'dob' => '10 Jan 2002',
        'phone' => '+880 1711 223344',
        'email' => 'ayesha.r@modolconnect.com',
        'submitted' => '25 Sep 2025',
        'status' => 'Pending',
        'hourly_rate' => 120,
        'services' => 'Bridal, Traditional, Saree, Casual',
        'bio' => 'Top bridal fashion catalog model specialized in traditional South Asian attire.',
        'profile_photo' => 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop&crop=faces',
        'verification_selfie' => 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&h=300&fit=crop&crop=faces',
        'nid_photo' => 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=400&h=250&fit=crop'
    ]
];

renderAdminHeader('Model Verification', 'verification');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Model Verification</h2>
        <p class="text-secondary mb-0">Review identity documents, verification selfies, and approve verified badges.</p>
    </div>
    <div class="d-flex align-items-center gap-2">
        <span class="badge bg-warning text-dark px-3 py-2 fw-semibold" style="font-size: 0.85rem;">
            <i class="bi bi-clock-history me-1"></i> <?= count($pendingModels) ?> Pending Approvals
        </span>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Filter & Search Bar -->
<div class="card card-custom p-3 mb-4">
    <div class="row g-3 align-items-center">
        <div class="col-md-5">
            <div class="input-group">
                <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
                <input type="text" class="form-control border-start-0 ps-0" placeholder="Search by name, phone, email...">
            </div>
        </div>
        <div class="col-md-3">
            <select class="form-select">
                <option value="">Country: All Countries</option>
                <option value="BD">Bangladesh</option>
                <option value="UAE">United Arab Emirates</option>
                <option value="MY">Malaysia</option>
                <option value="SA">Saudi Arabia</option>
            </select>
        </div>
        <div class="col-md-3">
            <select class="form-select">
                <option value="">Status: Pending Review</option>
                <option value="approved">Approved</option>
                <option value="rejected">Rejected</option>
                <option value="all">All Submissions</option>
            </select>
        </div>
        <div class="col-md-1">
            <button class="btn btn-outline-secondary w-100"><i class="bi bi-funnel"></i></button>
        </div>
    </div>
</div>

<!-- Pending Verification Table -->
<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Photo</th>
                    <th>Name</th>
                    <th>Country</th>
                    <th>City</th>
                    <th>Submitted</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Action</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($pendingModels as $m): ?>
                <tr>
                    <td class="ps-4">
                        <img src="<?= htmlspecialchars($m['profile_photo']) ?>" class="rounded-circle" style="width: 44px; height: 44px; object-fit: cover;" alt="Photo">
                    </td>
                    <td>
                        <div class="fw-bold text-dark"><?= htmlspecialchars($m['name']) ?></div>
                        <small class="text-muted"><?= htmlspecialchars($m['email']) ?></small>
                    </td>
                    <td><span class="fw-semibold text-secondary"><?= htmlspecialchars($m['country']) ?></span></td>
                    <td><?= htmlspecialchars($m['city']) ?></td>
                    <td class="text-muted small"><?= htmlspecialchars($m['submitted']) ?></td>
                    <td><span class="badge-status badge-pending">Pending Review</span></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-primary px-3 fw-semibold" data-bs-toggle="modal" data-bs-target="#verifyModal<?= $m['id'] ?>">
                            <i class="bi bi-eye-fill me-1"></i> Review KYC
                        </button>
                    </td>
                </tr>

                <!-- Verification Detail Modal -->
                <div class="modal fade" id="verifyModal<?= $m['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <div>
                                    <h5 class="modal-title fw-bold">KYC Verification — <?= htmlspecialchars($m['name']) ?></h5>
                                    <small class="text-muted">Application #<?= $m['id'] ?> | Submitted <?= htmlspecialchars($m['submitted']) ?></small>
                                </div>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body p-4">
                                <!-- Photo Comparison Row -->
                                <div class="row g-3 mb-4">
                                    <div class="col-sm-6 text-center">
                                        <div class="p-3 bg-light rounded-3 border">
                                            <span class="text-muted small fw-bold d-block mb-2 text-uppercase">Profile Photo</span>
                                            <img src="<?= htmlspecialchars($m['profile_photo']) ?>" class="rounded-3 img-fluid shadow-sm" style="max-height: 200px; object-fit: cover;" alt="Profile">
                                        </div>
                                    </div>
                                    <div class="col-sm-6 text-center">
                                        <div class="p-3 bg-light rounded-3 border">
                                            <span class="text-muted small fw-bold d-block mb-2 text-uppercase">Verification Selfie (Holding ID)</span>
                                            <img src="<?= htmlspecialchars($m['verification_selfie']) ?>" class="rounded-3 img-fluid shadow-sm" style="max-height: 200px; object-fit: cover;" alt="Selfie">
                                        </div>
                                    </div>
                                </div>

                                <!-- Personal Details Table -->
                                <h6 class="fw-bold text-dark mb-3">Model Profile Details</h6>
                                <div class="row g-2 mb-3">
                                    <div class="col-6 col-md-4">
                                        <small class="text-muted d-block">Full Legal Name</small>
                                        <span class="fw-semibold text-dark"><?= htmlspecialchars($m['name']) ?></span>
                                    </div>
                                    <div class="col-6 col-md-4">
                                        <small class="text-muted d-block">Date of Birth</small>
                                        <span class="fw-semibold text-dark"><?= htmlspecialchars($m['dob']) ?></span>
                                    </div>
                                    <div class="col-6 col-md-4">
                                        <small class="text-muted d-block">Phone Number</small>
                                        <span class="fw-semibold text-dark"><?= htmlspecialchars($m['phone']) ?></span>
                                    </div>
                                    <div class="col-6 col-md-4">
                                        <small class="text-muted d-block">Country & City</small>
                                        <span class="fw-semibold text-dark"><?= htmlspecialchars($m['country']) ?>, <?= htmlspecialchars($m['city']) ?></span>
                                    </div>
                                    <div class="col-6 col-md-4">
                                        <small class="text-muted d-block">Hourly Rate</small>
                                        <span class="fw-semibold text-success">$<?= $m['hourly_rate'] ?> / hour</span>
                                    </div>
                                    <div class="col-6 col-md-4">
                                        <small class="text-muted d-block">Offered Services</small>
                                        <span class="fw-semibold text-dark"><?= htmlspecialchars($m['services']) ?></span>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <small class="text-muted d-block">Biography</small>
                                    <p class="small text-secondary mb-0"><?= htmlspecialchars($m['bio']) ?></p>
                                </div>

                                <!-- Passport / NID Document Preview -->
                                <div class="p-3 bg-light rounded-3 border mb-3">
                                    <span class="text-muted small fw-bold d-block mb-2 text-uppercase">Passport / National ID Card Preview</span>
                                    <img src="<?= htmlspecialchars($m['nid_photo']) ?>" class="img-fluid rounded border" style="max-height: 160px; width: 100%; object-fit: cover;" alt="NID Document">
                                </div>

                                <!-- Verification Notes Form -->
                                <form method="POST">
                                    <input type="hidden" name="model_name" value="<?= htmlspecialchars($m['name']) ?>">
                                    <div class="mb-3">
                                        <label class="form-label text-dark fw-semibold small">Admin Verification Notes</label>
                                        <textarea name="verification_notes" class="form-control" rows="2" placeholder="Add optional remarks or instructions for the applicant..."></textarea>
                                    </div>

                                    <div class="d-flex justify-content-between align-items-center pt-2 border-top">
                                        <button type="submit" name="action" value="reject" class="btn btn-outline-danger px-3">
                                            <i class="bi bi-x-circle me-1"></i> Reject
                                        </button>
                                        <div class="d-flex gap-2">
                                            <button type="submit" name="action" value="request_update" class="btn btn-outline-warning px-3 text-dark">
                                                <i class="bi bi-arrow-repeat me-1"></i> Request Update
                                            </button>
                                            <button type="submit" name="action" value="approve" class="btn btn-success px-4 fw-semibold">
                                                <i class="bi bi-check-circle-fill me-1"></i> Approve & Verify
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <?php endforeach; ?>
            </tbody>
        </table>
    </div>
</div>

<?php
renderAdminFooter();
?>
