package com.wutsi.koki.portal.pub

import com.wutsi.koki.agent.dto.Agent
import com.wutsi.koki.agent.dto.AgentSummary
import com.wutsi.koki.portal.pub.UserFixtures.users

object AgentFixtures {
    val agent = Agent(
        id = 100,
        userId = users[0].id,
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
