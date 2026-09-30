<?php
declare(strict_types=1);

// backend/admin/reviews.php
// Client Reviews & Ratings Moderation (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$reviews = [
    ['id' => 1, 'user' => 'Rahim Uddin', 'model' => 'Jessica A.', 'rating' => 5.0, 'comment' => 'Exceptional professionalism during our commercial fashion shoot in Dhaka. Highly recommended!', 'date' => '30 Sep 2025', 'status' => 'Published'],
    ['id' => 2, 'user' => 'Karim Khan', 'model' => 'Maria K.', 'rating' => 4.8, 'comment' => 'Punctual, energetic, and took creative directions effortlessly.', 'date' => '28 Sep 2025', 'status' => 'Published'],
    ['id' => 3, 'user' => 'Faisal Al-Mansoor', 'model' => 'Sophia L.', 'rating' => 5.0, 'comment' => 'Outstanding runway performance for our Dubai luxury showcase.', 'date' => '25 Sep 2025', 'status' => 'Published']
];

renderAdminHeader('Reviews', 'reviews');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Model Reviews & Ratings</h2>
        <p class="text-secondary mb-0">Client reviews, rating authenticity moderation, and feedback verification.</p>
    </div>
</div>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Review ID</th>
                    <th>Client (Author)</th>
                    <th>Model Reviewed</th>
                    <th>Rating</th>
                    <th>Commentary</th>
                    <th>Date</th>
                    <th class="text-end pe-4">Action</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($reviews as $r): ?>
                <tr>
                    <td class="ps-4 fw-bold text-muted">#<?= $r['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($r['user']) ?></td>
                    <td class="fw-semibold text-danger"><?= htmlspecialchars($r['model']) ?></td>
                    <td>
                        <span class="text-warning fw-bold">
                            <i class="bi bi-star-fill me-1"></i><?= number_format($r['rating'], 1) ?>
                        </span>
                    </td>
                    <td class="text-secondary small" style="max-width: 320px;"><?= htmlspecialchars($r['comment']) ?></td>
                    <td class="text-muted small"><?= htmlspecialchars($r['date']) ?></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-outline-danger">Hide</button>
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
