package com.wutsi.koki

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormSummary

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
}
