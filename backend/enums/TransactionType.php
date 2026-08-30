<?php
declare(strict_types=1);

namespace App\Enums;

enum TransactionType: string
{
    case DEPOSIT_CREDIT = 'DEPOSIT_CREDIT';
    case WITHDRAWAL_DEBIT = 'WITHDRAWAL_DEBIT';
    case ESCROW_LOCK = 'ESCROW_LOCK';
    case ESCROW_RELEASE = 'ESCROW_RELEASE';
    case BOOKING_PAYMENT = 'BOOKING_PAYMENT';
    case COMMISSION_EARNING = 'COMMISSION_EARNING';
}
