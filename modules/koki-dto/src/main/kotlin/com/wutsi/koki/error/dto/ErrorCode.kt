package com.wutsi.koki.error.dto

object ErrorCode {
    private val PREFIX = "urn:wutsi:koki:error"

    val IMPORT_ERROR = "$PREFIX:import-error"

    val ACCOUNT_NOT_FOUND: String = "$PREFIX:account:not-found"
    val ACCOUNT_IN_USE: String = "$PREFIX:account:in-use"

    val ACCOUNT_TYPE_NOT_FOUND: String = "$PREFIX:account-type:not-found"
    val ACCOUNT_TYPE_NAME_MISSING: String = "$PREFIX:account-type:name-missing"

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

    val CONTACT_TYPE_NOT_FOUND: String = "$PREFIX:contact-type:not-found"
    val CONTACT_TYPE_NAME_MISSING: String = "$PREFIX:contact-type:name-missing"

    val CONTACT_NOT_FOUND: String = "$PREFIX:contact:not-found"

    val FILE_NOT_FOUND: String = "$PREFIX:file:not-found"

    val FORM_DUPLICATE_NAME: String = "$PREFIX:form:duplicate-name"
    val FORM_IN_USE: String = "$PREFIX:form:in-use"
    val FORM_NOT_FOUND: String = "$PREFIX:form:not-found"
    val FORM_DATA_NOT_FOUND: String = "$PREFIX:form-data:not-found"
    val FORM_SUBMISSION_NOT_FOUND: String = "$PREFIX:form-submission:not-found"

    val HTTP_MISSING_PARAMETER = "$PREFIX:http:missing-parameter"
    val HTTP_INVALID_PARAMETER = "$PREFIX:http:invalid-parameter"
    val HTTP_INTERNAL = "$PREFIX:http:unexpected-error"
    val HTTP_METHOD_NOT_SUPPORTED = "$PREFIX:http:method-not-supported"
    val HTTP_ACCESS_DENIED = "$PREFIX:http:access-denied"
    val HTTP_AUTHENTICATION_FAILED = "$PREFIX:http:authetication-failed"
    val HTTP_DOWNSTREAM_ERROR = "$PREFIX:http:downstream-error"

    val LOG_NOT_FOUND = "$PREFIX:log:not-found"

    val MESSAGE_DUPLICATE_NAME: String = "$PREFIX:message:duplicate-name"
    val MESSAGE_IN_USE: String = "$PREFIX:message:in-use"
    val MESSAGE_NOT_FOUND: String = "$PREFIX:message:not-found"

    val NOTE_NOT_FOUND: String = "$PREFIX:note:not-found"

    val ROLE_NOT_FOUND: String = "$PREFIX:role:not-found"
    val ROLE_NAME_MISSING: String = "$PREFIX:role:name-missing"

    val SERVICE_DUPLICATE_NAME: String = "$PREFIX:service:duplicate-name"
    val SERVICE_IN_USE: String = "$PREFIX:service:in-use"
    val SERVICE_NOT_FOUND: String = "$PREFIX:service:not-found"

    val SCRIPT_DUPLICATE_NAME: String = "$PREFIX:script:duplicate-name"
    val SCRIPT_IN_USE: String = "$PREFIX:script:in-use"
    val SCRIPT_NOT_FOUND: String = "$PREFIX:script:not-found"
    val SCRIPT_COMPILATION_FAILED: String = "$PREFIX:script:compilation-failed"
    val SCRIPT_EXECUTION_FAILED: String = "$PREFIX:script:execution-failed"

    val TAX_NOT_FOUND: String = "$PREFIX:tax:not-found"
    val TAX_TYPE_NOT_FOUND: String = "$PREFIX:tax-type:not-found"
    val TAX_TYPE_NAME_MISSING: String = "$PREFIX:tax-type:name-missing"

    val TENANT_NOT_FOUND: String = "$PREFIX:tenant:not-found"
    val TENANT_MISSING_FROM_HEADER: String = "$PREFIX:tenant:missing-from-header"

    val USER_NOT_FOUND: String = "$PREFIX:user:not-found"
    val USER_DUPLICATE_EMAIL: String = "$PREFIX:user:duplicate-email"

    val WORKFLOW_ACTIVITY_NOT_FOUND: String = "$PREFIX:workflow:activity-not-found"
    val WORKFLOW_DUPLICATE_NAME: String = "$PREFIX:workflow:duplicate-name"
    val WORKFLOW_FLOW_NOT_FOUND: String = "$PREFIX:workflow:flow-not-found"
    val WORKFLOW_HAS_INSTANCES: String = "$PREFIX:workflow:has-instance"
    val WORKFLOW_NOT_ACTIVE: String = "$PREFIX:workflow:not-active"
    val WORKFLOW_NOT_FOUND: String = "$PREFIX:workflow:not-found"
    val WORKFLOW_NOT_VALID: String = "$PREFIX:workflow:not-valid"

    val WORKFLOW_INSTANCE_ACTIVITY_NOT_FOUND = "$PREFIX:workflow-instance:activity-not-found"
    val WORKFLOW_INSTANCE_ACTIVITY_STILL_RUNNING = "$PREFIX:workflow-instance:activity-still-running"
    val WORKFLOW_INSTANCE_ACTIVITY_APPROVAL_PENDING = "$PREFIX:workflow-instance:activity-approval-pending"
    val WORKFLOW_INSTANCE_ACTIVITY_NO_APPROVAL_PENDING = "$PREFIX:workflow-instance:activity-no-approval-pending"
    val WORKFLOW_INSTANCE_APPROVER_MISSING = "$PREFIX:workflow-instance:approver-missing"
    val WORKFLOW_INSTANCE_ASSIGNEE_NOT_FOUND = "$PREFIX:workflow-instance:assignee-not-found"
    val WORKFLOW_INSTANCE_NOT_FOUND = "$PREFIX:workflow-instance:not-found"
    val WORKFLOW_INSTANCE_STATUS_ERROR = "$PREFIX:workflow-instance:status-error"
    val WORKFLOW_INSTANCE_PARTICIPANT_MISSING = "$PREFIX:workflow-instance:participant-missing"
    val WORKFLOW_INSTANCE_PARTICIPANT_NOT_VALID = "$PREFIX:workflow-instance:participant-not-valid"
    val WORKFLOW_INSTANCE_PARAMETER_MISSING = "$PREFIX:workflow-instance:parameter-missing"
}
