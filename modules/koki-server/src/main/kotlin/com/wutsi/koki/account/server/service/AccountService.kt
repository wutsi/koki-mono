package com.wutsi.koki.account.server.service

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.UpdateAccountRequest
import com.wutsi.koki.account.server.dao.AccountAttributeRepository
import com.wutsi.koki.account.server.dao.AccountRepository
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.server.domain.AccountAttributeEntity
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AccountService(
    private val dao: AccountRepository,
    private val attributeDao: AccountAttributeRepository,
    private val securityService: SecurityService,
    private val locationService: LocationService,
    private var em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): AccountEntity {
        val account = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.ACCOUNT_NOT_FOUND)) }

        if (account.tenantId != tenantId || account.deleted) {
            throw NotFoundException(Error(ErrorCode.ACCOUNT_NOT_FOUND))
        }
        return account
    }

    @Transactional
    fun create(request: CreateAccountRequest, tenantId: Long): AccountEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val shippingCity = request.shippingCityId?.let { id -> locationService.get(id, LocationType.CITY) }
        val billingCity = request.billingCityId?.let { id -> locationService.get(id, LocationType.CITY) }

        val account = dao.save(
            AccountEntity(
                tenantId = tenantId,
                accountTypeId = request.accountTypeId,
                name = request.name,
                phone = request.phone,
                email = request.email,
                mobile = request.mobile,
                website = request.website,
                language = request.language?.lowercase(),
                description = request.description,
                managedById = request.managedById,
                createdById = userId,
                modifiedById = userId,

                shippingStreet = request.shippingStreet,
                shippingCityId = shippingCity?.id,
                shippingStateId = shippingCity?.parentId,
                shippingCountry = (shippingCity?.country ?: request.shippingCountry)?.uppercase(),
                shippingPostalCode = request.shippingPostalCode,

                billingSameAsShippingAddress = request.billingSameAsShippingAddress,
                billingStreet = nullIfTrue(request.billingSameAsShippingAddress, request.billingStreet),
                billingCityId = nullIfTrue(request.billingSameAsShippingAddress, billingCity?.id),
                billingStateId = nullIfTrue(request.billingSameAsShippingAddress, billingCity?.parentId),
                billingCountry = nullIfTrue(
                    request.billingSameAsShippingAddress,
                    (billingCity?.country ?: request.billingCountry)?.uppercase()
                ),
                billingPostalCode = nullIfTrue(request.billingSameAsShippingAddress, request.billingPostalCode),
            )
        )

        request.attributes.forEach { entry ->
            if (entry.value.trim().isNotEmpty()) {
                attributeDao.save(
                    AccountAttributeEntity(
                        attributeId = entry.key,
                        accountId = account.id!!,
                        value = entry.value
                    )
                )
            }
        }

        return account
    }

    @Transactional
    fun update(id: Long, request: UpdateAccountRequest, tenantId: Long) {
        val account = get(id, tenantId)
        val shippingCity = request.shippingCityId?.let { id -> locationService.get(id, LocationType.CITY) }
        val billingCity = request.billingCityId?.let { id -> locationService.get(id, LocationType.CITY) }

        account.name = request.name
        account.phone = request.phone
        account.accountTypeId = request.accountTypeId
        account.email = request.email
        account.mobile = request.mobile
        account.website = request.website
        account.language = request.language?.lowercase()

        account.shippingStreet = request.shippingStreet
        account.shippingCityId = shippingCity?.id
        account.shippingStateId = shippingCity?.parentId
        account.shippingPostalCode = request.shippingPostalCode
        account.shippingCountry = (shippingCity?.country ?: request.shippingCountry)?.uppercase()

        account.billingSameAsShippingAddress = request.billingSameAsShippingAddress
        account.billingStreet = nullIfTrue(request.billingSameAsShippingAddress, request.billingStreet)
        account.billingCityId = nullIfTrue(request.billingSameAsShippingAddress, billingCity?.id)
        account.billingStateId = nullIfTrue(request.billingSameAsShippingAddress, billingCity?.parentId)
        account.billingPostalCode = nullIfTrue(request.billingSameAsShippingAddress, request.billingPostalCode)
        account.billingCountry = nullIfTrue(
            request.billingSameAsShippingAddress,
            (billingCity?.country ?: request.billingCountry)?.uppercase()
        )

        account.description = request.description
        account.managedById = request.managedById
        account.modifiedById = securityService.getCurrentUserIdOrNull()
        dao.save(account)

        // Add new attributes
        val save = mutableListOf<AccountAttributeEntity>()
        val attributeMap = account.accountAttributes.associateBy { it.attributeId }
        request.attributes.forEach { entry ->
            if (!attributeMap.containsKey(entry.key) && entry.value.isNotEmpty()) {
                save.add(
                    AccountAttributeEntity(
                        attributeId = entry.key,
                        accountId = id,
                        value = entry.value.ifEmpty { null }
                    )
                )
            }
        }

        // Update attributes
        account.accountAttributes.forEach { accountAttribute ->
            if (request.attributes.containsKey(accountAttribute.attributeId)) {
                val value = request.attributes[accountAttribute.attributeId]?.ifEmpty { null }
                if (value != accountAttribute.value) {
                    accountAttribute.value = value
                    accountAttribute.modifiedAt = Date()
                    save.add(accountAttribute)
                }
            } else {
                accountAttribute.value = null
                save.add(accountAttribute)
            }
        }

        if (save.isNotEmpty()) {
            attributeDao.saveAll(save)
        }
    }

    private fun <T> nullIfTrue(flag: Boolean, value: T): T? {
        return if (flag) null else value
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val account = get(id, tenantId)
        account.deleted = true
        account.deletedAt = Date()
        account.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(account)
    }

    fun search(
        tenantId: Long,
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        accountTypeIds: List<Long> = emptyList(),
        managedByIds: List<Long> = emptyList(),
        createdByIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<AccountEntity> {
        val jql = StringBuilder("SELECT A FROM AccountEntity A WHERE A.deleted=false AND A.tenantId = :tenantId")
        if (keyword != null) {
            jql.append(" AND ((UPPER(A.name) LIKE :keyword) OR (UPPER(A.email) LIKE :keyword))")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (managedByIds.isNotEmpty()) {
            jql.append(" AND A.managedById IN :managedByIds")
        }
        if (createdByIds.isNotEmpty()) {
            jql.append(" AND A.createdById IN :createdByIds")
        }
        if (accountTypeIds.isNotEmpty()) {
            jql.append(" AND A.accountTypeId IN :accountTypeIds")
        }
        jql.append(" ORDER BY A.name")

        val query = em.createQuery(jql.toString(), AccountEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (keyword != null) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (managedByIds.isNotEmpty()) {
            query.setParameter("managedByIds", managedByIds)
        }
        if (createdByIds.isNotEmpty()) {
            query.setParameter("createdByIds", createdByIds)
        }
        if (accountTypeIds.isNotEmpty()) {
            query.setParameter("accountTypeIds", accountTypeIds)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
