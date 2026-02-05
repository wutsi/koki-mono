package com.wutsi.koki.place.server.service.generator

import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceContentGenerator
import com.wutsi.koki.place.server.service.ai.NeighbourhoodContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.PlaceAgentFactory
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Service
class NeighbourhoodContentGenerator(
    private val factory: PlaceAgentFactory,
    private val jsonMapper: JsonMapper,
    private val persister: NeighbourhoodPersister,
    private val locationService: LocationService,
) : PlaceContentGenerator {
    override fun generate(place: PlaceEntity) {
        val neighbourhood = locationService.get(place.neighbourhoodId!!)
        val city = neighbourhood.parentId?.let { id -> locationService.get(id) }
        val agent = factory.createNeighborhoodContentGeneratorAgent(neighbourhood, city)
        val json = agent.run("")
        val result = jsonMapper.readValue(json, NeighbourhoodContentGeneratorResult::class.java)
        persister.persist(place, neighbourhood, result)
    }
}
