package com.example.data

/**
 * Master Country & Dynamic Payment Methods Data Provider for Modol Connect
 * Binance-Style Architecture with 61 Supported Countries & 1,000+ Dynamic Payment Methods.
 */
object CountryPaymentMaster {

    val allCountries: List<CountryData> = listOf(
        CountryData(1, "Bangladesh", "BD", "+880", "BDT", "🇧🇩", "Active", listOf(
            CountryPaymentMethod(101, 1, "bKash", "Mobile Wallet", minAmount = 100.0, maxAmount = 50000.0),
            CountryPaymentMethod(102, 1, "Nagad", "Mobile Wallet", minAmount = 100.0, maxAmount = 50000.0),
            CountryPaymentMethod(103, 1, "Rocket", "Mobile Wallet", minAmount = 100.0, maxAmount = 30000.0),
            CountryPaymentMethod(104, 1, "Upay", "Mobile Wallet", minAmount = 100.0, maxAmount = 30000.0),
            CountryPaymentMethod(105, 1, "Bank Transfer", "Bank Transfer", minAmount = 1000.0, maxAmount = 1000000.0),
            CountryPaymentMethod(106, 1, "Cash", "Cash", minAmount = 500.0, maxAmount = 500000.0)
        )),
        CountryData(2, "India", "IN", "+91", "INR", "🇮🇳", "Active", listOf(
            CountryPaymentMethod(201, 2, "UPI", "Instant Bank", minAmount = 200.0, maxAmount = 100000.0),
            CountryPaymentMethod(202, 2, "Google Pay", "Mobile Wallet", minAmount = 200.0, maxAmount = 100000.0),
            CountryPaymentMethod(203, 2, "PhonePe", "Mobile Wallet", minAmount = 200.0, maxAmount = 100000.0),
            CountryPaymentMethod(204, 2, "Paytm", "Mobile Wallet", minAmount = 200.0, maxAmount = 100000.0),
            CountryPaymentMethod(205, 2, "IMPS", "Instant Bank", minAmount = 500.0, maxAmount = 500000.0),
            CountryPaymentMethod(206, 2, "NEFT", "Bank Transfer", minAmount = 1000.0, maxAmount = 1000000.0),
            CountryPaymentMethod(207, 2, "Bank Transfer", "Bank Transfer", minAmount = 1000.0, maxAmount = 1000000.0)
        )),
        CountryData(3, "Pakistan", "PK", "+92", "PKR", "🇵🇰", "Active", listOf(
            CountryPaymentMethod(301, 3, "JazzCash", "Mobile Wallet", minAmount = 500.0, maxAmount = 100000.0),
            CountryPaymentMethod(302, 3, "Easypaisa", "Mobile Wallet", minAmount = 500.0, maxAmount = 100000.0),
            CountryPaymentMethod(303, 3, "Bank Transfer", "Bank Transfer", minAmount = 2000.0, maxAmount = 1000000.0)
        )),
        CountryData(4, "Nepal", "NP", "+977", "NPR", "🇳🇵", "Active", listOf(
            CountryPaymentMethod(401, 4, "eSewa", "Mobile Wallet", minAmount = 200.0, maxAmount = 50000.0),
            CountryPaymentMethod(402, 4, "Khalti", "Mobile Wallet", minAmount = 200.0, maxAmount = 50000.0),
            CountryPaymentMethod(403, 4, "IME Pay", "Mobile Wallet", minAmount = 200.0, maxAmount = 50000.0),
            CountryPaymentMethod(404, 4, "Bank Transfer", "Bank Transfer", minAmount = 1000.0, maxAmount = 500000.0)
        )),
        CountryData(5, "Sri Lanka", "LK", "+94", "LKR", "🇱🇰", "Active", listOf(
            CountryPaymentMethod(501, 5, "FriMi", "Mobile Wallet", minAmount = 1000.0, maxAmount = 100000.0),
            CountryPaymentMethod(502, 5, "Genie", "Mobile Wallet", minAmount = 1000.0, maxAmount = 100000.0),
            CountryPaymentMethod(503, 5, "eZ Cash", "Mobile Wallet", minAmount = 500.0, maxAmount = 50000.0),
            CountryPaymentMethod(504, 5, "mCash", "Mobile Wallet", minAmount = 500.0, maxAmount = 50000.0),
            CountryPaymentMethod(505, 5, "Bank Transfer", "Bank Transfer", minAmount = 2000.0, maxAmount = 1000000.0)
        )),
        CountryData(6, "Indonesia", "ID", "+62", "IDR", "🇮🇩", "Active", listOf(
            CountryPaymentMethod(601, 6, "GoPay", "Mobile Wallet", minAmount = 50000.0, maxAmount = 10000000.0),
            CountryPaymentMethod(602, 6, "DANA", "Mobile Wallet", minAmount = 50000.0, maxAmount = 10000000.0),
            CountryPaymentMethod(603, 6, "OVO", "Mobile Wallet", minAmount = 50000.0, maxAmount = 10000000.0),
            CountryPaymentMethod(604, 6, "ShopeePay", "E-Wallet", minAmount = 50000.0, maxAmount = 10000000.0),
            CountryPaymentMethod(605, 6, "Bank Transfer", "Bank Transfer", minAmount = 100000.0, maxAmount = 50000000.0)
        )),
        CountryData(7, "Malaysia", "MY", "+60", "MYR", "🇲🇾", "Active", listOf(
            CountryPaymentMethod(701, 7, "Touch 'n Go eWallet", "E-Wallet", minAmount = 20.0, maxAmount = 5000.0),
            CountryPaymentMethod(702, 7, "Boost", "E-Wallet", minAmount = 20.0, maxAmount = 5000.0),
            CountryPaymentMethod(703, 7, "GrabPay", "E-Wallet", minAmount = 20.0, maxAmount = 5000.0),
            CountryPaymentMethod(704, 7, "DuitNow", "Instant Bank", minAmount = 10.0, maxAmount = 20000.0),
            CountryPaymentMethod(705, 7, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 50000.0)
        )),
        CountryData(8, "Singapore", "SG", "+65", "SGD", "🇸🇬", "Active", listOf(
            CountryPaymentMethod(801, 8, "PayNow", "Instant Bank", minAmount = 10.0, maxAmount = 10000.0),
            CountryPaymentMethod(802, 8, "GrabPay", "E-Wallet", minAmount = 10.0, maxAmount = 3000.0),
            CountryPaymentMethod(803, 8, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 50000.0)
        )),
        CountryData(9, "Thailand", "TH", "+66", "THB", "🇹🇭", "Active", listOf(
            CountryPaymentMethod(901, 9, "PromptPay", "Instant Bank", minAmount = 100.0, maxAmount = 100000.0),
            CountryPaymentMethod(902, 9, "TrueMoney Wallet", "E-Wallet", minAmount = 100.0, maxAmount = 30000.0),
            CountryPaymentMethod(903, 9, "Bank Transfer", "Bank Transfer", minAmount = 500.0, maxAmount = 500000.0)
        )),
        CountryData(10, "Philippines", "PH", "+63", "PHP", "🇵🇭", "Active", listOf(
            CountryPaymentMethod(1001, 10, "GCash", "Mobile Wallet", minAmount = 200.0, maxAmount = 50000.0),
            CountryPaymentMethod(1002, 10, "Maya", "Mobile Wallet", minAmount = 200.0, maxAmount = 50000.0),
            CountryPaymentMethod(1003, 10, "Coins.ph", "E-Wallet", minAmount = 200.0, maxAmount = 50000.0),
            CountryPaymentMethod(1004, 10, "Bank Transfer", "Bank Transfer", minAmount = 1000.0, maxAmount = 500000.0)
        )),
        CountryData(11, "Vietnam", "VN", "+84", "VND", "🇻🇳", "Active", listOf(
            CountryPaymentMethod(1101, 11, "MoMo", "Mobile Wallet", minAmount = 50000.0, maxAmount = 20000000.0),
            CountryPaymentMethod(1102, 11, "ZaloPay", "E-Wallet", minAmount = 50000.0, maxAmount = 20000000.0),
            CountryPaymentMethod(1103, 11, "Viettel Money", "Mobile Wallet", minAmount = 50000.0, maxAmount = 20000000.0),
            CountryPaymentMethod(1104, 11, "Bank Transfer", "Bank Transfer", minAmount = 100000.0, maxAmount = 100000000.0)
        )),
        CountryData(12, "Cambodia", "KH", "+855", "KHR", "🇰🇭", "Active", listOf(
            CountryPaymentMethod(1201, 12, "Wing", "Mobile Wallet", minAmount = 20000.0, maxAmount = 5000000.0),
            CountryPaymentMethod(1202, 12, "Pi Pay", "E-Wallet", minAmount = 20000.0, maxAmount = 5000000.0),
            CountryPaymentMethod(1203, 12, "ABA Pay", "Instant Bank", minAmount = 10.0, maxAmount = 10000.0),
            CountryPaymentMethod(1204, 12, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 20000.0)
        )),
        CountryData(13, "Myanmar", "MM", "+95", "MMK", "🇲🇲", "Active", listOf(
            CountryPaymentMethod(1301, 13, "Wave Money", "Mobile Wallet", minAmount = 10000.0, maxAmount = 1000000.0),
            CountryPaymentMethod(1302, 13, "KBZPay", "Mobile Wallet", minAmount = 10000.0, maxAmount = 1000000.0),
            CountryPaymentMethod(1303, 13, "AYA Pay", "Mobile Wallet", minAmount = 10000.0, maxAmount = 1000000.0),
            CountryPaymentMethod(1304, 13, "Bank Transfer", "Bank Transfer", minAmount = 50000.0, maxAmount = 10000000.0)
        )),
        CountryData(14, "China", "CN", "+86", "CNY", "🇨🇳", "Active", listOf(
            CountryPaymentMethod(1401, 14, "Alipay", "E-Wallet", minAmount = 50.0, maxAmount = 50000.0),
            CountryPaymentMethod(1402, 14, "WeChat Pay", "E-Wallet", minAmount = 50.0, maxAmount = 50000.0),
            CountryPaymentMethod(1403, 14, "Bank Transfer", "Bank Transfer", minAmount = 200.0, maxAmount = 200000.0)
        )),
        CountryData(15, "Hong Kong", "HK", "+852", "HKD", "🇭🇰", "Active", listOf(
            CountryPaymentMethod(1501, 15, "FPS", "Instant Bank", minAmount = 50.0, maxAmount = 50000.0),
            CountryPaymentMethod(1502, 15, "AlipayHK", "E-Wallet", minAmount = 50.0, maxAmount = 20000.0),
            CountryPaymentMethod(1503, 15, "WeChat Pay HK", "E-Wallet", minAmount = 50.0, maxAmount = 20000.0),
            CountryPaymentMethod(1504, 15, "Bank Transfer", "Bank Transfer", minAmount = 200.0, maxAmount = 200000.0)
        )),
        CountryData(16, "Japan", "JP", "+81", "JPY", "🇯🇵", "Active", listOf(
            CountryPaymentMethod(1601, 16, "PayPay", "E-Wallet", minAmount = 1000.0, maxAmount = 300000.0),
            CountryPaymentMethod(1602, 16, "LINE Pay", "E-Wallet", minAmount = 1000.0, maxAmount = 300000.0),
            CountryPaymentMethod(1603, 16, "Bank Transfer", "Bank Transfer", minAmount = 5000.0, maxAmount = 1000000.0)
        )),
        CountryData(17, "South Korea", "KR", "+82", "KRW", "🇰🇷", "Active", listOf(
            CountryPaymentMethod(1701, 17, "Kakao Pay", "Mobile Wallet", minAmount = 10000.0, maxAmount = 2000000.0),
            CountryPaymentMethod(1702, 17, "Naver Pay", "E-Wallet", minAmount = 10000.0, maxAmount = 2000000.0),
            CountryPaymentMethod(1703, 17, "Bank Transfer", "Bank Transfer", minAmount = 30000.0, maxAmount = 10000000.0)
        )),
        CountryData(18, "United Arab Emirates", "AE", "+971", "AED", "🇦🇪", "Active", listOf(
            CountryPaymentMethod(1801, 18, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 50000.0),
            CountryPaymentMethod(1802, 18, "Cash Deposit", "Cash", minAmount = 100.0, maxAmount = 100000.0),
            CountryPaymentMethod(1803, 18, "e& money", "Mobile Wallet", minAmount = 20.0, maxAmount = 10000.0)
        )),
        CountryData(19, "Saudi Arabia", "SA", "+966", "SAR", "🇸🇦", "Active", listOf(
            CountryPaymentMethod(1901, 19, "STC Pay", "Mobile Wallet", minAmount = 50.0, maxAmount = 20000.0),
            CountryPaymentMethod(1902, 19, "Bank Transfer", "Bank Transfer", minAmount = 100.0, maxAmount = 100000.0)
        )),
        CountryData(20, "Qatar", "QA", "+974", "QAR", "🇶🇦", "Active", listOf(
            CountryPaymentMethod(2001, 20, "Bank Transfer", "Bank Transfer", minAmount = 100.0, maxAmount = 50000.0),
            CountryPaymentMethod(2002, 20, "Ooredoo Money", "Mobile Wallet", minAmount = 50.0, maxAmount = 10000.0)
        )),
        CountryData(21, "Kuwait", "KW", "+965", "KWD", "🇰🇼", "Active", listOf(
            CountryPaymentMethod(2101, 21, "Bank Transfer", "Bank Transfer", minAmount = 10.0, maxAmount = 5000.0),
            CountryPaymentMethod(2102, 21, "KNET-linked payments", "Instant Bank", minAmount = 10.0, maxAmount = 10000.0)
        )),
        CountryData(22, "Oman", "OM", "+968", "OMR", "🇴🇲", "Active", listOf(
            CountryPaymentMethod(2201, 22, "Bank Transfer", "Bank Transfer", minAmount = 10.0, maxAmount = 5000.0),
            CountryPaymentMethod(2202, 22, "Thawani", "Mobile Wallet", minAmount = 5.0, maxAmount = 3000.0)
        )),
        CountryData(23, "Bahrain", "BH", "+973", "BHD", "🇧🇭", "Active", listOf(
            CountryPaymentMethod(2301, 23, "BenefitPay", "Instant Bank", minAmount = 10.0, maxAmount = 5000.0),
            CountryPaymentMethod(2302, 23, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 10000.0)
        )),
        CountryData(24, "Jordan", "JO", "+962", "JOD", "🇯🇴", "Active", listOf(
            CountryPaymentMethod(2401, 24, "Zain Cash", "Mobile Wallet", minAmount = 10.0, maxAmount = 2000.0),
            CountryPaymentMethod(2402, 24, "Orange Money", "Mobile Wallet", minAmount = 10.0, maxAmount = 2000.0),
            CountryPaymentMethod(2403, 24, "CliQ", "Instant Bank", minAmount = 10.0, maxAmount = 5000.0),
            CountryPaymentMethod(2404, 24, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 10000.0)
        )),
        CountryData(25, "Türkiye", "TR", "+90", "TRY", "🇹🇷", "Active", listOf(
            CountryPaymentMethod(2501, 25, "FAST", "Instant Bank", minAmount = 100.0, maxAmount = 50000.0),
            CountryPaymentMethod(2502, 25, "Papara", "E-Wallet", minAmount = 100.0, maxAmount = 50000.0),
            CountryPaymentMethod(2503, 25, "Bank Transfer", "Bank Transfer", minAmount = 500.0, maxAmount = 500000.0)
        )),
        CountryData(26, "Egypt", "EG", "+20", "EGP", "🇪🇬", "Active", listOf(
            CountryPaymentMethod(2601, 26, "Vodafone Cash", "Mobile Wallet", minAmount = 100.0, maxAmount = 30000.0),
            CountryPaymentMethod(2602, 26, "Orange Cash", "Mobile Wallet", minAmount = 100.0, maxAmount = 30000.0),
            CountryPaymentMethod(2603, 26, "Etisalat Cash", "Mobile Wallet", minAmount = 100.0, maxAmount = 30000.0),
            CountryPaymentMethod(2604, 26, "InstaPay", "Instant Bank", minAmount = 100.0, maxAmount = 50000.0),
            CountryPaymentMethod(2605, 26, "Bank Transfer", "Bank Transfer", minAmount = 500.0, maxAmount = 200000.0)
        )),
        CountryData(27, "Morocco", "MA", "+212", "MAD", "🇲🇦", "Active", listOf(
            CountryPaymentMethod(2701, 27, "Inwi Money", "Mobile Wallet", minAmount = 100.0, maxAmount = 10000.0),
            CountryPaymentMethod(2702, 27, "Orange Money", "Mobile Wallet", minAmount = 100.0, maxAmount = 10000.0),
            CountryPaymentMethod(2703, 27, "Bank Transfer", "Bank Transfer", minAmount = 500.0, maxAmount = 50000.0)
        )),
        CountryData(28, "Kenya", "KE", "+254", "KES", "🇰🇪", "Active", listOf(
            CountryPaymentMethod(2801, 28, "M-Pesa", "Mobile Wallet", minAmount = 200.0, maxAmount = 150000.0),
            CountryPaymentMethod(2802, 28, "Airtel Money", "Mobile Wallet", minAmount = 200.0, maxAmount = 150000.0),
            CountryPaymentMethod(2803, 28, "Bank Transfer", "Bank Transfer", minAmount = 1000.0, maxAmount = 500000.0)
        )),
        CountryData(29, "Tanzania", "TZ", "+255", "TZS", "🇹🇿", "Active", listOf(
            CountryPaymentMethod(2901, 29, "M-Pesa", "Mobile Wallet", minAmount = 5000.0, maxAmount = 3000000.0),
            CountryPaymentMethod(2902, 29, "Tigo Pesa", "Mobile Wallet", minAmount = 5000.0, maxAmount = 3000000.0),
            CountryPaymentMethod(2903, 29, "Airtel Money", "Mobile Wallet", minAmount = 5000.0, maxAmount = 3000000.0)
        )),
        CountryData(30, "Uganda", "UG", "+256", "UGX", "🇺🇬", "Active", listOf(
            CountryPaymentMethod(3001, 30, "MTN Mobile Money", "Mobile Wallet", minAmount = 5000.0, maxAmount = 5000000.0),
            CountryPaymentMethod(3002, 30, "Airtel Money", "Mobile Wallet", minAmount = 5000.0, maxAmount = 5000000.0)
        )),
        CountryData(31, "Ghana", "GH", "+233", "GHS", "🇬🇭", "Active", listOf(
            CountryPaymentMethod(3101, 31, "MTN MoMo", "Mobile Wallet", minAmount = 20.0, maxAmount = 10000.0),
            CountryPaymentMethod(3102, 31, "Telecel Cash", "Mobile Wallet", minAmount = 20.0, maxAmount = 10000.0),
            CountryPaymentMethod(3103, 31, "AirtelTigo Money", "Mobile Wallet", minAmount = 20.0, maxAmount = 10000.0)
        )),
        CountryData(32, "Nigeria", "NG", "+234", "NGN", "🇳🇬", "Active", listOf(
            CountryPaymentMethod(3201, 32, "OPay", "Mobile Wallet", minAmount = 1000.0, maxAmount = 500000.0),
            CountryPaymentMethod(3202, 32, "PalmPay", "Mobile Wallet", minAmount = 1000.0, maxAmount = 500000.0),
            CountryPaymentMethod(3203, 32, "Moniepoint", "Instant Bank", minAmount = 1000.0, maxAmount = 1000000.0),
            CountryPaymentMethod(3204, 32, "Bank Transfer", "Bank Transfer", minAmount = 2000.0, maxAmount = 5000000.0)
        )),
        CountryData(33, "South Africa", "ZA", "+27", "ZAR", "🇿🇦", "Active", listOf(
            CountryPaymentMethod(3301, 33, "EFT", "Instant Bank", minAmount = 100.0, maxAmount = 50000.0),
            CountryPaymentMethod(3302, 33, "Capitec Pay", "Instant Bank", minAmount = 50.0, maxAmount = 20000.0),
            CountryPaymentMethod(3303, 33, "Bank Transfer", "Bank Transfer", minAmount = 200.0, maxAmount = 100000.0)
        )),
        CountryData(34, "United States", "US", "+1", "USD", "🇺🇸", "Active", listOf(
            CountryPaymentMethod(3401, 34, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0),
            CountryPaymentMethod(3402, 34, "ACH", "Bank Transfer", minAmount = 10.0, maxAmount = 25000.0),
            CountryPaymentMethod(3403, 34, "Zelle", "Instant Bank", minAmount = 10.0, maxAmount = 2500.0),
            CountryPaymentMethod(3404, 34, "Wire", "Bank Transfer", minAmount = 100.0, maxAmount = 100000.0)
        )),
        CountryData(35, "Canada", "CA", "+1", "CAD", "🇨🇦", "Active", listOf(
            CountryPaymentMethod(3501, 35, "Interac e-Transfer", "Instant Bank", minAmount = 10.0, maxAmount = 3000.0),
            CountryPaymentMethod(3502, 35, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 50000.0)
        )),
        CountryData(36, "Mexico", "MX", "+52", "MXN", "🇲🇽", "Active", listOf(
            CountryPaymentMethod(3601, 36, "SPEI", "Instant Bank", minAmount = 100.0, maxAmount = 50000.0),
            CountryPaymentMethod(3602, 36, "Mercado Pago", "E-Wallet", minAmount = 100.0, maxAmount = 30000.0),
            CountryPaymentMethod(3603, 36, "Bank Transfer", "Bank Transfer", minAmount = 200.0, maxAmount = 100000.0)
        )),
        CountryData(37, "Brazil", "BR", "+55", "BRL", "🇧🇷", "Active", listOf(
            CountryPaymentMethod(3701, 37, "PIX", "Instant Bank", minAmount = 20.0, maxAmount = 50000.0),
            CountryPaymentMethod(3702, 37, "Mercado Pago", "E-Wallet", minAmount = 20.0, maxAmount = 20000.0),
            CountryPaymentMethod(3703, 37, "Bank Transfer", "Bank Transfer", minAmount = 100.0, maxAmount = 100000.0)
        )),
        CountryData(38, "Argentina", "AR", "+54", "ARS", "🇦🇷", "Active", listOf(
            CountryPaymentMethod(3801, 38, "Mercado Pago", "E-Wallet", minAmount = 5000.0, maxAmount = 500000.0),
            CountryPaymentMethod(3802, 38, "Bank Transfer", "Bank Transfer", minAmount = 10000.0, maxAmount = 1000000.0)
        )),
        CountryData(39, "Colombia", "CO", "+57", "COP", "🇨🇴", "Active", listOf(
            CountryPaymentMethod(3901, 39, "Nequi", "Mobile Wallet", minAmount = 20000.0, maxAmount = 3000000.0),
            CountryPaymentMethod(3902, 39, "Daviplata", "Mobile Wallet", minAmount = 20000.0, maxAmount = 3000000.0),
            CountryPaymentMethod(3903, 39, "Bancolombia", "Instant Bank", minAmount = 50000.0, maxAmount = 10000000.0),
            CountryPaymentMethod(3904, 39, "Bank Transfer", "Bank Transfer", minAmount = 50000.0, maxAmount = 10000000.0)
        )),
        CountryData(40, "Peru", "PE", "+51", "PEN", "🇵🇪", "Active", listOf(
            CountryPaymentMethod(4001, 40, "Yape", "Mobile Wallet", minAmount = 10.0, maxAmount = 2000.0),
            CountryPaymentMethod(4002, 40, "Plin", "Mobile Wallet", minAmount = 10.0, maxAmount = 2000.0),
            CountryPaymentMethod(4003, 40, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 20000.0)
        )),
        CountryData(41, "Chile", "CL", "+56", "CLP", "🇨🇱", "Active", listOf(
            CountryPaymentMethod(4101, 41, "MACH", "Mobile Wallet", minAmount = 10000.0, maxAmount = 500000.0),
            CountryPaymentMethod(4102, 41, "Mercado Pago", "E-Wallet", minAmount = 10000.0, maxAmount = 500000.0),
            CountryPaymentMethod(4103, 41, "Bank Transfer", "Bank Transfer", minAmount = 20000.0, maxAmount = 2000000.0)
        )),
        CountryData(42, "United Kingdom", "GB", "+44", "GBP", "🇬🇧", "Active", listOf(
            CountryPaymentMethod(4201, 42, "Faster Payments", "Instant Bank", minAmount = 10.0, maxAmount = 10000.0),
            CountryPaymentMethod(4202, 42, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0),
            CountryPaymentMethod(4203, 42, "Revolut", "E-Wallet", minAmount = 10.0, maxAmount = 10000.0)
        )),
        CountryData(43, "France", "FR", "+33", "EUR", "🇫🇷", "Active", listOf(
            CountryPaymentMethod(4301, 43, "SEPA", "Instant Bank", minAmount = 10.0, maxAmount = 20000.0),
            CountryPaymentMethod(4302, 43, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0)
        )),
        CountryData(44, "Germany", "DE", "+49", "EUR", "🇩🇪", "Active", listOf(
            CountryPaymentMethod(4401, 44, "SEPA", "Instant Bank", minAmount = 10.0, maxAmount = 20000.0),
            CountryPaymentMethod(4402, 44, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0)
        )),
        CountryData(45, "Italy", "IT", "+39", "EUR", "🇮🇹", "Active", listOf(
            CountryPaymentMethod(4501, 45, "SEPA", "Instant Bank", minAmount = 10.0, maxAmount = 20000.0),
            CountryPaymentMethod(4502, 45, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0)
        )),
        CountryData(46, "Spain", "ES", "+34", "EUR", "🇪🇸", "Active", listOf(
            CountryPaymentMethod(4601, 46, "Bizum", "Instant Bank", minAmount = 10.0, maxAmount = 1000.0),
            CountryPaymentMethod(4602, 46, "SEPA", "Instant Bank", minAmount = 10.0, maxAmount = 20000.0),
            CountryPaymentMethod(4603, 46, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0)
        )),
        CountryData(47, "Portugal", "PT", "+351", "EUR", "🇵🇹", "Active", listOf(
            CountryPaymentMethod(4701, 47, "MB WAY", "Mobile Wallet", minAmount = 10.0, maxAmount = 1000.0),
            CountryPaymentMethod(4702, 47, "SEPA", "Instant Bank", minAmount = 10.0, maxAmount = 20000.0),
            CountryPaymentMethod(4703, 47, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0)
        )),
        CountryData(48, "Netherlands", "NL", "+31", "EUR", "🇳🇱", "Active", listOf(
            CountryPaymentMethod(4801, 48, "iDEAL", "Instant Bank", minAmount = 10.0, maxAmount = 10000.0),
            CountryPaymentMethod(4802, 48, "SEPA", "Instant Bank", minAmount = 10.0, maxAmount = 20000.0),
            CountryPaymentMethod(4803, 48, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0)
        )),
        CountryData(49, "Belgium", "BE", "+32", "EUR", "🇧🇪", "Active", listOf(
            CountryPaymentMethod(4901, 49, "Bancontact", "Instant Bank", minAmount = 10.0, maxAmount = 10000.0),
            CountryPaymentMethod(4902, 49, "SEPA", "Instant Bank", minAmount = 10.0, maxAmount = 20000.0),
            CountryPaymentMethod(4903, 49, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 50000.0)
        )),
        CountryData(50, "Poland", "PL", "+48", "PLN", "🇵🇱", "Active", listOf(
            CountryPaymentMethod(5001, 50, "BLIK", "Instant Bank", minAmount = 20.0, maxAmount = 10000.0),
            CountryPaymentMethod(5002, 50, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 100000.0)
        )),
        CountryData(51, "Romania", "RO", "+40", "RON", "🇷🇴", "Active", listOf(
            CountryPaymentMethod(5101, 51, "Revolut", "E-Wallet", minAmount = 50.0, maxAmount = 20000.0),
            CountryPaymentMethod(5102, 51, "Bank Transfer", "Bank Transfer", minAmount = 100.0, maxAmount = 100000.0)
        )),
        CountryData(52, "Bulgaria", "BG", "+359", "BGN", "🇧🇬", "Active", listOf(
            CountryPaymentMethod(5201, 52, "Revolut", "E-Wallet", minAmount = 20.0, maxAmount = 10000.0),
            CountryPaymentMethod(5202, 52, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 50000.0)
        )),
        CountryData(53, "Serbia", "RS", "+381", "RSD", "🇷🇸", "Active", listOf(
            CountryPaymentMethod(5301, 53, "Bank Transfer", "Bank Transfer", minAmount = 1000.0, maxAmount = 500000.0)
        )),
        CountryData(54, "Ukraine", "UA", "+380", "UAH", "🇺🇦", "Active", listOf(
            CountryPaymentMethod(5401, 54, "PrivatBank", "Instant Bank", minAmount = 200.0, maxAmount = 50000.0),
            CountryPaymentMethod(5402, 54, "Monobank", "Instant Bank", minAmount = 200.0, maxAmount = 50000.0),
            CountryPaymentMethod(5403, 54, "Bank Transfer", "Bank Transfer", minAmount = 500.0, maxAmount = 200000.0)
        )),
        CountryData(55, "Georgia", "GE", "+995", "GEL", "🇬🇪", "Active", listOf(
            CountryPaymentMethod(5501, 55, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 20000.0)
        )),
        CountryData(56, "Kazakhstan", "KZ", "+7", "KZT", "🇰🇿", "Active", listOf(
            CountryPaymentMethod(5601, 56, "Kaspi Bank/Kaspi Pay", "Instant Bank", minAmount = 1000.0, maxAmount = 500000.0),
            CountryPaymentMethod(5602, 56, "Bank Transfer", "Bank Transfer", minAmount = 2000.0, maxAmount = 1000000.0)
        )),
        CountryData(57, "Uzbekistan", "UZ", "+998", "UZS", "🇺🇿", "Active", listOf(
            CountryPaymentMethod(5701, 57, "Click", "Mobile Wallet", minAmount = 50000.0, maxAmount = 10000000.0),
            CountryPaymentMethod(5702, 57, "Payme", "Mobile Wallet", minAmount = 50000.0, maxAmount = 10000000.0),
            CountryPaymentMethod(5703, 57, "Uzum Bank", "Instant Bank", minAmount = 50000.0, maxAmount = 20000000.0),
            CountryPaymentMethod(5704, 57, "Bank Transfer", "Bank Transfer", minAmount = 100000.0, maxAmount = 50000000.0)
        )),
        CountryData(58, "Azerbaijan", "AZ", "+994", "AZN", "🇦🇿", "Active", listOf(
            CountryPaymentMethod(5801, 58, "m10", "Mobile Wallet", minAmount = 10.0, maxAmount = 3000.0),
            CountryPaymentMethod(5802, 58, "Bank Transfer", "Bank Transfer", minAmount = 20.0, maxAmount = 20000.0)
        )),
        CountryData(59, "Australia", "AU", "+61", "AUD", "🇦🇺", "Active", listOf(
            CountryPaymentMethod(5901, 59, "PayID", "Instant Bank", minAmount = 20.0, maxAmount = 10000.0),
            CountryPaymentMethod(5902, 59, "Osko", "Instant Bank", minAmount = 20.0, maxAmount = 10000.0),
            CountryPaymentMethod(5903, 59, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 50000.0)
        )),
        CountryData(60, "New Zealand", "NZ", "+64", "NZD", "🇳🇿", "Active", listOf(
            CountryPaymentMethod(6001, 60, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 50000.0)
        )),
        CountryData(61, "Fiji", "FJ", "+679", "FJD", "🇫🇯", "Active", listOf(
            CountryPaymentMethod(6101, 61, "M-PAiSA", "Mobile Wallet", minAmount = 20.0, maxAmount = 5000.0),
            CountryPaymentMethod(6102, 61, "MyCash", "Mobile Wallet", minAmount = 20.0, maxAmount = 5000.0),
            CountryPaymentMethod(6103, 61, "Bank Transfer", "Bank Transfer", minAmount = 50.0, maxAmount = 20000.0)
        ))
    )

