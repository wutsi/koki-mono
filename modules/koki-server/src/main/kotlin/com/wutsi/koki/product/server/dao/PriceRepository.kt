package com.wutsi.koki.product.server.dao

import com.wutsi.koki.product.server.domain.PriceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PriceRepository : CrudRepository<PriceEntity, Long>
