<?php
declare(strict_types=1);

// backend/admin/dashboard.php
// Modol Connect - Executive SaaS Dashboard (Desktop-First Responsive, PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

if (isApiRequest()) {
    sendJsonResponse('success', 'Admin dashboard statistics', [
        'total_users' => 12580,
        'total_models' => 3240,
        'verified_models' => 2180,
        'active_bookings' => 1120,
        'today_revenue' => 12580.00,
        'platform_wallet' => 85420.00,
        'active_agents' => 285,
        'open_disputes' => 18
    ]);
}

renderAdminHeader('Dashboard Overview', 'dashboard');
?>

<!-- Header Title & Filter Bar -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a; letter-spacing: -0.02em;">Dashboard</h2>
        <p class="text-secondary mb-0" style="font-size: 0.9rem;">Welcome to Modol Connect Admin Panel. Here's your platform overview.</p>
    </div>
    <div class="d-flex align-items-center gap-2">
        <div class="dropdown">
            <button class="btn btn-white border shadow-sm px-3 py-2 dropdown-toggle d-flex align-items-center gap-2 text-dark fw-semibold" style="font-size: 0.85rem; border-radius: 9px;" data-bs-toggle="dropdown">
                <i class="bi bi-calendar3 text-secondary"></i>
                <span>Sep 1, 2025 - Sep 30, 2025</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                <li><a class="dropdown-item active" href="#">Current Month (Sep 2025)</a></li>
                <li><a class="dropdown-item" href="#">Last 30 Days</a></li>
                <li><a class="dropdown-item" href="#">Last 90 Days</a></li>
                <li><a class="dropdown-item" href="#">Year to Date</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#">Custom Range...</a></li>
            </ul>
        </div>
    </div>
</div>

<!-- Row 1: 8 KPI Summary Cards (2 rows of 4 cards on desktop) -->
<div class="row g-3 mb-4">
    <!-- Card 1: Total Users -->
    <div class="col-xl-3 col-md-6">
        <div class="kpi-card">
            <div>
                <div class="kpi-title">Total Users</div>
                <div class="kpi-value">12,580</div>
                <div class="kpi-trend trend-up">
                    <i class="bi bi-arrow-up-short"></i>
                    <span>8.4%</span>
                    <span class="text-muted fw-normal">This month</span>
                </div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-blue">
                <i class="bi bi-people-fill"></i>
            </div>
        </div>
    </div>

    <!-- Card 2: Total Models -->
    <div class="col-xl-3 col-md-6">
        <div class="kpi-card">
            <div>
                <div class="kpi-title">Total Models</div>
                <div class="kpi-value">3,240</div>
                <div class="kpi-trend trend-up">
                    <i class="bi bi-arrow-up-short"></i>
                    <span>12.5%</span>
                    <span class="text-muted fw-normal">This month</span>
                </div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-pink">
                <i class="bi bi-person-heart"></i>
            </div>
        </div>
    </div>

    <!-- Card 3: Verified Models -->
    <div class="col-xl-3 col-md-6">
        <div class="kpi-card">
            <div>
                <div class="kpi-title">Verified Models</div>
                <div class="kpi-value">2,180</div>
                <div class="kpi-trend trend-up">
                    <i class="bi bi-arrow-up-short"></i>
                    <span>6.3%</span>
                    <span class="text-muted fw-normal">This month</span>
                </div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green">
                <i class="bi bi-patch-check-fill"></i>
            </div>
        </div>
    </div>

    <!-- Card 4: Active Bookings -->
    <div class="col-xl-3 col-md-6">
        <div class="kpi-card">
            <div>
                <div class="kpi-title">Active Bookings</div>
                <div class="kpi-value">1,120</div>
                <div class="kpi-trend trend-up">
                    <i class="bi bi-arrow-up-short"></i>
                    <span>9.7%</span>
                    <span class="text-muted fw-normal">This month</span>
                </div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-purple">
                <i class="bi bi-calendar-event-fill"></i>
            </div>
        </div>
    </div>

    <!-- Card 5: Today Revenue -->
    <div class="col-xl-3 col-md-6">
        <div class="kpi-card">
            <div>
                <div class="kpi-title">Today Revenue</div>
                <div class="kpi-value">$12,580</div>
                <div class="kpi-trend trend-up">
                    <i class="bi bi-arrow-up-short"></i>
                    <span>18.2%</span>
                    <span class="text-muted fw-normal">Today</span>
                </div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-gold">
                <i class="bi bi-currency-dollar"></i>
            </div>
        </div>
    </div>

    <!-- Card 6: Platform Wallet -->
    <div class="col-xl-3 col-md-6">
        <div class="kpi-card">
            <div>
                <div class="kpi-title">Platform Wallet</div>
                <div class="kpi-value">$85,420</div>
                <div class="kpi-trend trend-up">
                    <i class="bi bi-arrow-up-short"></i>
                    <span>5.4%</span>
                    <span class="text-muted fw-normal">Total Balance</span>
                </div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-indigo">
                <i class="bi bi-wallet-fill"></i>
            </div>
        </div>
    </div>

    <!-- Card 7: Active Agents -->
    <div class="col-xl-3 col-md-6">
        <div class="kpi-card">
            <div>
                <div class="kpi-title">Active Agents</div>
                <div class="kpi-value">285</div>
                <div class="kpi-trend trend-up">
                    <i class="bi bi-arrow-up-short"></i>
                    <span>4.1%</span>
                    <span class="text-muted fw-normal">Today</span>
                </div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-cyan">
                <i class="bi bi-people-fill"></i>
            </div>
        </div>
    </div>

    <!-- Card 8: Open Disputes -->
    <div class="col-xl-3 col-md-6">
        <div class="kpi-card">
            <div>
                <div class="kpi-title">Open Disputes</div>
                <div class="kpi-value">18</div>
                <div class="kpi-trend trend-down">
                    <i class="bi bi-arrow-down-short"></i>
                    <span>12.5%</span>
                    <span class="text-muted fw-normal">Pending</span>
                </div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-red">
                <i class="bi bi-exclamation-triangle-fill"></i>
            </div>
        </div>
    </div>
