<?php
declare(strict_types=1);

// backend/admin/layout.php
// Modol Connect - Premium SaaS Desktop-First Responsive Layout (PHP 8.2+)

function renderAdminHeader(string $title, string $activePage = 'dashboard'): void
{
    $adminName = $_SESSION['user_name'] ?? 'Admin';
    $adminRole = $_SESSION['user_role'] ?? 'Super Admin';
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= htmlspecialchars($title) ?> — Modol Connect Admin</title>
    <!-- Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- ApexCharts -->
    <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script>
    <style>
        :root {
            --sidebar-bg: #111827;
            --sidebar-border: #1f2937;
            --sidebar-text: #9ca3af;
            --sidebar-hover: #1f2937;
            --sidebar-active-bg: #4f46e5;
            --sidebar-active-gradient: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
            --main-bg: #f8fafc;
            --card-bg: #ffffff;
            --card-border: #e2e8f0;
            --text-heading: #0f172a;
            --text-body: #475569;
            --text-muted: #94a3b8;
            --accent-primary: #6366f1;
            --sidebar-width: 260px;
            --topbar-height: 70px;
        }

        body {
            font-family: 'Plus Jakarta Sans', 'Inter', -apple-system, sans-serif;
            background-color: var(--main-bg);
            color: var(--text-body);
            min-height: 100vh;
            overflow-x: hidden;
        }

        /* Sidebar Styling */
        .admin-sidebar {
            width: var(--sidebar-width);
            background-color: var(--sidebar-bg);
            border-right: 1px solid var(--sidebar-border);
            position: fixed;
            top: 0;
            left: 0;
            bottom: 0;
            z-index: 1040;
            display: flex;
            flex-direction: column;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            overflow: hidden;
        }

        .sidebar-brand-box {
            height: var(--topbar-height);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 1.25rem;
            border-bottom: 1px solid var(--sidebar-border);
            background: #0f172a;
        }

        .brand-logo-wrap {
            display: flex;
            align-items: center;
            gap: 0.65rem;
            text-decoration: none;
        }

        .brand-icon {
            width: 36px;
            height: 36px;
            background: linear-gradient(135deg, #ec4899 0%, #8b5cf6 50%, #3b82f6 100%);
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #ffffff;
            font-weight: 800;
            font-size: 1.15rem;
            box-shadow: 0 4px 10px rgba(139, 92, 246, 0.35);
        }

        .brand-title {
            color: #ffffff;
            font-size: 1.1rem;
            font-weight: 700;
            letter-spacing: -0.02em;
            margin: 0;
        }

        .brand-badge {
            background-color: rgba(99, 102, 241, 0.25);
            color: #818cf8;
            font-size: 0.68rem;
            font-weight: 600;
            padding: 0.2rem 0.5rem;
            border-radius: 6px;
            border: 1px solid rgba(99, 102, 241, 0.35);
        }

        .sidebar-nav-container {
            flex: 1;
            overflow-y: auto;
            padding: 0.75rem 0.65rem 1.5rem 0.65rem;
            scrollbar-width: thin;
            scrollbar-color: #374151 transparent;
        }

        .sidebar-nav-container::-webkit-scrollbar {
            width: 5px;
        }
        .sidebar-nav-container::-webkit-scrollbar-thumb {
            background: #374151;
            border-radius: 4px;
        }

        .nav-category {
            font-size: 0.7rem;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            font-weight: 700;
            color: #64748b;
            padding: 0.85rem 0.75rem 0.35rem 0.75rem;
        }

        .sidebar-link {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 0.62rem 0.85rem;
            color: var(--sidebar-text);
            text-decoration: none;
            font-size: 0.875rem;
            font-weight: 500;
            border-radius: 9px;
            transition: all 0.15s ease;
            margin-bottom: 2px;
        }

        .sidebar-link i {
            font-size: 1.05rem;
            width: 20px;
            text-align: center;
            color: #94a3b8;
            transition: color 0.15s ease;
        }

        .sidebar-link:hover {
            color: #ffffff;
            background-color: var(--sidebar-hover);
        }

        .sidebar-link:hover i {
            color: #ffffff;
        }

        .sidebar-link.active {
            color: #ffffff;
            background: var(--sidebar-active-gradient);
            box-shadow: 0 4px 14px rgba(79, 70, 229, 0.4);
            font-weight: 600;
        }

        .sidebar-link.active i {
            color: #ffffff;
        }

        .sidebar-badge {
            font-size: 0.7rem;
            font-weight: 600;
            padding: 0.18rem 0.5rem;
            border-radius: 6px;
            margin-left: auto;
        }

        /* Topbar Styling */
        .admin-topbar {
            height: var(--topbar-height);
            background-color: #ffffff;
            border-bottom: 1px solid var(--card-border);
            position: fixed;
            top: 0;
            right: 0;
            left: var(--sidebar-width);
            z-index: 1030;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 1.75rem;
            transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 1.25rem;
            flex: 1;
            max-width: 540px;
        }

        .btn-toggle-sidebar {
            background: none;
            border: none;
            color: #64748b;
            font-size: 1.35rem;
            cursor: pointer;
            padding: 0.25rem;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: color 0.15s;
        }

        .btn-toggle-sidebar:hover {
            color: var(--text-heading);
            background-color: #f1f5f9;
        }

        .topbar-search {
            position: relative;
            width: 100%;
        }

        .topbar-search input {
            width: 100%;
            height: 42px;
            background-color: #f1f5f9;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            padding-left: 2.5rem;
            padding-right: 1rem;
            font-size: 0.875rem;
            color: var(--text-heading);
            outline: none;
            transition: all 0.2s;
        }

        .topbar-search input:focus {
            background-color: #ffffff;
            border-color: #6366f1;
            box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
        }

        .topbar-search i {
            position: absolute;
            left: 0.85rem;
            top: 50%;
            transform: translateY(-50%);
            color: #94a3b8;
            font-size: 0.95rem;
        }

        .topbar-right {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .topbar-action-btn {
            background: none;
            border: 1px solid #e2e8f0;
            border-radius: 9px;
            width: 38px;
            height: 38px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #64748b;
            cursor: pointer;
            position: relative;
            transition: all 0.15s;
        }

        .topbar-action-btn:hover {
            background-color: #f8fafc;
            color: var(--text-heading);
            border-color: #cbd5e1;
        }

        .notification-dot {
            position: absolute;
            top: 6px;
            right: 6px;
            width: 8px;
            height: 8px;
            background-color: #ef4444;
            border-radius: 50%;
            border: 2px solid #ffffff;
        }

        .lang-btn {
            display: flex;
            align-items: center;
            gap: 0.45rem;
            padding: 0.45rem 0.75rem;
            font-size: 0.85rem;
            font-weight: 500;
            color: #334155;
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
        }

        .admin-profile-pill {
            display: flex;
            align-items: center;
            gap: 0.65rem;
            padding: 0.25rem 0.5rem 0.25rem 0.25rem;
            border-radius: 30px;
            border: 1px solid transparent;
            cursor: pointer;
            text-decoration: none;
            transition: background 0.15s;
        }

        .admin-profile-pill:hover {
            background-color: #f1f5f9;
        }

        .admin-avatar {
            width: 38px;
            height: 38px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #e0e7ff;
        }

        .admin-info {
            text-align: left;
            line-height: 1.2;
        }

        .admin-info .name {
            font-size: 0.85rem;
            font-weight: 600;
            color: var(--text-heading);
            margin: 0;
        }

        .admin-info .role {
            font-size: 0.72rem;
            color: var(--text-muted);
            margin: 0;
        }

        /* Main Content Container */
        .admin-main-wrap {
            margin-left: var(--sidebar-width);
            margin-top: var(--topbar-height);
            padding: 1.75rem 2rem 3rem 2rem;
            min-height: calc(100vh - var(--topbar-height));
            transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        /* Cards & KPI Styling */
        .card-custom {
            background-color: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 14px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.02);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .card-custom:hover {
            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
        }

        .kpi-card {
            background-color: #ffffff;
            border: 1px solid var(--card-border);
            border-radius: 14px;
            padding: 1.25rem 1.35rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: relative;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0,0,0,0.03);
            transition: all 0.2s ease;
        }

        .kpi-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.06);
        }

        .kpi-icon-wrap {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.35rem;
        }

        .kpi-icon-blue { background-color: #eff6ff; color: #3b82f6; }
        .kpi-icon-pink { background-color: #fdf2f8; color: #ec4899; }
        .kpi-icon-green { background-color: #f0fdf4; color: #10b981; }
        .kpi-icon-purple { background-color: #f5f3ff; color: #8b5cf6; }
        .kpi-icon-gold { background-color: #fffbeb; color: #f59e0b; }
        .kpi-icon-indigo { background-color: #eef2ff; color: #6366f1; }
        .kpi-icon-cyan { background-color: #ecfeff; color: #06b6d4; }
        .kpi-icon-red { background-color: #fef2f2; color: #ef4444; }

        .kpi-title {
            font-size: 0.8rem;
            color: #64748b;
            font-weight: 500;
            margin-bottom: 0.35rem;
        }

        .kpi-value {
            font-size: 1.6rem;
            font-weight: 800;
            color: var(--text-heading);
            letter-spacing: -0.03em;
            line-height: 1.1;
            margin-bottom: 0.25rem;
        }

        .kpi-trend {
            font-size: 0.75rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 0.25rem;
        }

        .trend-up { color: #10b981; }
        .trend-down { color: #ef4444; }

        /* Tables & Elements */
        .table-custom {
            margin-bottom: 0;
        }

        .table-custom th {
            font-size: 0.72rem;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            font-weight: 700;
            color: #64748b;
            background-color: #f8fafc;
            border-bottom: 1px solid #e2e8f0;
            padding: 0.85rem 1rem;
        }

        .table-custom td {
            font-size: 0.85rem;
            padding: 0.85rem 1rem;
            vertical-align: middle;
            border-bottom: 1px solid #f1f5f9;
            color: #334155;
        }

        .table-custom tbody tr:hover {
            background-color: #f8fafc;
        }

        .badge-status {
            padding: 0.3rem 0.65rem;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.72rem;
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
        }

        .badge-approved { background-color: #dcfce7; color: #15803d; }
        .badge-pending { background-color: #fef3c7; color: #b45309; }
        .badge-completed { background-color: #e0e7ff; color: #4338ca; }
        .badge-disputed { background-color: #fee2e2; color: #b91c1c; }
        .badge-waiting { background-color: #fef9c3; color: #854d0e; }
        .badge-released { background-color: #ccfbf1; color: #0f766e; }

        /* Responsive layout */
        @media (max-width: 991px) {
            .admin-sidebar {
                transform: translateX(-100%);
            }
            .admin-sidebar.show {
                transform: translateX(0);
            }
            .admin-topbar {
                left: 0;
            }
            .admin-main-wrap {
                margin-left: 0;
                padding: 1.25rem 1rem;
            }
        }
    </style>
</head>
<body>

<!-- Left Navigation Sidebar -->
<aside class="admin-sidebar" id="adminSidebar">
    <!-- Brand Box -->
    <div class="sidebar-brand-box">
        <a href="dashboard.php" class="brand-logo-wrap">
            <div class="brand-icon">M</div>
            <div>
                <h5 class="brand-title">Modol Connect</h5>
            </div>
        </a>
        <span class="brand-badge">Admin Panel</span>
    </div>

    <!-- Navigation Menu Items -->
    <div class="sidebar-nav-container">
        <!-- Main -->
        <a href="dashboard.php" class="sidebar-link <?= $activePage === 'dashboard' ? 'active' : '' ?>">
            <i class="bi bi-grid-1x2-fill"></i>
            <span>Dashboard</span>
        </a>

        <div class="nav-category">Management</div>

        <a href="users.php" class="sidebar-link <?= $activePage === 'users' ? 'active' : '' ?>">
            <i class="bi bi-people-fill"></i>
            <span>User Management</span>
            <span class="badge bg-secondary sidebar-badge">12.5k</span>
        </a>

        <a href="models.php" class="sidebar-link <?= $activePage === 'models' ? 'active' : '' ?>">
            <i class="bi bi-star-fill"></i>
            <span>Model Management</span>
            <span class="badge bg-danger sidebar-badge">3,240</span>
        </a>

        <a href="verification.php" class="sidebar-link <?= $activePage === 'verification' ? 'active' : '' ?>">
            <i class="bi bi-shield-check"></i>
            <span>Verification</span>
            <span class="badge bg-warning text-dark sidebar-badge">18 New</span>
        </a>

        <a href="bookings.php" class="sidebar-link <?= $activePage === 'bookings' ? 'active' : '' ?>">
            <i class="bi bi-calendar2-check-fill"></i>
            <span>Booking Management</span>
        </a>

        <a href="live_gps.php" class="sidebar-link <?= $activePage === 'live_gps' ? 'active' : '' ?>">
            <i class="bi bi-geo-alt-fill text-danger"></i>
            <span>Live GPS Tracking</span>
            <span class="badge bg-success sidebar-badge"><i class="bi bi-broadcast me-1"></i>LIVE</span>
        </a>

        <div class="nav-category">Finance & Network</div>

        <a href="wallets.php" class="sidebar-link <?= $activePage === 'wallets' ? 'active' : '' ?>">
            <i class="bi bi-wallet2"></i>
            <span>Wallet & Finance</span>
        </a>

        <a href="payments.php" class="sidebar-link <?= $activePage === 'payments' ? 'active' : '' ?>">
            <i class="bi bi-credit-card-2-front-fill"></i>
            <span>Payment Gateways</span>
            <span class="badge bg-success sidebar-badge">Config</span>
        </a>

        <a href="deposits.php" class="sidebar-link <?= $activePage === 'deposits' ? 'active' : '' ?>">
            <i class="bi bi-box-arrow-in-down-right"></i>
            <span>Deposits</span>
        </a>

        <a href="withdrawals.php" class="sidebar-link <?= $activePage === 'withdrawals' ? 'active' : '' ?>">
            <i class="bi bi-box-arrow-up-right"></i>
            <span>Withdrawals</span>
        </a>

        <a href="p2p.php" class="sidebar-link <?= $activePage === 'p2p' ? 'active' : '' ?>">
            <i class="bi bi-arrow-left-right"></i>
            <span>P2P Network</span>
            <span class="badge bg-primary sidebar-badge">Live</span>
        </a>

        <a href="cash_agents.php" class="sidebar-link <?= $activePage === 'cash_agents' ? 'active' : '' ?>">
            <i class="bi bi-shop"></i>
            <span>Cash Agents</span>
        </a>

        <a href="disputes.php" class="sidebar-link <?= $activePage === 'disputes' ? 'active' : '' ?>">
            <i class="bi bi-exclamation-triangle-fill"></i>
            <span>Disputes</span>
            <span class="badge bg-danger sidebar-badge">12</span>
        </a>

        <div class="nav-category">Communication & System</div>

        <a href="firebase.php" class="sidebar-link <?= $activePage === 'firebase' ? 'active' : '' ?>">
            <i class="bi bi-fire text-warning"></i>
            <span>Firebase Hub</span>
            <span class="badge bg-warning text-dark sidebar-badge">Cloud</span>
        </a>

        <a href="chats.php" class="sidebar-link <?= $activePage === 'chats' ? 'active' : '' ?>">
            <i class="bi bi-chat-dots-fill"></i>
            <span>Chat Management</span>
        </a>

        <a href="calls.php" class="sidebar-link <?= $activePage === 'calls' ? 'active' : '' ?>">
            <i class="bi bi-telephone-fill"></i>
            <span>Call Management</span>
        </a>

        <a href="reviews.php" class="sidebar-link <?= $activePage === 'reviews' ? 'active' : '' ?>">
            <i class="bi bi-stars"></i>
            <span>Reviews</span>
        </a>

        <a href="reports.php" class="sidebar-link <?= $activePage === 'reports' ? 'active' : '' ?>">
            <i class="bi bi-bar-chart-line-fill"></i>
            <span>Reports & Analytics</span>
        </a>

        <a href="support.php" class="sidebar-link <?= $activePage === 'support' ? 'active' : '' ?>">
            <i class="bi bi-ticket-perforated-fill"></i>
            <span>Support Tickets</span>
        </a>

        <a href="country_currency.php" class="sidebar-link <?= ($activePage === 'country_currency' || $activePage === 'countries') ? 'active' : '' ?>">
            <i class="bi bi-globe2"></i>
            <span>Country & Methods</span>
            <span class="badge bg-danger sidebar-badge">Binance</span>
        </a>

        <a href="staff.php" class="sidebar-link <?= $activePage === 'staff' ? 'active' : '' ?>">
            <i class="bi bi-person-gear"></i>
            <span>Admin & Staff</span>
        </a>

        <a href="settings.php" class="sidebar-link <?= $activePage === 'settings' ? 'active' : '' ?>">
            <i class="bi bi-gear-fill"></i>
            <span>Settings</span>
        </a>

        <a href="activity_logs.php" class="sidebar-link <?= $activePage === 'activity_logs' ? 'active' : '' ?>">
            <i class="bi bi-journal-text"></i>
            <span>Activity Logs</span>
        </a>
    </div>
</aside>

<!-- Topbar Navigation Header -->
<header class="admin-topbar">
    <div class="topbar-left">
        <button class="btn-toggle-sidebar" id="sidebarToggle" aria-label="Toggle Navigation">
            <i class="bi bi-list"></i>
        </button>
        <div class="topbar-search">
            <i class="bi bi-search"></i>
            <input type="text" placeholder="Search users, models, bookings, orders, agents..." id="globalSearchInput">
        </div>
    </div>

    <div class="topbar-right">
        <!-- Language Selector -->
        <div class="dropdown">
            <button class="lang-btn dropdown-toggle" data-bs-toggle="dropdown">
                <span>🇬🇧</span> English
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                <li><a class="dropdown-item active" href="?lang=en">🇬🇧 English</a></li>
                <li><a class="dropdown-item" href="?lang=bn">🇧🇩 বাংলা (Bengali)</a></li>
                <li><a class="dropdown-item" href="?lang=ar">🇦🇪 العربية (Arabic)</a></li>
                <li><a class="dropdown-item" href="?lang=zh">🇨🇳 中文 (Chinese)</a></li>
            </ul>
        </div>

        <!-- Notification Bell -->
        <div class="dropdown">
            <button class="topbar-action-btn" data-bs-toggle="dropdown" title="Notifications">
                <i class="bi bi-bell"></i>
                <span class="notification-dot"></span>
            </button>
            <div class="dropdown-menu dropdown-menu-end shadow-lg p-0" style="width: 320px;">
                <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
                    <h6 class="mb-0 fw-bold">Notifications</h6>
                    <span class="badge bg-danger rounded-pill">12 New</span>
                </div>
                <div class="list-group list-group-flush small" style="max-height: 280px; overflow-y: auto;">
                    <a href="verification.php" class="list-group-item list-group-item-action p-2">
                        <div class="d-flex align-items-center gap-2">
                            <i class="bi bi-person-check text-warning fs-5"></i>
                            <div>
                                <p class="mb-0 fw-semibold">Jessica A. submitted KYC</p>
                                <small class="text-muted">2 mins ago</small>
                            </div>
                        </div>
                    </a>
                    <a href="deposits.php" class="list-group-item list-group-item-action p-2">
                        <div class="d-flex align-items-center gap-2">
                            <i class="bi bi-cash-stack text-success fs-5"></i>
                            <div>
                                <p class="mb-0 fw-semibold">Deposit ৳10,000 via bKash</p>
                                <small class="text-muted">5 mins ago</small>
                            </div>
                        </div>
                    </a>
                    <a href="disputes.php" class="list-group-item list-group-item-action p-2">
                        <div class="d-flex align-items-center gap-2">
                            <i class="bi bi-exclamation-octagon text-danger fs-5"></i>
                            <div>
                                <p class="mb-0 fw-semibold">Dispute opened for order #P20184</p>
                                <small class="text-muted">12 mins ago</small>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="p-2 border-top text-center">
                    <a href="notifications.php" class="text-primary small fw-semibold text-decoration-none">View All Notifications</a>
                </div>
            </div>
        </div>

        <!-- Fullscreen Toggle -->
        <button class="topbar-action-btn d-none d-sm-flex" onclick="toggleFullScreen()" title="Toggle Fullscreen">
            <i class="bi bi-arrows-fullscreen"></i>
        </button>

        <!-- Admin Profile Dropdown -->
        <div class="dropdown">
            <div class="admin-profile-pill" data-bs-toggle="dropdown">
                <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&h=100&fit=crop&crop=faces" class="admin-avatar" alt="Avatar">
                <div class="admin-info d-none d-md-block">
                    <p class="name"><?= htmlspecialchars($adminName) ?></p>
                    <p class="role"><?= htmlspecialchars($adminRole) ?></p>
                </div>
                <i class="bi bi-chevron-down text-muted small ms-1"></i>
            </div>
            <ul class="dropdown-menu dropdown-menu-end shadow-lg py-2">
                <li><h6 class="dropdown-header">Signed in as <strong><?= htmlspecialchars($adminName) ?></strong></h6></li>
                <li><a class="dropdown-item" href="staff.php"><i class="bi bi-person me-2"></i>My Profile</a></li>
                <li><a class="dropdown-item" href="firebase.php"><i class="bi bi-fire text-warning me-2"></i>Firebase Hub</a></li>
                <li><a class="dropdown-item" href="settings.php"><i class="bi bi-gear me-2"></i>System Settings</a></li>
                <li><a class="dropdown-item" href="activity_logs.php"><i class="bi bi-clock-history me-2"></i>Audit Logs</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="logout.php"><i class="bi bi-box-arrow-right me-2"></i>Sign Out</a></li>
            </ul>
        </div>
    </div>
</header>

<!-- Main Wrapper -->
<main class="admin-main-wrap">
<?php
}

function renderAdminFooter(): void
{
?>
</main>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Sidebar Toggle for Mobile & Responsive
    const sidebarToggle = document.getElementById('sidebarToggle');
    const adminSidebar = document.getElementById('adminSidebar');
    if (sidebarToggle && adminSidebar) {
        sidebarToggle.addEventListener('click', () => {
            adminSidebar.classList.toggle('show');
        });
    }

    // Fullscreen Toggle
    function toggleFullScreen() {
        if (!document.fullscreenElement) {
            document.documentElement.requestFullscreen().catch(err => {
                console.error(`Error attempting fullscreen: ${err.message}`);
            });
        } else {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            }
        }
    }
</script>
</body>
</html>
<?php
}
