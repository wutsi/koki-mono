package com.wutsi.koki.tax.server.service.ai

import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.form.server.service.FormService
import org.springframework.stereotype.Service

@Service
class TaxAgentFactory(
    private val llmProvider: LLMProvider,
    private val formService: FormService,
) {
    fun createTaxFileAgent(account: AccountEntity): TaxFileAgent {
        return TaxFileAgent(
            llm = llmProvider.get(account.tenantId),
            formService = formService,
            account = account
        )
    }
}
