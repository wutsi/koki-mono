package com.wutsi.koki.tax.server.service.ai

import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import org.springframework.http.MediaType
import java.util.Locale

/**
 * This is the agent to extract information from document uploaded.
 * The information includes:
 *   - Document code
 *   - Document language
 *   - Document short description
 *   - Document contacts: information about all the people listed into the document
 */
class TaxFileAgent(
    private val formService: FormService,
    private val account: AccountEntity,
    val llm: LLM,
    val maxIterations: Int = MAX_ITERATIONS,
) : Agent(llm, maxIterations, MediaType.APPLICATION_JSON) {
    companion object {
        const val EXPENSE_CODE = "EXP"
        const val MAX_ITERATIONS = 10
    }

    override fun systemInstructions() =
        """
        You are an expert tax accountant assisting with tax preparation.
        Provide accurate and detailed information based on the latest tax laws and regulations.

        Instructions:
            1. Analyze the query, previous reasoning steps, and observations.
            2. Decide on the next action: use a tool or provide a final answer.
            3. Always explain your thought process at every step
            4. The provided document contains one or multiple forms. Each form can be either:
              4.1. An official fiscal form of the government Revenue Service or Agencies.
              4.2. An expense form. Their code should always be "$EXPENSE_CODE"
              4.3. An internal form, use for collecting information about the client.
            5. If you are not able to identify the document, set the document code to NULL
            6. For the final answer, return the data in JSON format that looks like this:
                  {
                    "language": "two-letter code of the language",
                    "description": "Short description of the document (max 255 characters)",
                    "numberOfPages": "Number of pages in the document",
                    "sections": [
                        {
                            "code": "Code of the document. Example: T1, T4, RL-1",
                            "startPage": 1,
                            "endPage": 3
                        }
                    ],
                    "contacts":[
                        {
                            "salutation": "Ex: M. or Mme or Miss etc.",
                            "firstName": "First Name",
                            "lastName": "Last Name",
                            "birthDate": "Birth date in the format yyyy-MM-dd",
                            "email": "Email address",
                            "homePhone": "Home phone number",
                            "cellPhone": "Mobile phone number",
                            "address": "Street",
                            "city": "Name of the city",
                            "state": "State",
                            "zipCode": "Zip code",
                            "country": "two-letter country code"
                            "role": "PRIMARY, SPOUSE, CHILD or VENDOR"
                        }
                    ]
                  }

              IMPORTANT: Never include null fields in the JSON.
    """.trimIndent()

    override fun buildPrompt(query: String, memory: List<String>): String {
        val forms = formService.search(
            tenantId = account.tenantId,
            active = true,
            limit = 1000,
        )
        if (forms.isEmpty()) {
            return ""
        } else {
            return "Internal forms in CSV format:\n" +
                "form_code,form_name,form_description\n" +
                forms.map { form ->
                    listOf(
                        form.code,
                        form.name,
                        form.description?.replace("\n", "")?.take(200) ?: ""
                    ).joinToString(",")
                }.joinToString(separator = "\n")
        }

        """
        Goal: Extract informations from the provided document.

        Query: {{query}}

        Additional Informations:
         - The system tenant ID: {{tenant_id}}.
         - The location of the client: {{country}}.

        {{internal_forms}}

        Observations:
        {{observation}}
    """.trimIndent()
            .replace("{{query}}", query)
            .replace("{{observation}}", memory.map { entry -> "- $entry" }.joinToString("\n"))
            .replace("{{tenant_id}}", account.tenantId.toString())
            .replace(
                "{{country}}",
                account.shippingCountry?.let { country -> Locale("en", country).displayCountry } ?: "UNKNOWN",
            )
    }

    override fun tools(): List<Tool> = emptyList<Tool>()
}
