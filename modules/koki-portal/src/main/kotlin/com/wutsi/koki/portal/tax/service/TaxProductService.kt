package com.wutsi.koki.portal.tax.service

import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.tax.form.TaxProductForm
import com.wutsi.koki.portal.tax.mapper.TaxMapper
import com.wutsi.koki.portal.tax.model.TaxProductModel
import com.wutsi.koki.sdk.KokiTaxes
import com.wutsi.koki.tax.dto.CreateTaxProductRequest
import com.wutsi.koki.tax.dto.UpdateTaxProductRequest
import org.springframework.stereotype.Service

@Service
class TaxProductService(
    private val koki: KokiTaxes,
    private val mapper: TaxMapper,
    private val productService: ProductService,
) {
    fun product(id: Long): TaxProductModel {
        val taxProduct = koki.product(id).taxProduct
        val product = productService.product(id, fullGraph = false)
        return mapper.toTaxProductModel(taxProduct, mapOf(product.id to product))
    }

    fun products(
        taxIds: List<Long> = emptyList(),
        productIds: List<Long> = emptyList(),
        unitPriceIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<TaxProductModel> {
        val taxProducts = koki.products(
            taxIds = taxIds,
            productIds = productIds,
            unitPriceIds = unitPriceIds,
            limit = limit,
            offset = offset,
        ).taxProducts

        // Products
        val productIds = taxProducts.map { taxProduct -> taxProduct.productId }.toSet()
        val products = productService.products(
            ids = productIds.toList(),
            limit = productIds.size,
        ).associateBy { product -> product.id }

        return taxProducts.map { taxProduct ->
            mapper.toTaxProductModel(
                entity = taxProduct,
                products = products,
            )
        }
    }

    fun delete(id: Long) {
        koki.deleteProduct(id)
    }

    fun add(form: TaxProductForm): Long {
        return koki.addProduct(
            CreateTaxProductRequest(
                productId = form.productId,
                taxId = form.taxId,
                quantity = form.quantity,
                unitPriceId = form.unitPriceId,
            )
        ).taxProductId
    }

    fun update(id: Long, form: TaxProductForm) {
        koki.updateProduct(
            id, UpdateTaxProductRequest(
                quantity = form.quantity,
                unitPriceId = form.unitPriceId,
                description = form.description,
            )
        )
    }
}
