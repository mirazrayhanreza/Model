<?php
declare(strict_types=1);

namespace App\Enums;

enum OrderStatus: string
{
    case PENDING_PAYMENT = 'PENDING_PAYMENT';
    case PAYMENT_SUBMITTED = 'PAYMENT_SUBMITTED';
    case RELEASED = 'RELEASED';
    case DISPUTED = 'DISPUTED';
    case CANCELLED = 'CANCELLED';
}
