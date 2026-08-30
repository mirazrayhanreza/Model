<?php
declare(strict_types=1);

namespace App\Enums;

enum BookingStatus: string
{
    case PENDING = 'PENDING';
    case PAYMENT_RECEIVED = 'PAYMENT_RECEIVED';
    case ACCEPTED = 'ACCEPTED';
    case REJECTED = 'REJECTED';
    case IN_PROGRESS = 'IN_PROGRESS';
    case PROOF_UPLOADED = 'PROOF_UPLOADED';
    case USER_CONFIRMED = 'USER_CONFIRMED';
    case ADMIN_REVIEW = 'ADMIN_REVIEW';
    case PAYMENT_RELEASED = 'PAYMENT_RELEASED';
    case COMPLETED = 'COMPLETED';
    case CANCELLED = 'CANCELLED';
    case REFUNDED = 'REFUNDED';

    public function isFinal(): bool
    {
        return match ($this) {
            self::COMPLETED, self::CANCELLED, self::REFUNDED => true,
            default => false,
        };
    }
}
