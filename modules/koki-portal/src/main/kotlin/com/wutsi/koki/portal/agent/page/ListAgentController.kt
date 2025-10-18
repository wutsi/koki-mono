package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.portal.agent.model.AgentMetricModel
import com.wutsi.koki.portal.agent.model.AgentMetricSetModel
import com.wutsi.koki.portal.agent.model.AgentModel
import com.wutsi.koki.portal.common.model.MoneyModel
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/agents")
@RequiresPermission(["agent"])
class ListAgentController : AbstractAgentController() {
    @GetMapping
    fun list(model: Model): String {
        val agents = findAgents()
        model.addAttribute("agents", agents)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.AGENT_LIST,
                title = "Agents",
            )
        )
        return "agents/list"
    }

    private fun findAgents(): List<AgentModel> {
        return listOf(
            AgentModel(
                user = UserModel(
                    displayName = "Roger Milla",
                    employer = "ZILLOW",
                    photoUrl = "https://picsum.photos/600/600",
                    city = LocationModel(name = "Montreal")
                ),
                pyMetrics = AgentMetricSetModel(
                    sales = AgentMetricModel(
                        total = 121,
                        minPrice = MoneyModel(shortText = "$50K"),
                        maxPrice = MoneyModel(shortText = "$1M")
                    ),
                ),
                metrics = AgentMetricSetModel(
                    sales = AgentMetricModel(
                        total = 1215,
                        minPrice = MoneyModel(shortText = "$50K"),
                        maxPrice = MoneyModel(shortText = "$1M")
                    ),
                ),
            ),

            AgentModel(
                user = UserModel(
                    displayName = "Roger Milla",
                    employer = "ZILLOW",
                    photoUrl = "https://picsum.photos/600/600",
                    city = LocationModel(name = "Laval")
                ),
                pyMetrics = AgentMetricSetModel(
                    rentals = AgentMetricModel(
                        total = 77,
                        minPrice = MoneyModel(shortText = "$550"),
                        maxPrice = MoneyModel(shortText = "$2K")
                    ),
                ),
                metrics = AgentMetricSetModel(
                    rentals = AgentMetricModel(
                        total = 300,
                        minPrice = MoneyModel(shortText = "$550"),
                        maxPrice = MoneyModel(shortText = "$4K")
                    ),
                ),
            ),

            AgentModel(
                user = UserModel(
                    displayName = "James Bond",
                    employer = "REDFIN",
                    photoUrl = "https://picsum.photos/500/500",
                    city = LocationModel(name = "Douala")
                ),
                pyMetrics = AgentMetricSetModel(
                    rentals = AgentMetricModel(
                        total = 77,
                        minPrice = MoneyModel(shortText = "$550"),
                        maxPrice = MoneyModel(shortText = "$2K")
                    ),
                ),
                metrics = AgentMetricSetModel(
                    rentals = AgentMetricModel(
                        total = 300,
                        minPrice = MoneyModel(shortText = "$550"),
                        maxPrice = MoneyModel(shortText = "$4K")
                    ),
                ),
            ),
        )
    }
}
