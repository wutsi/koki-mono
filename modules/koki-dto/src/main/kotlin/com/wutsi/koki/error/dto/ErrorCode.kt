package com.wutsi.koki.error.dto

object ErrorCode {
    private val PREFIX = "urn:wutsi:koki:error"

    val IMPORT_ERROR = "$PREFIX:import-error"

    val ACCOUNT_NOT_FOUND: String = "$PREFIX:account:not-found"
    val ACCOUNT_IN_USE: String = "$PREFIX:account:in-use"

    val ATTRIBUTE_NOT_FOUND: String = "$PREFIX:attribute:not-found"
    val ATTRIBUTE_NAME_MISSING: String = "$PREFIX:attribute:name-missing"
    val ATTRIBUTE_TYPE_INVALID: String = "$PREFIX:attribute:type-invalid"

    val AUTHENTICATION_FAILED: String = "$PREFIX:auth:failed"
    val AUTHENTICATION_USER_NOT_ACTIVE: String = "$PREFIX:auth:user-not-active"

    val AUTHORIZATION_PERMISSION_DENIED: String = "$PREFIX:auth:permission-denied"
    val AUTHORIZATION_SCHEME_NOT_SUPPORTED: String = "$PREFIX:auth:scheme-not-supported"
    val AUTHORIZATION_TENANT_MISMATCH: String = "$PREFIX:auth:tenant-mismatch"
    val AUTHORIZATION_TOKEN_EXPIRED: String = "$PREFIX:auth:token-expired"
    val AUTHORIZATION_UNAUTHENTICATED: String = "$PREFIX:auth:unauthenticated"

    val BUSINESS_NOT_FOUND: String = "$PREFIX:business:not-found"

    val CATEGORY_TYPE_NOT_SUPPORTED: String = "$PREFIX:category:type-not-supported"

    val CONTACT_NOT_FOUND: String = "$PREFIX:contact:not-found"

    val EMAIL_NOT_FOUND: String = "$PREFIX:email:not-found"
    val EMAIL_DELIVERY_FAILED: String = "$PREFIX:email:delivery-failed"
    val EMAIL_RECIPIENT_EMAIL_MISSING: String = "$PREFIX:email:recipient-email-missing"
    val EMAIL_SMTP_NOT_CONFIGURED: String = "$PREFIX:email:smtp-not-configured"

    val EMPLOYEE_ALREADY_EXIST: String = "$PREFIX:employee:already-exist"
    val EMPLOYEE_NOT_FOUND: String = "$PREFIX:employee:not-found"

    val FILE_NOT_FOUND: String = "$PREFIX:file:not-found"

    val FORM_NOT_FOUND: String = "$PREFIX:form:not-found"
    val FORM_IN_USE: String = "$PREFIX:form:in-use"

    val HTTP_MISSING_PARAMETER = "$PREFIX:http:missing-parameter"
    val HTTP_INVALID_PARAMETER = "$PREFIX:http:invalid-parameter"
    val HTTP_INTERNAL = "$PREFIX:http:unexpected-error"
    val HTTP_METHOD_NOT_SUPPORTED = "$PREFIX:http:method-not-supported"
    val HTTP_ACCESS_DENIED = "$PREFIX:http:access-denied"
    val HTTP_AUTHENTICATION_FAILED = "$PREFIX:http:authetication-failed"
    val HTTP_DOWNSTREAM_ERROR = "$PREFIX:http:downstream-error"

    val INVOICE_NOT_FOUND: String = "$PREFIX:invoice:not-found"
    val INVOICE_BAD_STATUS: String = "$PREFIX:invoice:bad-status"
    val INVOICE_NO_PRODUCT: String = "$PREFIX:invoice:no-product"

    val JURIDICTION_NOT_FOUND: String = "$PREFIX:juridiction:not-found"
    val JURIDICTION_COUNTRY_NOT_SUPPORTED: String = "$PREFIX:juridiction:country-not-supported"

    val LOCATION_NOT_FOUND = "$PREFIX:location:not-found"
    val LOCATION_FEED_NOT_FOUND = "$PREFIX:location:feed-not-found"

    val MODULE_NOT_FOUND: String = "$PREFIX:module:not-found"

    val NOTE_NOT_FOUND: String = "$PREFIX:note:not-found"

    val PRICE_NOT_FOUND: String = "$PREFIX:price:not-found"
    val PRICE_IN_USE: String = "$PREFIX:price:in-use"
    val PRICE_CURRENCY_NOT_VALID: String = "$PREFIX:price:currency-not-valid"
    val PRICE_CURRENCY_MISSING: String = "$PREFIX:price:currency-missing"

    val PRODUCT_NOT_FOUND: String = "$PREFIX:product:not-found"
    val PRODUCT_IN_USE: String = "$PREFIX:product:in-use"

    val ROLE_DUPLICATE_NAME: String = "$PREFIX:role:duplicate-name"
    val ROLE_NOT_FOUND: String = "$PREFIX:role:not-found"
    val ROLE_NAME_MISSING: String = "$PREFIX:role:name-missing"
    val ROLE_IN_USE: String = "$PREFIX:role:in-use"

    val SALES_TAX_COUNTRY_NOT_SUPPORTED: String = "$PREFIX:sales-tax:country-not-supported"
    val SALES_TAX_STATE_NOT_FOUND: String = "$PREFIX:sales-tax:state-not-found"

    val TAX_NOT_FOUND: String = "$PREFIX:tax:not-found"
    val TAX_PRODUCT_NOT_FOUND: String = "$PREFIX:tax-product:not-found"

    val TENANT_NOT_FOUND: String = "$PREFIX:tenant:not-found"
    val TENANT_MISSING_FROM_HEADER: String = "$PREFIX:tenant:missing-from-header"

    val TRANSACTION_NOT_FOUND: String = "$PREFIX:transaction:not-found"
    val TRANSACTION_PAYMENT_METHOD_NOT_FOUND: String = "$PREFIX:transaction:payment-method-not-found"
    val TRANSACTION_PAYMENT_METHOD_NOT_SUPPORTED: String = "$PREFIX:transaction:payment-method-not-supported"
    val TRANSACTION_PAYMENT_FAILED: String = "$PREFIX:transaction:failed"

    val TYPE_NOT_FOUND: String = "$PREFIX:type:not-found"
    val TYPE_NAME_MISSING: String = "$PREFIX:type:name-missing"

    val USER_NOT_FOUND: String = "$PREFIX:user:not-found"
    val USER_DUPLICATE_EMAIL: String = "$PREFIX:user:duplicate-email"
    val USER_ALREADY_ASSIGNED: String = "$PREFIX:user:already-assigned"
}
