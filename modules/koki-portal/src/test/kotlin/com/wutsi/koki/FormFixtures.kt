package com.wutsi.koki

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormSubmission
import com.wutsi.koki.form.dto.FormSubmissionSummary
import com.wutsi.koki.form.dto.FormSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date
import java.util.UUID

object FormFixtures {
    val FORM_ID = "11111-22222-33333"
    val FORM_NAME = "FRM-001"

    val form = Form(
        id = FORM_ID,
        name = FORM_NAME,
        title = "Incident Report",
        content = ObjectMapper().readValue(
            FormFixtures::class.java.getResourceAsStream("/form-001-incident.json"),
            FormContent::class.java
        )
    )

    val forms = listOf(
        FormSummary(
            id = FORM_ID,
            name = FORM_NAME,
            title = "Indicent Manager",
            active = true,
        ),
        FormSummary(
            id = "2",
            name = "FRM-002",
            title = "Reimbursement Form",
            active = true,
        ),
        FormSummary(
            id = "3",
            name = "FRM-003",
            title = "Receipt Submission",
            active = false,
        ),
    )

    val formSubmissions = listOf(
        FormSubmissionSummary(
            id = UUID.randomUUID().toString(),
            formId = FORM_ID,
            submittedById = users[0].id,
            submittedAt = Date(),
        ),
        FormSubmissionSummary(
            id = UUID.randomUUID().toString(),
            formId = FORM_ID,
            submittedById = null,
            submittedAt = DateUtils.addDays(Date(), -1),
        ),
        FormSubmissionSummary(
            id = UUID.randomUUID().toString(),
            formId = FORM_ID,
            submittedById = null,
            submittedAt = DateUtils.addDays(Date(), -1),
        ),
        FormSubmissionSummary(
            id = UUID.randomUUID().toString(),
            formId = FORM_ID,
            submittedById = null,
            submittedAt = DateUtils.addDays(Date(), -2),
        ),
        FormSubmissionSummary(
            id = UUID.randomUUID().toString(),
            formId = FORM_ID,
            submittedById = null,
            submittedAt = DateUtils.addDays(Date(), -2),
        ),
    )

    val formSubmission = FormSubmission(
        id = UUID.randomUUID().toString(),
        formId = FORM_ID,
        submittedById = users[0].id,
        submittedAt = DateUtils.addDays(Date(), -2),
        data = mapOf(
            "employee_name" to "Ray Sponsible",
            "employee_email" to "ray.sponsible@gmail.com",
            "employee_date_of_birth" to "1990-01-31"
        )
    )
}
