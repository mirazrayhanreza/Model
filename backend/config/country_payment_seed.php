<?php
declare(strict_types=1);

// backend/config/country_payment_seed.php
// Master dataset of 61 Countries & Dynamic Multi-Currency Payment Methods for Modol Connect Binance-Style P2P

return [
    [
        'country_name' => 'Bangladesh',
        'iso_code' => 'BD',
        'phone_code' => '+880',
        'currency_code' => 'BDT',
        'flag' => '🇧🇩',
        'methods' => [
            ['name' => 'bKash', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 50000],
            ['name' => 'Nagad', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 50000],
            ['name' => 'Rocket', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 30000],
            ['name' => 'Upay', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 30000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 1000, 'max' => 1000000],
            ['name' => 'Cash', 'type' => 'Cash', 'min' => 500, 'max' => 500000],
        ]
    ],
    [
        'country_name' => 'India',
        'iso_code' => 'IN',
        'phone_code' => '+91',
        'currency_code' => 'INR',
        'flag' => '🇮🇳',
        'methods' => [
            ['name' => 'UPI', 'type' => 'Instant Bank', 'min' => 200, 'max' => 100000],
            ['name' => 'Google Pay', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 100000],
            ['name' => 'PhonePe', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 100000],
            ['name' => 'Paytm', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 100000],
            ['name' => 'IMPS', 'type' => 'Instant Bank', 'min' => 500, 'max' => 500000],
            ['name' => 'NEFT', 'type' => 'Bank Transfer', 'min' => 1000, 'max' => 1000000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 1000, 'max' => 1000000],
        ]
    ],
    [
        'country_name' => 'Pakistan',
        'iso_code' => 'PK',
        'phone_code' => '+92',
        'currency_code' => 'PKR',
        'flag' => '🇵🇰',
        'methods' => [
            ['name' => 'JazzCash', 'type' => 'Mobile Wallet', 'min' => 500, 'max' => 100000],
            ['name' => 'Easypaisa', 'type' => 'Mobile Wallet', 'min' => 500, 'max' => 100000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 2000, 'max' => 1000000],
        ]
    ],
    [
        'country_name' => 'Nepal',
        'iso_code' => 'NP',
        'phone_code' => '+977',
        'currency_code' => 'NPR',
        'flag' => '🇳🇵',
        'methods' => [
            ['name' => 'eSewa', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 50000],
            ['name' => 'Khalti', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 50000],
            ['name' => 'IME Pay', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 50000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 1000, 'max' => 500000],
        ]
    ],
    [
        'country_name' => 'Sri Lanka',
        'iso_code' => 'LK',
        'phone_code' => '+94',
        'currency_code' => 'LKR',
        'flag' => '🇱🇰',
        'methods' => [
            ['name' => 'FriMi', 'type' => 'Mobile Wallet', 'min' => 1000, 'max' => 100000],
            ['name' => 'Genie', 'type' => 'Mobile Wallet', 'min' => 1000, 'max' => 100000],
            ['name' => 'eZ Cash', 'type' => 'Mobile Wallet', 'min' => 500, 'max' => 50000],
            ['name' => 'mCash', 'type' => 'Mobile Wallet', 'min' => 500, 'max' => 50000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 2000, 'max' => 1000000],
        ]
    ],
    [
        'country_name' => 'Indonesia',
        'iso_code' => 'ID',
        'phone_code' => '+62',
        'currency_code' => 'IDR',
        'flag' => '🇮🇩',
        'methods' => [
            ['name' => 'GoPay', 'type' => 'Mobile Wallet', 'min' => 50000, 'max' => 10000000],
            ['name' => 'DANA', 'type' => 'Mobile Wallet', 'min' => 50000, 'max' => 10000000],
            ['name' => 'OVO', 'type' => 'Mobile Wallet', 'min' => 50000, 'max' => 10000000],
            ['name' => 'ShopeePay', 'type' => 'E-Wallet', 'min' => 50000, 'max' => 10000000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 100000, 'max' => 50000000],
        ]
    ],
    [
        'country_name' => 'Malaysia',
        'iso_code' => 'MY',
        'phone_code' => '+60',
        'currency_code' => 'MYR',
        'flag' => '🇲🇾',
        'methods' => [
            ['name' => "Touch 'n Go eWallet", 'type' => 'E-Wallet', 'min' => 20, 'max' => 5000],
            ['name' => 'Boost', 'type' => 'E-Wallet', 'min' => 20, 'max' => 5000],
            ['name' => 'GrabPay', 'type' => 'E-Wallet', 'min' => 20, 'max' => 5000],
            ['name' => 'DuitNow', 'type' => 'Instant Bank', 'min' => 10, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Singapore',
        'iso_code' => 'SG',
        'phone_code' => '+65',
        'currency_code' => 'SGD',
        'flag' => '🇸🇬',
        'methods' => [
            ['name' => 'PayNow', 'type' => 'Instant Bank', 'min' => 10, 'max' => 10000],
            ['name' => 'GrabPay', 'type' => 'E-Wallet', 'min' => 10, 'max' => 3000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Thailand',
        'iso_code' => 'TH',
        'phone_code' => '+66',
        'currency_code' => 'THB',
        'flag' => '🇹🇭',
        'methods' => [
            ['name' => 'PromptPay', 'type' => 'Instant Bank', 'min' => 100, 'max' => 100000],
            ['name' => 'TrueMoney Wallet', 'type' => 'E-Wallet', 'min' => 100, 'max' => 30000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 500, 'max' => 500000],
        ]
    ],
    [
        'country_name' => 'Philippines',
        'iso_code' => 'PH',
        'phone_code' => '+63',
        'currency_code' => 'PHP',
        'flag' => '🇵🇭',
        'methods' => [
            ['name' => 'GCash', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 50000],
            ['name' => 'Maya', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 50000],
            ['name' => 'Coins.ph', 'type' => 'E-Wallet', 'min' => 200, 'max' => 50000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 1000, 'max' => 500000],
        ]
    ],
    [
        'country_name' => 'Vietnam',
        'iso_code' => 'VN',
        'phone_code' => '+84',
        'currency_code' => 'VND',
        'flag' => '🇻🇳',
        'methods' => [
            ['name' => 'MoMo', 'type' => 'Mobile Wallet', 'min' => 50000, 'max' => 20000000],
            ['name' => 'ZaloPay', 'type' => 'E-Wallet', 'min' => 50000, 'max' => 20000000],
            ['name' => 'Viettel Money', 'type' => 'Mobile Wallet', 'min' => 50000, 'max' => 20000000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 100000, 'max' => 100000000],
        ]
    ],
    [
        'country_name' => 'Cambodia',
        'iso_code' => 'KH',
        'phone_code' => '+855',
        'currency_code' => 'KHR',
        'flag' => '🇰🇭',
        'methods' => [
            ['name' => 'Wing', 'type' => 'Mobile Wallet', 'min' => 20000, 'max' => 5000000],
            ['name' => 'Pi Pay', 'type' => 'E-Wallet', 'min' => 20000, 'max' => 5000000],
            ['name' => 'ABA Pay', 'type' => 'Instant Bank', 'min' => 10, 'max' => 10000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 20000],
        ]
    ],
    [
        'country_name' => 'Myanmar',
        'iso_code' => 'MM',
        'phone_code' => '+95',
        'currency_code' => 'MMK',
        'flag' => '🇲🇲',
        'methods' => [
            ['name' => 'Wave Money', 'type' => 'Mobile Wallet', 'min' => 10000, 'max' => 1000000],
            ['name' => 'KBZPay', 'type' => 'Mobile Wallet', 'min' => 10000, 'max' => 1000000],
            ['name' => 'AYA Pay', 'type' => 'Mobile Wallet', 'min' => 10000, 'max' => 1000000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50000, 'max' => 10000000],
        ]
    ],
    [
        'country_name' => 'China',
        'iso_code' => 'CN',
        'phone_code' => '+86',
        'currency_code' => 'CNY',
        'flag' => '🇨🇳',
        'methods' => [
            ['name' => 'Alipay', 'type' => 'E-Wallet', 'min' => 50, 'max' => 50000],
            ['name' => 'WeChat Pay', 'type' => 'E-Wallet', 'min' => 50, 'max' => 50000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 200, 'max' => 200000],
        ]
    ],
    [
        'country_name' => 'Hong Kong',
        'iso_code' => 'HK',
        'phone_code' => '+852',
        'currency_code' => 'HKD',
        'flag' => '🇭🇰',
        'methods' => [
            ['name' => 'FPS', 'type' => 'Instant Bank', 'min' => 50, 'max' => 50000],
            ['name' => 'AlipayHK', 'type' => 'E-Wallet', 'min' => 50, 'max' => 20000],
            ['name' => 'WeChat Pay HK', 'type' => 'E-Wallet', 'min' => 50, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 200, 'max' => 200000],
        ]
    ],
    [
        'country_name' => 'Japan',
        'iso_code' => 'JP',
        'phone_code' => '+81',
        'currency_code' => 'JPY',
        'flag' => '🇯🇵',
        'methods' => [
            ['name' => 'PayPay', 'type' => 'E-Wallet', 'min' => 1000, 'max' => 300000],
            ['name' => 'LINE Pay', 'type' => 'E-Wallet', 'min' => 1000, 'max' => 300000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 5000, 'max' => 1000000],
        ]
    ],
    [
        'country_name' => 'South Korea',
        'iso_code' => 'KR',
        'phone_code' => '+82',
        'currency_code' => 'KRW',
        'flag' => '🇰🇷',
        'methods' => [
            ['name' => 'Kakao Pay', 'type' => 'Mobile Wallet', 'min' => 10000, 'max' => 2000000],
            ['name' => 'Naver Pay', 'type' => 'E-Wallet', 'min' => 10000, 'max' => 2000000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 30000, 'max' => 10000000],
        ]
    ],
    [
        'country_name' => 'United Arab Emirates',
        'iso_code' => 'AE',
        'phone_code' => '+971',
        'currency_code' => 'AED',
        'flag' => '🇦🇪',
        'methods' => [
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 50000],
            ['name' => 'Cash Deposit', 'type' => 'Cash', 'min' => 100, 'max' => 100000],
            ['name' => 'e& money', 'type' => 'Mobile Wallet', 'min' => 20, 'max' => 10000],
        ]
    ],
    [
        'country_name' => 'Saudi Arabia',
        'iso_code' => 'SA',
        'phone_code' => '+966',
        'currency_code' => 'SAR',
        'flag' => '🇸🇦',
        'methods' => [
            ['name' => 'STC Pay', 'type' => 'Mobile Wallet', 'min' => 50, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 100, 'max' => 100000],
        ]
    ],
    [
        'country_name' => 'Qatar',
        'iso_code' => 'QA',
        'phone_code' => '+974',
        'currency_code' => 'QAR',
        'flag' => '🇶🇦',
        'methods' => [
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 100, 'max' => 50000],
            ['name' => 'Ooredoo Money', 'type' => 'Mobile Wallet', 'min' => 50, 'max' => 10000],
        ]
    ],
    [
        'country_name' => 'Kuwait',
        'iso_code' => 'KW',
        'phone_code' => '+965',
        'currency_code' => 'KWD',
        'flag' => '🇰🇼',
        'methods' => [
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 10, 'max' => 5000],
            ['name' => 'KNET-linked payments', 'type' => 'Instant Bank', 'min' => 10, 'max' => 10000],
        ]
    ],
    [
        'country_name' => 'Oman',
        'iso_code' => 'OM',
        'phone_code' => '+968',
        'currency_code' => 'OMR',
        'flag' => '🇴🇲',
        'methods' => [
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 10, 'max' => 5000],
            ['name' => 'Thawani', 'type' => 'Mobile Wallet', 'min' => 5, 'max' => 3000],
        ]
    ],
    [
        'country_name' => 'Bahrain',
        'iso_code' => 'BH',
        'phone_code' => '+973',
        'currency_code' => 'BHD',
        'flag' => '🇧🇭',
        'methods' => [
            ['name' => 'BenefitPay', 'type' => 'Instant Bank', 'min' => 10, 'max' => 5000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 10000],
        ]
    ],
    [
        'country_name' => 'Jordan',
        'iso_code' => 'JO',
        'phone_code' => '+962',
        'currency_code' => 'JOD',
        'flag' => '🇯🇴',
        'methods' => [
            ['name' => 'Zain Cash', 'type' => 'Mobile Wallet', 'min' => 10, 'max' => 2000],
            ['name' => 'Orange Money', 'type' => 'Mobile Wallet', 'min' => 10, 'max' => 2000],
            ['name' => 'CliQ', 'type' => 'Instant Bank', 'min' => 10, 'max' => 5000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 10000],
        ]
    ],
    [
        'country_name' => 'Türkiye',
        'iso_code' => 'TR',
        'phone_code' => '+90',
        'currency_code' => 'TRY',
        'flag' => '🇹🇷',
        'methods' => [
            ['name' => 'FAST', 'type' => 'Instant Bank', 'min' => 100, 'max' => 50000],
            ['name' => 'Papara', 'type' => 'E-Wallet', 'min' => 100, 'max' => 50000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 500, 'max' => 500000],
        ]
    ],
    [
        'country_name' => 'Egypt',
        'iso_code' => 'EG',
        'phone_code' => '+20',
        'currency_code' => 'EGP',
        'flag' => '🇪🇬',
        'methods' => [
            ['name' => 'Vodafone Cash', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 30000],
            ['name' => 'Orange Cash', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 30000],
            ['name' => 'Etisalat Cash', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 30000],
            ['name' => 'InstaPay', 'type' => 'Instant Bank', 'min' => 100, 'max' => 50000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 500, 'max' => 200000],
        ]
    ],
    [
        'country_name' => 'Morocco',
        'iso_code' => 'MA',
        'phone_code' => '+212',
        'currency_code' => 'MAD',
        'flag' => '🇲🇦',
        'methods' => [
            ['name' => 'Inwi Money', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 10000],
            ['name' => 'Orange Money', 'type' => 'Mobile Wallet', 'min' => 100, 'max' => 10000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 500, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Kenya',
        'iso_code' => 'KE',
        'phone_code' => '+254',
        'currency_code' => 'KES',
        'flag' => '🇰🇪',
        'methods' => [
            ['name' => 'M-Pesa', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 150000],
            ['name' => 'Airtel Money', 'type' => 'Mobile Wallet', 'min' => 200, 'max' => 150000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 1000, 'max' => 500000],
        ]
    ],
    [
        'country_name' => 'Tanzania',
        'iso_code' => 'TZ',
        'phone_code' => '+255',
        'currency_code' => 'TZS',
        'flag' => '🇹🇿',
        'methods' => [
            ['name' => 'M-Pesa', 'type' => 'Mobile Wallet', 'min' => 5000, 'max' => 3000000],
            ['name' => 'Tigo Pesa', 'type' => 'Mobile Wallet', 'min' => 5000, 'max' => 3000000],
            ['name' => 'Airtel Money', 'type' => 'Mobile Wallet', 'min' => 5000, 'max' => 3000000],
        ]
    ],
    [
        'country_name' => 'Uganda',
        'iso_code' => 'UG',
        'phone_code' => '+256',
        'currency_code' => 'UGX',
        'flag' => '🇺🇬',
        'methods' => [
            ['name' => 'MTN Mobile Money', 'type' => 'Mobile Wallet', 'min' => 5000, 'max' => 5000000],
            ['name' => 'Airtel Money', 'type' => 'Mobile Wallet', 'min' => 5000, 'max' => 5000000],
        ]
    ],
    [
        'country_name' => 'Ghana',
        'iso_code' => 'GH',
        'phone_code' => '+233',
        'currency_code' => 'GHS',
        'flag' => '🇬🇭',
        'methods' => [
            ['name' => 'MTN MoMo', 'type' => 'Mobile Wallet', 'min' => 20, 'max' => 10000],
            ['name' => 'Telecel Cash', 'type' => 'Mobile Wallet', 'min' => 20, 'max' => 10000],
            ['name' => 'AirtelTigo Money', 'type' => 'Mobile Wallet', 'min' => 20, 'max' => 10000],
        ]
    ],
    [
        'country_name' => 'Nigeria',
        'iso_code' => 'NG',
        'phone_code' => '+234',
        'currency_code' => 'NGN',
        'flag' => '🇳🇬',
        'methods' => [
            ['name' => 'OPay', 'type' => 'Mobile Wallet', 'min' => 1000, 'max' => 500000],
            ['name' => 'PalmPay', 'type' => 'Mobile Wallet', 'min' => 1000, 'max' => 500000],
            ['name' => 'Moniepoint', 'type' => 'Instant Bank', 'min' => 1000, 'max' => 1000000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 2000, 'max' => 5000000],
        ]
    ],
    [
        'country_name' => 'South Africa',
        'iso_code' => 'ZA',
        'phone_code' => '+27',
        'currency_code' => 'ZAR',
        'flag' => '🇿🇦',
        'methods' => [
            ['name' => 'EFT', 'type' => 'Instant Bank', 'min' => 100, 'max' => 50000],
            ['name' => 'Capitec Pay', 'type' => 'Instant Bank', 'min' => 50, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 200, 'max' => 100000],
        ]
    ],
    [
        'country_name' => 'United States',
        'iso_code' => 'US',
        'phone_code' => '+1',
        'currency_code' => 'USD',
        'flag' => '🇺🇸',
        'methods' => [
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
            ['name' => 'ACH', 'type' => 'Bank Transfer', 'min' => 10, 'max' => 25000],
            ['name' => 'Zelle', 'type' => 'Instant Bank', 'min' => 10, 'max' => 2500],
            ['name' => 'Wire', 'type' => 'Bank Transfer', 'min' => 100, 'max' => 100000],
        ]
    ],
    [
        'country_name' => 'Canada',
        'iso_code' => 'CA',
        'phone_code' => '+1',
        'currency_code' => 'CAD',
        'flag' => '🇨🇦',
        'methods' => [
            ['name' => 'Interac e-Transfer', 'type' => 'Instant Bank', 'min' => 10, 'max' => 3000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Mexico',
        'iso_code' => 'MX',
        'phone_code' => '+52',
        'currency_code' => 'MXN',
        'flag' => '🇲🇽',
        'methods' => [
            ['name' => 'SPEI', 'type' => 'Instant Bank', 'min' => 100, 'max' => 50000],
            ['name' => 'Mercado Pago', 'type' => 'E-Wallet', 'min' => 100, 'max' => 30000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 200, 'max' => 100000],
        ]
    ],
    [
        'country_name' => 'Brazil',
        'iso_code' => 'BR',
        'phone_code' => '+55',
        'currency_code' => 'BRL',
        'flag' => '🇧🇷',
        'methods' => [
            ['name' => 'PIX', 'type' => 'Instant Bank', 'min' => 20, 'max' => 50000],
            ['name' => 'Mercado Pago', 'type' => 'E-Wallet', 'min' => 20, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 100, 'max' => 100000],
        ]
    ],
    [
        'country_name' => 'Argentina',
        'iso_code' => 'AR',
        'phone_code' => '+54',
        'currency_code' => 'ARS',
        'flag' => '🇦🇷',
        'methods' => [
            ['name' => 'Mercado Pago', 'type' => 'E-Wallet', 'min' => 5000, 'max' => 500000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 10000, 'max' => 1000000],
        ]
    ],
    [
        'country_name' => 'Colombia',
        'iso_code' => 'CO',
        'phone_code' => '+57',
        'currency_code' => 'COP',
        'flag' => '🇨🇴',
        'methods' => [
            ['name' => 'Nequi', 'type' => 'Mobile Wallet', 'min' => 20000, 'max' => 3000000],
            ['name' => 'Daviplata', 'type' => 'Mobile Wallet', 'min' => 20000, 'max' => 3000000],
            ['name' => 'Bancolombia', 'type' => 'Instant Bank', 'min' => 50000, 'max' => 10000000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50000, 'max' => 10000000],
        ]
    ],
    [
        'country_name' => 'Peru',
        'iso_code' => 'PE',
        'phone_code' => '+51',
        'currency_code' => 'PEN',
        'flag' => '🇵🇪',
        'methods' => [
            ['name' => 'Yape', 'type' => 'Mobile Wallet', 'min' => 10, 'max' => 2000],
            ['name' => 'Plin', 'type' => 'Mobile Wallet', 'min' => 10, 'max' => 2000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 20000],
        ]
    ],
    [
        'country_name' => 'Chile',
        'iso_code' => 'CL',
        'phone_code' => '+56',
        'currency_code' => 'CLP',
        'flag' => '🇨🇱',
        'methods' => [
            ['name' => 'MACH', 'type' => 'Mobile Wallet', 'min' => 10000, 'max' => 500000],
            ['name' => 'Mercado Pago', 'type' => 'E-Wallet', 'min' => 10000, 'max' => 500000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20000, 'max' => 2000000],
        ]
    ],
    [
        'country_name' => 'United Kingdom',
        'iso_code' => 'GB',
        'phone_code' => '+44',
        'currency_code' => 'GBP',
        'flag' => '🇬🇧',
        'methods' => [
            ['name' => 'Faster Payments', 'type' => 'Instant Bank', 'min' => 10, 'max' => 10000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
            ['name' => 'Revolut', 'type' => 'E-Wallet', 'min' => 10, 'max' => 10000],
        ]
    ],
    [
        'country_name' => 'France',
        'iso_code' => 'FR',
        'phone_code' => '+33',
        'currency_code' => 'EUR',
        'flag' => '🇫🇷',
        'methods' => [
            ['name' => 'SEPA', 'type' => 'Instant Bank', 'min' => 10, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Germany',
        'iso_code' => 'DE',
        'phone_code' => '+49',
        'currency_code' => 'EUR',
        'flag' => '🇩🇪',
        'methods' => [
            ['name' => 'SEPA', 'type' => 'Instant Bank', 'min' => 10, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Italy',
        'iso_code' => 'IT',
        'phone_code' => '+39',
        'currency_code' => 'EUR',
        'flag' => '🇮🇹',
        'methods' => [
            ['name' => 'SEPA', 'type' => 'Instant Bank', 'min' => 10, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Spain',
        'iso_code' => 'ES',
        'phone_code' => '+34',
        'currency_code' => 'EUR',
        'flag' => '🇪🇸',
        'methods' => [
            ['name' => 'Bizum', 'type' => 'Instant Bank', 'min' => 10, 'max' => 1000],
            ['name' => 'SEPA', 'type' => 'Instant Bank', 'min' => 10, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Portugal',
        'iso_code' => 'PT',
        'phone_code' => '+351',
        'currency_code' => 'EUR',
        'flag' => '🇵🇹',
        'methods' => [
            ['name' => 'MB WAY', 'type' => 'Mobile Wallet', 'min' => 10, 'max' => 1000],
            ['name' => 'SEPA', 'type' => 'Instant Bank', 'min' => 10, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Netherlands',
        'iso_code' => 'NL',
        'phone_code' => '+31',
        'currency_code' => 'EUR',
        'flag' => '🇳🇱',
        'methods' => [
            ['name' => 'iDEAL', 'type' => 'Instant Bank', 'min' => 10, 'max' => 10000],
            ['name' => 'SEPA', 'type' => 'Instant Bank', 'min' => 10, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Belgium',
        'iso_code' => 'BE',
        'phone_code' => '+32',
        'currency_code' => 'EUR',
        'flag' => '🇧🇪',
        'methods' => [
            ['name' => 'Bancontact', 'type' => 'Instant Bank', 'min' => 10, 'max' => 10000],
            ['name' => 'SEPA', 'type' => 'Instant Bank', 'min' => 10, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Poland',
        'iso_code' => 'PL',
        'phone_code' => '+48',
        'currency_code' => 'PLN',
        'flag' => '🇵🇱',
        'methods' => [
            ['name' => 'BLIK', 'type' => 'Instant Bank', 'min' => 20, 'max' => 10000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 100000],
        ]
    ],
    [
        'country_name' => 'Romania',
        'iso_code' => 'RO',
        'phone_code' => '+40',
        'currency_code' => 'RON',
        'flag' => '🇷🇴',
        'methods' => [
            ['name' => 'Revolut', 'type' => 'E-Wallet', 'min' => 50, 'max' => 20000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 100, 'max' => 100000],
        ]
    ],
    [
        'country_name' => 'Bulgaria',
        'iso_code' => 'BG',
        'phone_code' => '+359',
        'currency_code' => 'BGN',
        'flag' => '🇧🇬',
        'methods' => [
            ['name' => 'Revolut', 'type' => 'E-Wallet', 'min' => 20, 'max' => 10000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Serbia',
        'iso_code' => 'RS',
        'phone_code' => '+381',
        'currency_code' => 'RSD',
        'flag' => '🇷🇸',
        'methods' => [
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 1000, 'max' => 500000],
        ]
    ],
    [
        'country_name' => 'Ukraine',
        'iso_code' => 'UA',
        'phone_code' => '+380',
        'currency_code' => 'UAH',
        'flag' => '🇺🇦',
        'methods' => [
            ['name' => 'PrivatBank', 'type' => 'Instant Bank', 'min' => 200, 'max' => 50000],
            ['name' => 'Monobank', 'type' => 'Instant Bank', 'min' => 200, 'max' => 50000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 500, 'max' => 200000],
        ]
    ],
    [
        'country_name' => 'Georgia',
        'iso_code' => 'GE',
        'phone_code' => '+995',
        'currency_code' => 'GEL',
        'flag' => '🇬🇪',
        'methods' => [
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 20000],
        ]
    ],
    [
        'country_name' => 'Kazakhstan',
        'iso_code' => 'KZ',
        'phone_code' => '+7',
        'currency_code' => 'KZT',
        'flag' => '🇰🇿',
        'methods' => [
            ['name' => 'Kaspi Bank/Kaspi Pay', 'type' => 'Instant Bank', 'min' => 1000, 'max' => 500000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 2000, 'max' => 1000000],
        ]
    ],
    [
        'country_name' => 'Uzbekistan',
        'iso_code' => 'UZ',
        'phone_code' => '+998',
        'currency_code' => 'UZS',
        'flag' => '🇺🇿',
        'methods' => [
            ['name' => 'Click', 'type' => 'Mobile Wallet', 'min' => 50000, 'max' => 10000000],
            ['name' => 'Payme', 'type' => 'Mobile Wallet', 'min' => 50000, 'max' => 10000000],
            ['name' => 'Uzum Bank', 'type' => 'Instant Bank', 'min' => 50000, 'max' => 20000000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 100000, 'max' => 50000000],
        ]
    ],
    [
        'country_name' => 'Azerbaijan',
        'iso_code' => 'AZ',
        'phone_code' => '+994',
        'currency_code' => 'AZN',
        'flag' => '🇦🇿',
        'methods' => [
            ['name' => 'm10', 'type' => 'Mobile Wallet', 'min' => 10, 'max' => 3000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 20, 'max' => 20000],
        ]
    ],
    [
        'country_name' => 'Australia',
        'iso_code' => 'AU',
        'phone_code' => '+61',
        'currency_code' => 'AUD',
        'flag' => '🇦🇺',
        'methods' => [
            ['name' => 'PayID', 'type' => 'Instant Bank', 'min' => 20, 'max' => 10000],
            ['name' => 'Osko', 'type' => 'Instant Bank', 'min' => 20, 'max' => 10000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'New Zealand',
        'iso_code' => 'NZ',
        'phone_code' => '+64',
        'currency_code' => 'NZD',
        'flag' => '🇳🇿',
        'methods' => [
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 50000],
        ]
    ],
    [
        'country_name' => 'Fiji',
        'iso_code' => 'FJ',
        'phone_code' => '+679',
        'currency_code' => 'FJD',
        'flag' => '🇫🇯',
        'methods' => [
            ['name' => 'M-PAiSA', 'type' => 'Mobile Wallet', 'min' => 20, 'max' => 5000],
            ['name' => 'MyCash', 'type' => 'Mobile Wallet', 'min' => 20, 'max' => 5000],
            ['name' => 'Bank Transfer', 'type' => 'Bank Transfer', 'min' => 50, 'max' => 20000],
        ]
    ]
];