</div>

<!-- Row 2: Charts & Recent Registrations -->
<div class="row g-4 mb-4">
    <!-- Revenue Overview Chart -->
    <div class="col-xl-6 col-lg-12">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <div>
                    <h5 class="fw-bold text-dark mb-0">Revenue Overview</h5>
                </div>
                <div class="btn-group btn-group-sm" role="group">
                    <button type="button" class="btn btn-outline-secondary px-3">Today</button>
                    <button type="button" class="btn btn-outline-secondary px-3">7 Days</button>
                    <button type="button" class="btn btn-primary px-3 fw-semibold">30 Days</button>
                    <button type="button" class="btn btn-outline-secondary px-3">This Year</button>
                </div>
            </div>
            <div id="revenueChart" style="min-height: 290px;"></div>
            <div class="d-flex justify-content-center align-items-center gap-4 mt-2 flex-wrap small">
                <div class="d-flex align-items-center gap-2">
                    <span class="rounded-circle d-inline-block" style="width: 10px; height: 10px; background-color: #3b82f6;"></span>
                    <span class="text-secondary">Deposit</span>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <span class="rounded-circle d-inline-block" style="width: 10px; height: 10px; background-color: #ec4899;"></span>
                    <span class="text-secondary">Booking Revenue</span>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <span class="rounded-circle d-inline-block" style="width: 10px; height: 10px; background-color: #10b981;"></span>
                    <span class="text-secondary">Platform Commission</span>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <span class="rounded-circle d-inline-block" style="width: 10px; height: 10px; background-color: #f59e0b;"></span>
                    <span class="text-secondary">Agent Commission</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Booking Status Donut -->
    <div class="col-xl-3 col-lg-6">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0">Booking Status</h5>
            </div>
            <div id="bookingStatusChart" style="min-height: 220px;"></div>
            
            <div class="mt-3">
                <div class="d-flex justify-content-between align-items-center py-1 border-bottom border-light">
                    <div class="d-flex align-items-center gap-2">
                        <span class="rounded-circle" style="width: 8px; height: 8px; background-color: #f97316;"></span>
                        <span class="small text-secondary">Pending</span>
                    </div>
                    <span class="fw-bold text-dark small">125</span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-1 border-bottom border-light">
                    <div class="d-flex align-items-center gap-2">
                        <span class="rounded-circle" style="width: 8px; height: 8px; background-color: #3b82f6;"></span>
                        <span class="small text-secondary">Accepted</span>
                    </div>
                    <span class="fw-bold text-dark small">88</span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-1 border-bottom border-light">
                    <div class="d-flex align-items-center gap-2">
                        <span class="rounded-circle" style="width: 8px; height: 8px; background-color: #8b5cf6;"></span>
                        <span class="small text-secondary">Paid</span>
                    </div>
                    <span class="fw-bold text-dark small">74</span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-1 border-bottom border-light">
                    <div class="d-flex align-items-center gap-2">
                        <span class="rounded-circle" style="width: 8px; height: 8px; background-color: #06b6d4;"></span>
                        <span class="small text-secondary">Active</span>
                    </div>
                    <span class="fw-bold text-dark small">35</span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-1 border-bottom border-light">
                    <div class="d-flex align-items-center gap-2">
                        <span class="rounded-circle" style="width: 8px; height: 8px; background-color: #10b981;"></span>
                        <span class="small text-secondary">Completed</span>
                    </div>
                    <span class="fw-bold text-dark small">840</span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-1 border-bottom border-light">
                    <div class="d-flex align-items-center gap-2">
                        <span class="rounded-circle" style="width: 8px; height: 8px; background-color: #ef4444;"></span>
                        <span class="small text-secondary">Cancelled</span>
                    </div>
                    <span class="fw-bold text-dark small">52</span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-1">
                    <div class="d-flex align-items-center gap-2">
                        <span class="rounded-circle" style="width: 8px; height: 8px; background-color: #991b1b;"></span>
                        <span class="small text-secondary">Disputed</span>
                    </div>
                    <span class="fw-bold text-dark small">18</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Registrations -->
    <div class="col-xl-3 col-lg-6">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0">Recent Registrations</h5>
                <a href="users.php" class="small fw-semibold text-primary text-decoration-none">View All</a>
            </div>
            
            <div class="d-flex flex-column gap-3 mt-2">
                <div class="d-flex align-items-center justify-content-between">
                    <div class="d-flex align-items-center gap-2">
                        <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 38px; height: 38px; object-fit: cover;" alt="Avatar">
                        <div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="fw-semibold text-dark small">Rahim Uddin</span>
                                <span class="badge bg-primary-subtle text-primary border" style="font-size: 0.65rem;">User</span>
                            </div>
                            <small class="text-muted" style="font-size: 0.72rem;">2 minutes ago</small>
                        </div>
                    </div>
                </div>

                <div class="d-flex align-items-center justify-content-between">
                    <div class="d-flex align-items-center gap-2">
                        <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 38px; height: 38px; object-fit: cover;" alt="Avatar">
                        <div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="fw-semibold text-dark small">Jessica A.</span>
                                <span class="badge bg-danger-subtle text-danger border" style="font-size: 0.65rem;">Model</span>
                            </div>
                            <small class="text-muted" style="font-size: 0.72rem;">5 minutes ago</small>
                        </div>
                    </div>
                </div>

                <div class="d-flex align-items-center justify-content-between">
                    <div class="d-flex align-items-center gap-2">
                        <img src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 38px; height: 38px; object-fit: cover;" alt="Avatar">
                        <div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="fw-semibold text-dark small">Karim Khan</span>
                                <span class="badge bg-primary-subtle text-primary border" style="font-size: 0.65rem;">User</span>
                            </div>
                            <small class="text-muted" style="font-size: 0.72rem;">12 minutes ago</small>
                        </div>
                    </div>
                </div>

                <div class="d-flex align-items-center justify-content-between">
                    <div class="d-flex align-items-center gap-2">
                        <img src="https://images.unsplash.com/photo-1517841905240-472988babdf9?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 38px; height: 38px; object-fit: cover;" alt="Avatar">
                        <div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="fw-semibold text-dark small">Maria K.</span>
                                <span class="badge bg-danger-subtle text-danger border" style="font-size: 0.65rem;">Model</span>
                            </div>
                            <small class="text-muted" style="font-size: 0.72rem;">18 minutes ago</small>
                        </div>
                    </div>
                </div>

                <div class="d-flex align-items-center justify-content-between">
                    <div class="d-flex align-items-center gap-2">
                        <img src="https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 38px; height: 38px; object-fit: cover;" alt="Avatar">
                        <div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="fw-semibold text-dark small">Hasan Ali</span>
                                <span class="badge bg-success-subtle text-success border" style="font-size: 0.65rem;">Agent</span>
                            </div>
                            <small class="text-muted" style="font-size: 0.72rem;">25 minutes ago</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Row 3: Recent Transactions & Top Models -->
