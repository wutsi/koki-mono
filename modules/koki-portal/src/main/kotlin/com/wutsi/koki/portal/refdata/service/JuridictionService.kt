package com.wutsi.koki.portal.refdata.service

import com.wutsi.koki.portal.refdata.mapper.RefDataMapper
import com.wutsi.koki.portal.refdata.model.JuridictionModel
import com.wutsi.koki.sdk.KokiRefData
import org.springframework.stereotype.Service

@Service
class JuridictionService(
    private val koki: KokiRefData,
    private val mapper: RefDataMapper,
    private val locationService: LocationService,
) {
    fun juridictions(
        ids: List<Long> = emptyList(),
        stateId: Long? = null,
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<JuridictionModel> {
        val juridictions = koki.juridictions(
            ids = ids,
            stateId = stateId,
            country = country,
            limit = limit,
            offset = offset,
        ).juridictions

        val stateIds = juridictions.mapNotNull { juridiction -> juridiction.stateId }.toSet()
        val states = if (stateIds.isEmpty()) {
            emptyMap()
        } else {
            locationService.search(
                ids = stateIds.toList(),
                limit = stateIds.size
            ).associateBy { juridiction -> juridiction.id }
        }

        return juridictions.map { juridiction -> mapper.toJuridictionModel(juridiction, states) }
            .sortedBy { juridiction ->
                juridiction.country + " - " + (juridiction.state?.let { juridiction.name } ?: "")
            }
    }
}
