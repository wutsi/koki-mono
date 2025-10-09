package com.wutsi.koki.tenant.dto

object ConfigurationName {
    // ACCOUNT
    const val ACCOUNT_INVITATION_EMAIL_BODY = "account.invitation.email.body"
    const val ACCOUNT_INVITATION_EMAIL_SUBJECT = "account.invitation.email.subject"

    // AI
    const val AI_PROVIDER = "ai.provider"
    const val AI_PROVIDER_GEMINI_API_KEY = "ai.provider.gemini.api_key"
    const val AI_PROVIDER_GEMINI_MODEL = "ai.provider.gemini.model"
    const val AI_PROVIDER_DEEPSEEK_API_KEY = "ai.provider.deepseek.api_key"
    const val AI_PROVIDER_DEEPSEEK_MODEL = "ai.provider.deepseek.model"

    // EMAIL
    const val EMAIL_DECORATOR = "email.decorator"

    // LISTINGS
    const val LISTING_START_NUMBER = "listing.start.number"

    // ROLE
    const val PORTAL_SIGNUP_ROLE_ID = "portal.signup.role_id" // Default Role to assign to all new users

    // SMTP CONFIGURATION
    const val SMTP_TYPE = "smtp.type"
    const val SMTP_USERNAME = "smtp.username"
    const val SMTP_PASSWORD = "smtp.password"
    const val SMTP_HOST = "smtp.host"
    const val SMTP_PORT = "smtp.port"
    const val SMTP_FROM_ADDRESS = "smtp.from.address"
    const val SMTP_FROM_PERSONAL = "smtp.from.personal"

    // STORAGE
    const val STORAGE_TYPE = "storage.type"
    const val STORAGE_LOCAL_DIRECTORY = "storage.local.directory"
    const val STORAGE_LOCAL_BASE_URL = "storage.local.base_url"
    const val STORAGE_S3_BUCKET = "storage.s3.bucket"
    const val STORAGE_S3_REGION = "storage.s3.region"
    const val STORAGE_S3_ACCESS_KEY = "storage.s3.access-key"
    const val STORAGE_S3_SECRET_KEY = "storage.s3.secret-key"

    // TRANSACTION
    const val TRANSLATION_PROVIDER = "translation.provider"
    const val TRANSLATION_PROVIDER_AWS_ACCESS_KEY = "translation.provider.aws.access-key"
    const val TRANSLATION_PROVIDER_AWS_SECRET_KEY = "translation.provider.aws.secret-key"
    const val TRANSLATION_PROVIDER_AWS_REGION = "translation.provider.aws.region"
}
