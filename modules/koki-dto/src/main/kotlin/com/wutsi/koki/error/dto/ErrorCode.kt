package com.wutsi.koki.error.dto

object ErrorCode {
    private val PREFIX = "urn:wutsi:koki:error"

    val IMPORT_ERROR = "$PREFIX:import-error"

    val ATTRIBUTE_NOT_FOUND: String = "$PREFIX:attribute:not-found"
    val ATTRIBUTE_NAME_MISSING: String = "$PREFIX:attribute:name-missing"
    val ATTRIBUTE_TYPE_INVALID: String = "$PREFIX:attribute:type-invalid"

    val FORM_NOT_FOUND: String = "$PREFIX:form:not-found"
    val FORM_DATA_NOT_FOUND: String = "$PREFIX:form-data:not-found"
    val FORM_DATA_FIELD_MISSING: String = "$PREFIX:form-data:field-missing"
    val FORM_DATA_FIELD_INVALID: String = "$PREFIX:form-data:field-invalid"

    val HTTP_REQUEST_NOT_READABLE = "$PREFIX:http:request-not-readable"
    val HTTP_MISSING_PARAMETER = "$PREFIX:http:missing-parameter"
    val HTTP_INVALID_PARAMETER = "$PREFIX:http:invalid-parameter"
    val HTTP_INTERNAL = "$PREFIX:http:unexpected-error"
    val HTTP_METHOD_NOT_SUPPORTED = "$PREFIX:http:method-not-supported"
    val HTTP_ACCESS_DENIED = "$PREFIX:http:access-denied"
    val HTTP_AUTHENTICATION_FAILED = "$PREFIX:http:authetication-failed"
    val HTTP_DOWNSTREAM_ERROR = "$PREFIX:http:downstream-error"

    val ROLE_NOT_FOUND: String = "$PREFIX:role:not-found"
    val ROLE_NAME_MISSING: String = "$PREFIX:role:name-missing"

    val TENANT_NOT_FOUND: String = "$PREFIX:tenant:not-found"
    val TENANT_MISSING_FROM_HEADER: String = "$PREFIX:tenant:missing-from-header"

    val USER_NOT_FOUND: String = "$PREFIX:user:not-found"
    val USER_DUPLICATE_EMAIL: String = "$PREFIX:user:duplicate-email"

    val WORKFLOW_ACTIVITY_NOT_FOUND: String = "$PREFIX:workflow:activity-not-found"
    val WORKFLOW_FLOW_NOT_FOUND: String = "$PREFIX:workflow:flow-not-found"
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
    val WORKFLOW_INSTANCE_PARAMETER_NOT_VALID = "$PREFIX:workflow-instance:parameter-not-valid"
}
