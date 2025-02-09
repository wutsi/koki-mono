package com.wutsi.koki.portal.product.service

import com.wutsi.koki.portal.product.form.ProductForm
import com.wutsi.koki.portal.product.mapper.ProductMapper
import com.wutsi.koki.portal.product.model.ProductModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.UpdateProductRequest
import com.wutsi.koki.sdk.KokiProducts
import org.springframework.stereotype.Service
import kotlin.collections.flatMap

@Service
class ProductService(
    private val koki: KokiProducts,
    private val mapper: ProductMapper,
    private val userService: UserService,
) {
    fun product(id: Long, fullGraph: Boolean = true): ProductModel {
        val product = koki.product(id).product

        val userIds = listOf(product.createdById, product.modifiedById).filterNotNull().toSet()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size,
            ).associateBy { user -> user.id }
        }

        return mapper.toProductModel(product, users)
    }

    fun products(
        ids: List<Long> = emptyList(),
        types: List<ProductType> = emptyList(),
        active: Boolean? = null,
        limit: Int = 10,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<ProductModel> {
        val products = koki.products(
            ids = ids,
            types = types,
            active = active,
            limit = limit,
            offset = offset,
        ).products

        val userIds =
            products.flatMap { product -> listOf(product.createdById, product.modifiedById) }.filterNotNull().toSet()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size,
            ).associateBy { user -> user.id }
        }

        return products.map { product -> mapper.toProductModel(product, users) }
    }

    fun create(form: ProductForm): Long {
        return koki.create(
            CreateProductRequest(
                name = form.name,
                code = form.code?.trim()?.ifEmpty { null },
                description = form.description?.trim()?.ifEmpty { null },
                active = form.active,
                type = form.type,
            )
        ).productId
    }

    fun update(id: Long, form: ProductForm) {
        return koki.update(
            id, UpdateProductRequest(
                name = form.name,
                code = form.code?.trim()?.ifEmpty { null },
                description = form.description?.trim()?.ifEmpty { null },
                active = form.active,
                type = form.type,
            )
        )
    }

    fun delete(id: Long) {
        koki.delete(id)
    }
}
