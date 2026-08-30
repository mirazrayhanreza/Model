<?php
declare(strict_types=1);

namespace App\Enums;

enum PaymentStatus: string
{
    case UNPAID = 'UNPAID';
    case PENDING = 'PENDING';
    case ESCROW_HELD = 'ESCROW_HELD';
    case RELEASED = 'RELEASED';
    case REFUNDED = 'REFUNDED';
}