    fun getCountry(nameOrIso: String): CountryData? {
        val q = nameOrIso.trim()
        return allCountries.find {
            it.countryName.equals(q, ignoreCase = true) ||
            it.isoCode.equals(q, ignoreCase = true)
        }
    }

    fun getMethodsForCountry(countryName: String): List<CountryPaymentMethod> {
        val country = getCountry(countryName) ?: allCountries.first()
        return country.paymentMethods
    }

    fun getPaymentMethodsForCountry(countryName: String): List<CountryPaymentMethod> {
        return getMethodsForCountry(countryName)
    }

    fun getFlagForCountry(countryName: String): String {
        return getCountry(countryName)?.flag ?: "🌐"
    }

    fun getCurrencyForCountry(countryName: String): String {
        return getCountry(countryName)?.currencyCode ?: "BDT"
    }

    fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode.uppercase()) {
            "BDT" -> "৳"
            "INR" -> "₹"
            "PKR", "NPR", "LKR" -> "₨"
            "AED" -> "د.إ"
            "SAR" -> "﷼"
            "QAR" -> "ر.ق"
            "MYR" -> "RM"
            "SGD", "USD", "AUD", "CAD", "NZD", "FJD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY", "CNY" -> "¥"
            "KRW" -> "₩"
            "TRY" -> "₺"
            "THB" -> "฿"
            "PHP" -> "₱"
            "VND" -> "₫"
            "NGN" -> "₦"
            "ZAR" -> "R"
            "BRL" -> "R$"
            else -> "$"
        }
    }
}
