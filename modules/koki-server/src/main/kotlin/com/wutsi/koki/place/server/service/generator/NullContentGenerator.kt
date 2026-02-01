package com.wutsi.koki.place.server.service.generator

import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceContentGenerator
import com.wutsi.koki.place.server.service.ai.NeighbourhoodContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.PlaceAgentFactory
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Service
class NeighbourhoodContentGenerator(
    private val factory: PlaceAgentFactory,
    private val jsonMapper: JsonMapper,
    private val persister: NeighbourhoodPersister,
) : PlaceContentGenerator {
    override fun generate(
        place: PlaceEntity,
        neighbourhood: LocationEntity,
        city: LocationEntity
    ) {
        val agent = factory.createNeighborhoodContentGeneratorAgent(neighbourhood, city)
        val json = agent.run("")
        val result = jsonMapper.readValue(json, NeighbourhoodContentGeneratorResult::class.java)
        persister.updateNeighbourhood(place, neighbourhood, result)
    }
}
