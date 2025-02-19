package com.wutsi.koki.portal.product.service

import com.wutsi.koki.portal.product.form.PriceForm
import com.wutsi.koki.portal.product.form.ProductForm
import com.wutsi.koki.portal.product.mapper.ProductMapper
import com.wutsi.koki.portal.product.model.PriceModel
import com.wutsi.koki.portal.product.model.ProductModel
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.refdata.service.UnitService
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.UpdatePriceRequest
import com.wutsi.koki.product.dto.UpdateProductRequest
import com.wutsi.koki.sdk.KokiProducts
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.collections.flatMap

@Service
class ProductService(
    private val koki: KokiProducts,
    private val mapper: ProductMapper,
    private val userService: UserService,
    private val typeService: TypeService,
    private val unitService: UnitService,
    private val categoryService: CategoryService,
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

        val units = if (product.serviceDetails?.unitId == null || !fullGraph) {
            emptyMap()
        } else {
            val unit = unitService.unit(product.serviceDetails!!.unitId!!)
            unit?.let {
                mapOf(unit.id to unit)
            } ?: emptyMap()
        }

        val categories = if (product.categoryId == null || !fullGraph) {
            emptyMap()
        } else {
            categoryService.category(product.categoryId!!)
                ?.let { category -> mapOf(category.id to category) }
                ?: emptyMap()
        }

        return mapper.toProductModel(product, units, users, categories)
    }

    fun products(
        ids: List<Long> = emptyList(),
        types: List<ProductType> = emptyList(),
        active: Boolean? = null,
        keyword: String? = null,
        limit: Int = 10,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<ProductModel> {
        val products = koki.products(
            ids = ids,
            types = types,
            active = active,
            keyword = keyword,
            limit = limit,
            offset = offset,
        ).products

        val userIds = products.flatMap { product -> listOf(product.createdById, product.modifiedById) }
            .filterNotNull()
            .toSet()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size,
            ).associateBy { user -> user.id }
        }

        val categoryIds = products.map { product -> product.categoryId }
            .filterNotNull()
            .toSet()
        val categories = if (categoryIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            categoryService.categories(
                ids = categoryIds.toList(),
                limit = categoryIds.size
            ).associateBy { category -> category.id }
        }
        return products.map { product -> mapper.toProductModel(product, users, categories) }
    }

    fun create(form: ProductForm): Long {
        return koki.create(
            CreateProductRequest(
                name = form.name,
                code = form.code?.trim()?.ifEmpty { null },
                description = form.description?.trim()?.ifEmpty { null },
                active = form.active,
                type = form.type,
                unitId = if (form.type == ProductType.SERVICE) toId(form.unitId) else null,
                quantity = if (form.type == ProductType.SERVICE) form.quantity else null,
                categoryId = if (form.categoryId == -1L) null else form.categoryId
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
                unitId = if (form.type == ProductType.SERVICE) toId(form.unitId) else null,
                quantity = if (form.type == ProductType.SERVICE) form.quantity else null,
                categoryId = if (form.categoryId == -1L) null else form.categoryId
            )
        )
    }

    private fun toId(id: Long?): Long? {
        return if (id == null || id == -1L) null else id
    }

    fun delete(id: Long) {
        koki.delete(id)
    }

    fun price(id: Long, fullGraph: Boolean = true): PriceModel {
        val price = koki.price(id).price
        val type = if (price.accountTypeId == null || !fullGraph) {
            null
        } else {
            typeService.type(price.accountTypeId!!)
        }
        return mapper.toPriceModel(price, type)
    }

    fun prices(
        ids: List<Long> = emptyList(),
        productIds: List<Long> = emptyList(),
        accountTypeIds: List<Long> = emptyList(),
        currency: String? = null,
        date: LocalDate? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true
    ): List<PriceModel> {
        val prices = koki.prices(
            ids = ids,
            productIds = productIds,
            accountTypeIds = accountTypeIds,
            currency = currency,
            date = date,
            active = active,
            limit = limit,
            offset = offset
        ).prices

        val typeIds = prices.map { price -> price.accountTypeId }.filterNotNull().toSet()
        val types = if (typeIds.isEmpty() || fullGraph) {
            emptyMap()
        } else {
            typeService.types(
                ids = typeIds.toList(), limit = typeIds.size
            ).associateBy { type -> type.id }
        }

        return prices.map { price -> mapper.toPriceModel(price, types) }
    }

    fun create(form: PriceForm): Long {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return koki.create(CreatePriceRequest(
            productId = form.productId,
            active = form.active,
            name = form.name,
            amount = form.amount,
            currency = form.currency,
            startAt = form.startAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
            endAt = form.endAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
        )
        ).priceId
    }

    fun update(id: Long, form: PriceForm) {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return koki.update(id,
            UpdatePriceRequest(
                active = form.active,
                name = form.name,
                amount = form.amount,
                currency = form.currency,
                startAt = form.startAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                endAt = form.endAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
            )
        )
    }

    fun deletePrice(id: Long) {
        koki.deletePrice(id)
    }
}
