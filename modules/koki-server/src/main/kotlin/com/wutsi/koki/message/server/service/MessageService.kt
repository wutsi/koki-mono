package com.wutsi.koki.message.server.service

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import com.wutsi.koki.message.server.dao.MessageRepository
import com.wutsi.koki.message.server.domain.MessageEntity
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class MessageService(
    private val dao: MessageRepository,
    private val em: EntityManager,
    private val accountService: AccountService,
    private val locationService: LocationService,
) {
    fun get(id: Long, tenantId: Long): MessageEntity {
        val msg = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND)) }

        if (msg.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND))
        }
        return msg
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        statuses: List<MessageStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<MessageEntity> {
        val jql = StringBuilder("SELECT M FROM MessageEntity AS M")

        jql.append(" WHERE M.tenantId=:tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND M.id IN :ids")
        }
        if (ownerId != null) {
            jql.append(" AND M.ownerId = :ownerId")
        }
        if (ownerType != null) {
            jql.append(" AND M.ownerType = :ownerType")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND M.status IN :statuses")
        }
        jql.append(" ORDER BY M.createdAt DESC")

        val query = em.createQuery(jql.toString(), MessageEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (ownerId != null) {
            query.setParameter("ownerId", ownerId)
        }
        if (ownerType != null) {
            query.setParameter("ownerType", ownerType)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun send(request: SendMessageRequest, tenantId: Long): MessageEntity {
        val city = request.cityId?.let { id -> locationService.get(id, LocationType.CITY) }

        return dao.save(
            MessageEntity(
                tenantId = tenantId,
                ownerId = request.owner?.id,
                ownerType = request.owner?.type,
                senderName = request.senderName,
                senderPhone = request.senderPhone,
                senderEmail = request.senderEmail.lowercase(),
                body = request.body,
                createdAt = Date(),
                status = MessageStatus.NEW,
                country = city?.country ?: request.country?.uppercase(),
                language = request.language,
                cityId = city?.id,
                senderAccountId = findOrCreateAccount(request, city, tenantId).id,
            )
        )
    }

    @Transactional
    fun status(id: Long, request: UpdateMessageStatusRequest, tenantId: Long): MessageEntity {
        val message = get(id, tenantId)
        message.status = request.status
        return dao.save(message)
    }

    private fun findOrCreateAccount(request: SendMessageRequest, city: LocationEntity?, tenantId: Long): AccountEntity {
        val account = accountService.getByEmailOrNull(request.senderEmail, tenantId)
        if (account == null) {
            return accountService.create(
                tenantId = tenantId,
                request = CreateAccountRequest(
                    name = request.senderName,
                    email = request.senderEmail,
                    mobile = request.senderPhone,
                    language = request.language,
                    shippingCountry = city?.country ?: request.country?.uppercase(),
                    shippingCityId = city?.id,
                    billingSameAsShippingAddress = true,
                )
            )
        } else {
            var update = 0
            if (account.mobile == null && request.senderPhone != null) {
                account.mobile = request.senderPhone
                update++
            }
            if (account.language == null && request.language != null) {
                account.language = request.language
                update++
            }
            if (account.shippingCountry == null && request.country != null) {
                account.shippingCountry = city?.country ?: request.country?.uppercase()
                update++
            }
            if (account.shippingCityId == null && city != null) {
                account.shippingCityId = city.id
                account.shippingCountry = city.country
                update++
            }

            return if (update > 0) {
                accountService.save(account)
            } else {
                account
            }
        }
    }
}
