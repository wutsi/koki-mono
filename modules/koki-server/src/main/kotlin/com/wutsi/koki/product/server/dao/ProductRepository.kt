package com.wutsi.koki.product.server.dao

import com.wutsi.koki.product.server.domain.ProductEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Long>
