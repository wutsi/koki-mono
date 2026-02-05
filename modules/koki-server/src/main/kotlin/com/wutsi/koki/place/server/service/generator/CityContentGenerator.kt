package com.wutsi.koki.place.server.service.generator

import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceContentGenerator
import com.wutsi.koki.place.server.service.ai.CityContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.PlaceAgentFactory
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Service
class CityContentGenerator(
    private val factory: PlaceAgentFactory,
    private val jsonMapper: JsonMapper,
    private val persister: CityPersister,
    private val locationService: LocationService,
) : PlaceContentGenerator {
    override fun generate(place: PlaceEntity) {
        val city = locationService.get(place.cityId)
        val agent = factory.createCityContentGeneratorAgent(city)
        val json = agent.run("")
        val result = jsonMapper.readValue(json, CityContentGeneratorResult::class.java)
        persister.persist(place, city, result)
    }
}