<div class="row g-4 mb-4">
    <!-- Recent Transactions Table -->
    <div class="col-xl-7 col-lg-12">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0">Recent Transactions</h5>
                <a href="wallets.php" class="small fw-semibold text-primary text-decoration-none">View All</a>
            </div>
            <div class="table-responsive">
                <table class="table table-custom align-middle">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>User</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Method</th>
                            <th>Status</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="fw-semibold text-muted">#10021</td>
                            <td class="fw-semibold text-dark">Rahim Uddin</td>
                            <td><span class="text-success fw-semibold">Deposit</span></td>
                            <td class="fw-bold text-dark">$100.00</td>
                            <td><span class="badge bg-light text-dark border">bKash</span></td>
                            <td><span class="badge-status badge-approved">Approved</span></td>
                            <td class="text-muted small">30 Sep 2025 10:45</td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">#10020</td>
                            <td class="fw-semibold text-dark">Jessica A.</td>
                            <td><span class="text-danger fw-semibold">Withdraw</span></td>
                            <td class="fw-bold text-dark">$250.00</td>
                            <td><span class="badge bg-light text-dark border">Bank</span></td>
                            <td><span class="badge-status badge-pending">Pending</span></td>
                            <td class="text-muted small">30 Sep 2025 09:30</td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">#10019</td>
                            <td class="fw-semibold text-dark">Karim Khan</td>
                            <td><span class="text-primary fw-semibold">Booking</span></td>
                            <td class="fw-bold text-dark">$150.00</td>
                            <td><span class="badge bg-light text-dark border">Wallet</span></td>
                            <td><span class="badge-status badge-completed">Completed</span></td>
                            <td class="text-muted small">29 Sep 2025 22:15</td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">#10018</td>
                            <td class="fw-semibold text-dark">Maria K.</td>
                            <td><span class="text-success fw-semibold">Deposit</span></td>
                            <td class="fw-bold text-dark">$200.00</td>
                            <td><span class="badge bg-light text-dark border">USDT</span></td>
                            <td><span class="badge-status badge-approved">Approved</span></td>
                            <td class="text-muted small">29 Sep 2025 21:40</td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">#10017</td>
                            <td class="fw-semibold text-dark">Hasan Ali</td>
                            <td><span class="text-danger fw-semibold">Withdraw</span></td>
                            <td class="fw-bold text-dark">$300.00</td>
                            <td><span class="badge bg-light text-dark border">Bank</span></td>
                            <td><span class="badge-status badge-completed">Completed</span></td>
                            <td class="text-muted small">29 Sep 2025 19:20</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Top Models (This Month) -->
    <div class="col-xl-5 col-lg-12">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0">Top Models (This Month)</h5>
                <a href="models.php" class="small fw-semibold text-primary text-decoration-none">View All</a>
            </div>
            <div class="table-responsive">
                <table class="table table-custom align-middle">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Model</th>
                            <th>Bookings</th>
                            <th>Earnings</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><span class="text-warning fs-5">👑</span></td>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 32px; height: 32px; object-fit: cover;" alt="Avatar">
                                    <span class="fw-semibold text-dark">Jessica A.</span>
                                </div>
                            </td>
                            <td class="fw-semibold text-secondary">125</td>
                            <td class="fw-bold text-dark">$5,420</td>
                        </tr>
                        <tr>
                            <td><span class="text-secondary fs-6 fw-bold">🥈</span></td>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://images.unsplash.com/photo-1517841905240-472988babdf9?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 32px; height: 32px; object-fit: cover;" alt="Avatar">
                                    <span class="fw-semibold text-dark">Maria K.</span>
                                </div>
                            </td>
                            <td class="fw-semibold text-secondary">98</td>
                            <td class="fw-bold text-dark">$4,850</td>
                        </tr>
                        <tr>
                            <td><span class="text-danger fs-6 fw-bold" style="color: #cd7f32 !important;">🥉</span></td>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 32px; height: 32px; object-fit: cover;" alt="Avatar">
                                    <span class="fw-semibold text-dark">Sophia L.</span>
                                </div>
                            </td>
                            <td class="fw-semibold text-secondary">78</td>
                            <td class="fw-bold text-dark">$3,680</td>
                        </tr>
                        <tr>
                            <td class="fw-bold text-muted ps-3">4</td>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 32px; height: 32px; object-fit: cover;" alt="Avatar">
                                    <span class="fw-semibold text-dark">Emma R.</span>
                                </div>
                            </td>
                            <td class="fw-semibold text-secondary">65</td>
                            <td class="fw-bold text-dark">$3,150</td>
                        </tr>
                        <tr>
                            <td class="fw-bold text-muted ps-3">5</td>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=60&h=60&fit=crop&crop=faces" class="rounded-circle" style="width: 32px; height: 32px; object-fit: cover;" alt="Avatar">
                                    <span class="fw-semibold text-dark">Olivia T.</span>
                                </div>
                            </td>
                            <td class="fw-semibold text-secondary">54</td>
                            <td class="fw-bold text-dark">$2,940</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Row 4: Top Countries, Payment Methods, and P2P Orders -->
