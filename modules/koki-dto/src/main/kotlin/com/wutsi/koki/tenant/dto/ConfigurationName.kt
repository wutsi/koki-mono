package com.wutsi.koki.tenant.dto

object ConfigurationName {
    // SMTP CONFIGURATION
    const val SMTP_TYPE = "smtp.type"
    const val SMTP_USERNAME = "smtp.username"
    const val SMTP_PASSWORD = "smtp.password"
    const val SMTP_HOST = "smtp.host"
    const val SMTP_PORT = "smtp.port"
    const val SMTP_FROM_ADDRESS = "smtp.from.address"
    const val SMTP_FROM_PERSONAL = "smtp.from.personal"

    // EMAIL
    const val EMAIL_DECORATOR = "email.decorator"

    // INVOICE
    const val INVOICE_START_NUMBER = "invoice.start.number"
    const val INVOICE_DUE_DAYS = "invoice.due.days"
    const val INVOICE_EMAIL_OPENED_ENABLED = "invoice.email.opened.enabled"
    const val INVOICE_EMAIL_OPENED_BODY = "invoice.email.opened.body"
    const val INVOICE_EMAIL_OPENED_SUBJECT = "invoice.email.opened.subject"
    const val INVOICE_EMAIL_PAID_ENABLED = "invoice.email.paid.enabled"
    const val INVOICE_EMAIL_PAID_BODY = "invoice.email.paid.body"
    const val INVOICE_EMAIL_PAID_SUBJECT = "invoice.email.paid.subject"

    // Payment
    const val PAYMENT_METHOD_CASH_ENABLED = "payment.method.cash.enabled"

    const val PAYMENT_METHOD_CHECK_ENABLED = "payment.method.check.enabled"

    const val PAYMENT_METHOD_INTERAC_ENABLED = "payment.method.interac.enabled"

    const val PAYMENT_METHOD_CREDIT_CARD_ENABLED = "payment.method.credit_card.enabled"
    const val PAYMENT_METHOD_CREDIT_CARD_GATEWAY = "payment.method.credit_card.gateway"
    const val PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY = "payment.method.credit_card.gateway.stripe.api_key"

    const val PAYMENT_METHOD_MOBILE_ENABLED = "payment.method.mobile.enabled"
    const val PAYMENT_METHOD_MOBILE_GATEWAY = "payment.method.mobile.gateway"
    const val PAYMENT_METHOD_MOBILE_GATEWAY_FLUTTERWAVE_SECRET_KEY =
        "payment.method.mobile.gateway.flutterwave_secret_key"

    const val PAYMENT_METHOD_BANK_ENABLED = "payment.method.bank.enabled"

    const val PAYMENT_METHOD_PAYPAL_ENABLED = "payment.method.paypal.enabled"
    const val PAYMENT_METHOD_PAYPAL_CLIENT_ID = "payment.method.paypal.client_id"
    const val PAYMENT_METHOD_PAYPAL_SECRET_KEY = "payment.method.paypal.secret_key"

    // Storage
    const val STORAGE_TYPE = "storage.type"
    const val STORAGE_LOCAL_DIRECTORY = "storage.local.directory"
    const val STORAGE_LOCAL_BASE_URL = "storage.local.base_url"
    const val STORAGE_S3_BUCKET = "storage.s3.bucket"
    const val STORAGE_S3_REGION = "storage.s3.region"
    const val STORAGE_S3_ACCESS_KEY = "storage.s3.access-key"
    const val STORAGE_S3_SECRET_KEY = "storage.s3.secret-key"
}
