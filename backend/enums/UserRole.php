<?php
declare(strict_types=1);

namespace App\Enums;

enum UserRole: string
{
    case USER = 'USER';
    case MODEL = 'MODEL';
    case AGENT = 'CASH_AGENT';
    case ADMIN = 'ADMIN';
    case SUPER_ADMIN = 'SUPER_ADMIN';

    public function label(): string
    {
        return match ($this) {
            self::USER => 'Client User',
            self::MODEL => 'Model / Talent',
            self::AGENT => 'Cash Agent Partner',
            self::ADMIN => 'System Administrator',
            self::SUPER_ADMIN => 'Super Administrator',
        };
    }
}
