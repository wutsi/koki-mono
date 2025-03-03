package com.wutsi.koki.tenant.dto

object ConfigurationName {
    // SMTP CONFIGURATION
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
    const val INVOICE_EMAIL_OPENED = "invoice.email.opened"
    const val INVOICE_EMAIL_PAID = "invoice.email.paid"

    // Storage
    const val STORAGE_TYPE = "storage.type"
    const val STORAGE_LOCAL_DIRECTORY = "storage.local.directory"
    const val STORAGE_LOCAL_BASE_URL = "storage.local.base_url"
    const val STORAGE_S3_BUCKET = "storage.s3.bucket"
    const val STORAGE_S3_REGION = "storage.s3.region"
    const val STORAGE_S3_ACCESS_KEY = "storage.s3.access-key"
    const val STORAGE_S3_SECRET_KEY = "storage.s3.secret-key"
}
