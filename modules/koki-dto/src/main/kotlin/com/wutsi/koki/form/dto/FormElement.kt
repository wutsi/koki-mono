package com.wutsi.koki.form.dto

data class FormElement(
    val name: String = "",
    val type: FormElementType = FormElementType.TEXT,
    val title: String? = null,
    val description: String? = null,
    val required: Boolean? = null,
    val min: String? = null,
    val max: String? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val pattern: String? = null,
    val readOnly: Boolean? = null,

    // For CHECKBOXES, MULTIPLE_CHOICE, DROPDOWN
    val options: List<FormOption> = emptyList(),

    // For CHECKBOXES, MULTIPLE_CHOICE
    val otherOption: FormOption? = null,

    // For VIDEO, URL
    val url: String? = null,

    val accessControl: FormAccessControl? = null,
    val logic: FormLogic? = null,
    val elements: List<FormElement>? = null,
)
