package com.wutsi.koki.portal

import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.agent.dto.Agent
import com.wutsi.koki.agent.dto.AgentSummary

object AgentFixtures {
    val agent = Agent(
        id = 100,
        userId = users[0].id,
        qrCodeUrl = "https://www.freepnglogos.com/uploads/qr-code-png/qr-code-blackberry-code-variant-technology-icons-32.png"
    )

    val agents = listOf(
        AgentSummary(
            id = 100,
            userId = users[0].id,
        ),
        AgentSummary(
            id = 101,
            userId = users[1].id,
        ),
        AgentSummary(
            id = 102,
            userId = users[2].id,
        ),
    )
}