<div class="row g-4">
    <!-- Top Countries -->
    <div class="col-xl-4 col-md-6">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0">Top Countries (Users)</h5>
                <a href="country_currency.php" class="small fw-semibold text-primary text-decoration-none">View All</a>
            </div>
            
            <div class="d-flex flex-column gap-3 mt-2">
                <div>
                    <div class="d-flex justify-content-between align-items-center small mb-1">
                        <span class="fw-semibold text-dark">🇧🇩 Bangladesh</span>
                        <div>
                            <span class="fw-bold text-dark">4,520</span>
                            <span class="text-muted ms-1">35.9%</span>
                        </div>
                    </div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-primary" role="progressbar" style="width: 35.9%"></div>
                    </div>
                </div>

                <div>
                    <div class="d-flex justify-content-between align-items-center small mb-1">
                        <span class="fw-semibold text-dark">🇲🇾 Malaysia</span>
                        <div>
                            <span class="fw-bold text-dark">2,850</span>
                            <span class="text-muted ms-1">22.6%</span>
                        </div>
                    </div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-primary" role="progressbar" style="width: 22.6%"></div>
                    </div>
                </div>

                <div>
                    <div class="d-flex justify-content-between align-items-center small mb-1">
                        <span class="fw-semibold text-dark">🇦🇪 UAE</span>
                        <div>
                            <span class="fw-bold text-dark">1,850</span>
                            <span class="text-muted ms-1">14.7%</span>
                        </div>
                    </div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-primary" role="progressbar" style="width: 14.7%"></div>
                    </div>
                </div>

                <div>
                    <div class="d-flex justify-content-between align-items-center small mb-1">
                        <span class="fw-semibold text-dark">🇸🇦 Saudi Arabia</span>
                        <div>
                            <span class="fw-bold text-dark">1,120</span>
                            <span class="text-muted ms-1">8.9%</span>
                        </div>
                    </div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-primary" role="progressbar" style="width: 8.9%"></div>
                    </div>
                </div>

                <div>
                    <div class="d-flex justify-content-between align-items-center small mb-1">
                        <span class="fw-semibold text-dark">🇺🇸 United States</span>
                        <div>
                            <span class="fw-bold text-dark">980</span>
                            <span class="text-muted ms-1">7.8%</span>
                        </div>
                    </div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-primary" role="progressbar" style="width: 7.8%"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Payment Methods Stats -->
    <div class="col-xl-4 col-md-6">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0">Payment Methods</h5>
                <a href="wallets.php" class="small fw-semibold text-primary text-decoration-none">View All</a>
            </div>
            
            <div class="table-responsive">
                <table class="table table-custom align-middle">
                    <thead>
                        <tr>
                            <th>Method</th>
                            <th>Transactions</th>
                            <th>Volume</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="badge rounded-circle p-1 bg-danger-subtle text-danger"><i class="bi bi-phone"></i></span>
                                    <span class="fw-semibold text-dark">bKash</span>
                                </div>
                            </td>
                            <td class="text-secondary">2,850</td>
                            <td class="fw-bold text-dark">$125,420</td>
                        </tr>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="badge rounded-circle p-1 bg-warning-subtle text-warning"><i class="bi bi-wallet2"></i></span>
                                    <span class="fw-semibold text-dark">Nagad</span>
                                </div>
                            </td>
                            <td class="text-secondary">1,980</td>
                            <td class="fw-bold text-dark">$98,560</td>
                        </tr>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="badge rounded-circle p-1 bg-info-subtle text-info"><i class="bi bi-bank"></i></span>
                                    <span class="fw-semibold text-dark">Bank Transfer</span>
                                </div>
                            </td>
                            <td class="text-secondary">1,520</td>
                            <td class="fw-bold text-dark">$85,330</td>
                        </tr>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="badge rounded-circle p-1 bg-success-subtle text-success"><i class="bi bi-currency-bitcoin"></i></span>
                                    <span class="fw-semibold text-dark">USDT (TRC20)</span>
                                </div>
                            </td>
                            <td class="text-secondary">980</td>
                            <td class="fw-bold text-dark">$62,450</td>
                        </tr>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="badge rounded-circle p-1 bg-secondary-subtle text-secondary"><i class="bi bi-cash"></i></span>
                                    <span class="fw-semibold text-dark">Cash</span>
                                </div>
                            </td>
                            <td class="text-secondary">420</td>
                            <td class="fw-bold text-dark">$25,880</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- P2P Orders (Latest) -->
    <div class="col-xl-4 col-md-12">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0">P2P Orders (Latest)</h5>
                <a href="p2p.php" class="small fw-semibold text-primary text-decoration-none">View All</a>
            </div>
            
            <div class="table-responsive">
                <table class="table table-custom align-middle">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>User</th>
                            <th>Agent</th>
                            <th>Amount</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="fw-semibold text-muted">#P20182</td>
                            <td class="text-dark">Rahim U.</td>
                            <td class="text-secondary">Agent 12</td>
                            <td class="fw-bold text-dark">$200</td>
                            <td><span class="badge-status badge-waiting">Waiting</span></td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">#P20183</td>
                            <td class="text-dark">Karim K.</td>
                            <td class="text-secondary">Agent 08</td>
                            <td class="fw-bold text-dark">$500</td>
                            <td><span class="badge-status badge-approved">Paid</span></td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">#P20184</td>
                            <td class="text-dark">Hasan A.</td>
                            <td class="text-secondary">Agent 04</td>
                            <td class="fw-bold text-dark">$100</td>
                            <td><span class="badge-status badge-disputed">Disputed</span></td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">#P20185</td>
                            <td class="text-dark">Maria K.</td>
                            <td class="text-secondary">Agent 06</td>
                            <td class="fw-bold text-dark">$300</td>
                            <td><span class="badge-status badge-released">Released</span></td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">#P20186</td>
                            <td class="text-dark">Sophia L.</td>
                            <td class="text-secondary">Agent 09</td>
                            <td class="fw-bold text-dark">$250</td>
                            <td><span class="badge-status badge-completed">Completed</span></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- ApexCharts Script Initialization -->
