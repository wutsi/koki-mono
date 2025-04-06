package com.wutsi.koki.tax.server.agent.categorizer

import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.FunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.FunctionParameters
import com.wutsi.koki.platform.ai.llm.Type
import org.apache.commons.io.IOUtils

/**
 * Return the list of official forms required by Revenue Services and Agencies of different countries
 * - For CA, see https://www.canada.ca/en/revenue-agency/services/forms-publications/forms.html
 */
class TaxFormTool : Tool {
    override fun function(): FunctionDeclaration {
        return FunctionDeclaration(
            name = "tax_form_list",
            description = "Return the list of official forms required by Revenue Services and Agencies of different countries",
            parameters = FunctionParameters(
                properties = mapOf(
                    "country_code" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "ISO 3166-1 two-letter country code"
                    )
                ),
                required = listOf("country_code")
            )
        )
    }

    override fun use(args: Map<String, Any>): String {
        val countryCode = args["country_code"].toString().uppercase()
        val input = TaxFormTool::class.java.getResourceAsStream("/tax/forms/$countryCode.csv")
        if (input == null){
            return "No form "
        } else {
            return IOUtils.toString(input, "utf-8")
        }
    }
}
