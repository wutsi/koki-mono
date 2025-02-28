package com.wutsi.koki.portal.refdata.service

import com.wutsi.koki.portal.refdata.mapper.RefDataMapper
import com.wutsi.koki.portal.refdata.model.UnitModel
import com.wutsi.koki.sdk.KokiRefData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UnitService(
    private val koki: KokiRefData,
    private val mapper: RefDataMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UnitService::class.java)
    }

    private var all: List<UnitModel>? = null

    fun units(): List<UnitModel> {
        if (all == null) {
            all = koki.units().units.map { unit -> mapper.toUnitModel(unit) }
            LOGGER.info("${all?.size} Unit(s) loaded")
        }
        return all!!
    }

    fun units(ids: List<Long>): List<UnitModel> {
        return units().filter { unit -> ids.contains(unit.id) }
    }

    fun unit(id: Long): UnitModel? {
        return units().find { unit -> unit.id == id }
    }
}