<script>
document.addEventListener("DOMContentLoaded", function () {
    // 1. Revenue Overview Area Spline Chart
    var revenueOptions = {
        series: [
            {
                name: 'Deposit',
                data: [15, 22, 28, 32, 38, 42, 40, 44, 48, 45, 42]
            },
            {
                name: 'Booking Revenue',
                data: [10, 12, 18, 22, 25, 28, 30, 32, 36, 34, 30]
            },
            {
                name: 'Platform Commission',
                data: [3, 4, 6, 8, 9, 11, 10, 12, 14, 13, 11]
            },
            {
                name: 'Agent Commission',
                data: [1, 2, 3, 3, 4, 5, 4, 5, 6, 5, 4]
            }
        ],
        chart: {
            height: 290,
            type: 'area',
            toolbar: { show: false },
            fontFamily: 'Plus Jakarta Sans, sans-serif'
        },
        colors: ['#3b82f6', '#ec4899', '#10b981', '#f59e0b'],
        dataLabels: { enabled: false },
        stroke: {
            curve: 'smooth',
            width: 2.5
        },
        fill: {
            type: 'gradient',
            gradient: {
                shadeIntensity: 1,
                opacityFrom: 0.25,
                opacityTo: 0.02,
                stops: [0, 95, 100]
            }
        },
        xaxis: {
            categories: ['Sep 1', 'Sep 5', 'Sep 10', 'Sep 15', 'Sep 20', 'Sep 25', 'Sep 30'],
            labels: {
                style: { colors: '#94a3b8', fontSize: '11px' }
            },
            axisBorder: { show: false },
            axisTicks: { show: false }
        },
        yaxis: {
            labels: {
                formatter: function (value) {
                    return '$' + value + 'K';
                },
                style: { colors: '#94a3b8', fontSize: '11px' }
            }
        },
        grid: {
            borderColor: '#f1f5f9',
            strokeDashArray: 3
        },
        legend: { show: false },
        tooltip: {
            y: {
                formatter: function (val) {
                    return '$' + val + ',000';
                }
            }
        }
    };
    var revenueChart = new ApexCharts(document.querySelector("#revenueChart"), revenueOptions);
    revenueChart.render();

    // 2. Booking Status Donut Chart
    var donutOptions = {
        series: [125, 88, 74, 35, 840, 52, 18],
        chart: {
            height: 220,
            type: 'donut',
            fontFamily: 'Plus Jakarta Sans, sans-serif'
        },
        labels: ['Pending', 'Accepted', 'Paid', 'Active', 'Completed', 'Cancelled', 'Disputed'],
        colors: ['#f97316', '#3b82f6', '#8b5cf6', '#06b6d4', '#10b981', '#ef4444', '#991b1b'],
        dataLabels: { enabled: false },
        legend: { show: false },
        plotOptions: {
            pie: {
                donut: {
                    size: '72%',
                    labels: {
                        show: true,
                        name: {
                            show: true,
                            fontSize: '12px',
                            color: '#64748b',
                            offsetY: -4
                        },
                        value: {
                            show: true,
                            fontSize: '20px',
                            fontWeight: 800,
                            color: '#0f172a',
                            offsetY: 6,
                            formatter: function () {
                                return '1,120';
                            }
                        },
                        total: {
                            show: true,
                            label: 'Total',
                            color: '#64748b',
                            formatter: function () {
                                return '1,120';
                            }
                        }
                    }
                }
            }
        }
    };
    var donutChart = new ApexCharts(document.querySelector("#bookingStatusChart"), donutOptions);
    donutChart.render();
});
</script>

<?php
renderAdminFooter();
?>
