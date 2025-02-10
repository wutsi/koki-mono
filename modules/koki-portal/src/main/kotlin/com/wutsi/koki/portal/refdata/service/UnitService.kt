package com.wutsi.koki.portal.product.service

import com.wutsi.koki.portal.product.mapper.ProductMapper
import com.wutsi.koki.portal.product.model.UnitModel
import com.wutsi.koki.sdk.KokiProducts
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UnitService(
    private val koki: KokiProducts,
    private val mapper: ProductMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UnitService::class.java)
    }

    private var all: List<UnitModel>? = null

    fun units(): List<UnitModel> {
        if (all == null){
            all = koki.units().units.map{unit -> mapper.toUnitModel(unit)}
            LOGGER.info("${all?.size} Unit(s) loaded")
        }
        return all!!
    }

    fun unit(id: Long): UnitModel? {
        return units().find{unit -> unit.id  == id}
    }
}
