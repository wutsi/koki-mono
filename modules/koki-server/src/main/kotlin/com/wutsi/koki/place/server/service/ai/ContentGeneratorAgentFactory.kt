package com.wutsi.koki.place.server.service.ai

import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service

@Service
class ContentGeneratorAgentFactory(private val locationService: LocationService) {
    fun get(request: CreatePlaceRequest): Agent {
        when (request.type) {
            PlaceType.NEIGHBORHOOD -> createNeighbourhoodAgent(request)
            else -> throw IllegalArgumentException("Unsupported place type: ${request.type}")
        }
    }

    private fun createNeighbourhoodAgent(request: CreatePlaceRequest): NeighbourhoodContentGeneratorAgent {
        val city = locationService.get(request.neighbourhoodId!!)
        val neighbourhood = locationService.getLocation(request.parentLocationId!!)
        return NeighbourhoodContentGeneratorAgent(city, neighbourhood, llm = null!!)
    }
}
