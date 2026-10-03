package com.example

import com.example.data.AdminPaymentGatewayConfig
import com.example.data.ApiEndpointDoc
import com.example.data.GeneratedApiKey
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testGooglePayGatewayConfig() {
    val gpay = AdminPaymentGatewayConfig(
      id = "google_pay",
      name = "Google Pay",
      code = "GOOGLE_PAY",
      iconType = "GOOGLE_PAY",
      isEnabled = true,
      environment = "PRODUCTION",
      merchantId = "BCR2DN6TXM4K829L",
      transactionFeePercent = 1.2,
      minAmount = 100.0,
      maxAmount = 500000.0,
      isSystemDefault = true
    )

    assertEquals("google_pay", gpay.id)
    assertEquals("GOOGLE_PAY", gpay.code)
    assertTrue(gpay.isEnabled)
    assertEquals("PRODUCTION", gpay.environment)
    assertEquals(1.2, gpay.transactionFeePercent, 0.001)
    assertTrue(gpay.isSystemDefault)
  }

  @Test
  fun testAlipayGatewayConfig() {
    val alipay = AdminPaymentGatewayConfig(
      id = "alipay",
      name = "Alipay (支付宝)",
      code = "ALIPAY",
      iconType = "ALIPAY",
      isEnabled = true,
      environment = "PRODUCTION",
      merchantId = "2088731920194821",
      supportedCurrencies = "CNY, USD, BDT, HKD, EUR",
      transactionFeePercent = 1.8,
      minAmount = 50.0,
      maxAmount = 1000000.0,
      isSystemDefault = true
    )

    assertEquals("alipay", alipay.id)
    assertTrue(alipay.supportedCurrencies.contains("CNY"))
    assertTrue(alipay.supportedCurrencies.contains("USD"))
    assertEquals(1.8, alipay.transactionFeePercent, 0.001)
  }

  @Test
  fun testApplePayGatewayConfig() {
    val applePay = AdminPaymentGatewayConfig(
      id = "apple_pay",
      name = "Apple Pay",
      code = "APPLE_PAY",
      iconType = "APPLE_PAY",
      isEnabled = true,
      environment = "PRODUCTION",
      merchantId = "merchant.com.modolconnect.app",
      supportedCurrencies = "USD, EUR, GBP, AED, BDT, AUD",
      transactionFeePercent = 1.5,
      minAmount = 100.0,
      maxAmount = 500000.0,
      isSystemDefault = true
    )

    assertEquals("apple_pay", applePay.id)
    assertEquals("merchant.com.modolconnect.app", applePay.merchantId)
    assertTrue(applePay.supportedCurrencies.contains("USD"))
    assertTrue(applePay.supportedCurrencies.contains("BDT"))
  }

  @Test
  fun testCustomGatewayCreation() {
    val customGw = AdminPaymentGatewayConfig(
      id = "custom_stripe_12345",
      name = "Stripe Global",
      code = "STRIPE_GLOBAL",
      iconType = "CARD",
      isEnabled = true,
      environment = "SANDBOX",
      merchantId = "acct_1029384756",
      apiKey = "pk_test_12345",
      secretKey = "sk_test_67890",
      transactionFeePercent = 2.9,
      minAmount = 50.0,
      maxAmount = 250000.0,
      isSystemDefault = false
    )

    assertFalse(customGw.isSystemDefault)
    assertEquals("SANDBOX", customGw.environment)
    assertEquals(2.9, customGw.transactionFeePercent, 0.001)
    assertEquals("STRIPE_GLOBAL", customGw.code)
  }

  @Test
  fun testGeneratedApiKeyModel() {
    val apiKey = GeneratedApiKey(
      id = "KEY-1001",
      name = "Official Website Portal",
      appType = "WEBSITE",
      apiKey = "mc_live_pk_modol_9942a",
      apiSecret = "mc_live_sk_modol_sec_7781a",
      environment = "PRODUCTION",
      scopes = listOf("auth.otp", "users.read", "models.read", "bookings.create"),
      rateLimitPerMin = 120,
      status = "ACTIVE"
    )

    assertEquals("KEY-1001", apiKey.id)
    assertEquals("WEBSITE", apiKey.appType)
    assertEquals("ACTIVE", apiKey.status)
    assertEquals(4, apiKey.scopes.size)
    assertTrue(apiKey.scopes.contains("auth.otp"))
  }

  @Test
  fun testApiEndpointDocModel() {
    val endpoint = ApiEndpointDoc(
      method = "POST",
      path = "/v1/auth/send-otp",
      category = "Authentication & OTP",
      description = "Send Firebase/SMS dynamic OTP to user or model phone number",
      requiresAuth = false,
      sampleRequest = "{\"phoneNumber\": \"+8801711000000\", \"role\": \"USER\"}",
      sampleResponse = "{\"status\": \"SUCCESS\", \"verificationSessionId\": \"sess_otp_9921\"}"
    )

    assertEquals("POST", endpoint.method)
    assertEquals("/v1/auth/send-otp", endpoint.path)
    assertFalse(endpoint.requiresAuth)
    assertTrue(endpoint.sampleResponse.contains("SUCCESS"))
  }

  @Test
  fun testPhotoUploadPathFormatting() {
    val localRawPath = "/data/user/0/com.modol.connect.app/files/uploads/profile/profile_12345.jpg"
    val formatted = if (localRawPath.startsWith("/") && !localRawPath.startsWith("file://")) {
      "file://$localRawPath"
    } else {
      localRawPath
    }

    assertTrue(formatted.startsWith("file://"))
    assertEquals("file:///data/user/0/com.modol.connect.app/files/uploads/profile/profile_12345.jpg", formatted)

    val httpUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
    val unchanged = if (httpUrl.startsWith("/") && !httpUrl.startsWith("file://")) {
      "file://$httpUrl"
    } else {
      httpUrl
    }
    assertEquals(httpUrl, unchanged)
  }

  @Test
  fun testPaymentFeeAndNetCalculation() {
    val amount = 10000.0
    val feePercent = 1.5
    val fee = amount * (feePercent / 100.0)
    val net = amount - fee

    assertEquals(150.0, fee, 0.001)
    assertEquals(9850.0, net, 0.001)
  }

  @Test
  fun testOtpFormatConstraint() {
    val sampleOtps = listOf("123456", "998877", "445566")
    for (otp in sampleOtps) {
      assertEquals(6, otp.length)
      assertTrue(otp.all { it.isDigit() })
    }
  }
}

