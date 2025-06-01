package com.wutsi.koki.room.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.room.dto.AddAmenityRequest
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.SaveRoomGeoLocationRequest
import com.wutsi.koki.room.dto.SetHeroImageRequest
import com.wutsi.koki.room.dto.UpdateRoomRequest
import com.wutsi.koki.room.server.dao.RoomRepository
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import java.util.Date

@Service
class RoomService(
    private val dao: RoomRepository,
    private val em: EntityManager,
    private val securityService: SecurityService,
    private val locationService: LocationService,
    private val amenityService: AmenityService,
    private val fileService: FileService,
    private val validator: RoomPublisherValidator,
) {
    fun get(id: Long, tenantId: Long): RoomEntity {
        val room = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.ROOM_NOT_FOUND)) }

        if (room.tenantId != tenantId || room.deleted) {
            throw NotFoundException(Error(ErrorCode.ROOM_NOT_FOUND))
        }
        return room
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        accountIds: List<Long> = emptyList(),
        cityId: Long? = null,
        neighborhoodId: Long? = null,
        status: RoomStatus? = null,
        types: List<RoomType> = emptyList(),
        totalGuests: Int? = null,
        amenityIds: List<Long> = emptyList(),
        minRooms: Int? = null,
        maxRooms: Int? = null,
        minBathrooms: Int? = null,
        maxBathrooms: Int? = null,
        categoryIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<RoomEntity> {
        val jql = StringBuilder("SELECT R FROM RoomEntity R")
        if (amenityIds.isNotEmpty()) {
            jql.append(" JOIN R.amenities A")
        }

        jql.append(" WHERE R.deleted=false AND R.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND R.id IN :ids")
        }
        if (cityId != null) {
            jql.append(" AND R.cityId = :cityId")
        }
        if (neighborhoodId != null) {
            jql.append(" AND R.neighborhoodId = :neighborhoodId")
        }
        if (accountIds.isNotEmpty()) {
            jql.append(" AND R.accountId IN :accountIds")
        }
        if (status != null) {
            jql.append(" AND R.status IN :status")
        }
        if (types.isNotEmpty()) {
            jql.append(" AND R.type IN :types")
        }
        if (totalGuests != null) {
            jql.append(" AND R.maxGuests >= :totalGuests")
        }
        if (minRooms != null) {
            jql.append(" AND R.numberOfRooms >= :minRooms")
        }
        if (maxRooms != null) {
            jql.append(" AND R.numberOfRooms <= :maxRooms")
        }
        if (minBathrooms != null) {
            jql.append(" AND R.numberOfBathrooms >= :minBathrooms")
        }
        if (maxBathrooms != null) {
            jql.append(" AND R.numberOfBathrooms <= :maxBathrooms")
        }
        if (categoryIds.isNotEmpty()) {
            jql.append(" AND R.categoryId IN :categoryIds")
        }
        if (amenityIds.isNotEmpty()) {
            jql.append(" AND A.id IN :amenityIds")
        }
        jql.append(" ORDER BY R.pricePerNight")

        val query = em.createQuery(jql.toString(), RoomEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (cityId != null) {
            query.setParameter("cityId", cityId)
        }
        if (neighborhoodId != null) {
            query.setParameter("neighborhoodId", neighborhoodId)
        }
        if (accountIds.isNotEmpty()) {
            query.setParameter("accountIds", accountIds)
        }
        if (status != null) {
            query.setParameter("status", status)
        }
        if (types.isNotEmpty()) {
            query.setParameter("types", types)
        }
        if (totalGuests != null) {
            query.setParameter("totalGuests", totalGuests)
        }
        if (minRooms != null) {
            query.setParameter("minRooms", minRooms)
        }
        if (maxRooms != null) {
            query.setParameter("maxRooms", maxRooms)
        }
        if (minBathrooms != null) {
            query.setParameter("minBathrooms", minBathrooms)
        }
        if (maxBathrooms != null) {
            query.setParameter("maxBathrooms", maxBathrooms)
        }
        if (amenityIds.isNotEmpty()) {
            query.setParameter("amenityIds", amenityIds)
        }
        if (categoryIds.isNotEmpty()) {
            query.setParameter("categoryIds", categoryIds)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateRoomRequest, tenantId: Long): RoomEntity {
        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        val city = request.cityId?.let { id -> locationService.get(id, LocationType.CITY) }
        return dao.save(
            RoomEntity(
                tenantId = tenantId,
                accountId = request.accountId,
                type = request.type,
                status = RoomStatus.DRAFT,
                maxGuests = request.maxGuests,
                numberOfBathrooms = request.numberOfBathrooms,
                numberOfRooms = request.numberOfRooms,
                numberOfBeds = request.numberOfBeds,
                area = request.area,
                pricePerNight = if (request.pricePerNight == 0.0) null else request.pricePerNight,
                pricePerMonth = if (request.pricePerMonth == 0.0) null else request.pricePerMonth,
                currency = request.currency,
                checkinTime = request.checkinTime,
                checkoutTime = request.checkoutTime,
                leaseTerm = request.leaseTerm,
                furnishedType = request.furnishedType,
                categoryId = request.categoryId,
                leaseType = request.leaseType,
                latitude = request.latitude,
                longitude = request.longitude,

                street = request.street,
                postalCode = request.postalCode,
                cityId = request.cityId,
                stateId = city?.parentId,
                neighborhoodId = request.neighborhoodId,
                country = city?.country,

                createdById = userId,
                modifiedById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateRoomRequest, tenantId: Long): RoomEntity {
        val room = get(id, tenantId)
        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        val city = request.cityId?.let { id -> locationService.get(id, LocationType.CITY) }

        room.type = request.type
        room.title = request.title
        room.description = request.description
        room.summary = request.summary
        room.maxGuests = request.maxGuests
        room.numberOfBathrooms = request.numberOfBathrooms
        room.numberOfRooms = request.numberOfRooms
        room.numberOfBeds = request.numberOfBeds
        room.area = request.area
        room.pricePerNight = if (request.pricePerNight == 0.0) null else request.pricePerNight
        room.pricePerMonth = if (request.pricePerMonth == 0.0) null else request.pricePerMonth
        room.currency = request.currency
        room.checkinTime = request.checkinTime
        room.checkoutTime = request.checkoutTime
        room.leaseTerm = request.leaseTerm
        room.furnishedType = request.furnishedType
        room.categoryId = request.categoryId
        room.leaseType = request.leaseType

        room.street = request.street
        room.postalCode = request.postalCode
        room.cityId = request.cityId
        room.neighborhoodId = request.neighborhoodId
        room.stateId = city?.parentId
        room.country = city?.country

        room.modifiedById = userId
        room.modifiedAt = now

        return dao.save(room)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val room = get(id, tenantId)
        if (!room.deleted) {
            room.deletedAt = Date()
            room.deleteById = securityService.getCurrentUserIdOrNull()
            room.deleted = true
            dao.save(room)
        }
    }

    @Transactional
    fun geo(id: Long, request: SaveRoomGeoLocationRequest, tenantId: Long) {
        val room = get(id, tenantId)
        room.latitude = request.latitude
        room.longitude = request.longitude
        room.modifiedById = securityService.getCurrentUserIdOrNull()
        room.modifiedAt = Date()
        save(room)
    }

    @Transactional
    fun save(room: RoomEntity): RoomEntity {
        return dao.save(room)
    }

    @Transactional
    fun addAmenities(id: Long, request: AddAmenityRequest, tenantId: Long): List<AmenityEntity> {
        val room = get(id, tenantId)
        val amenities = amenityService.search(
            ids = request.amenityIds,
            limit = request.amenityIds.size,
        )
        val added = mutableListOf<AmenityEntity>()
        amenities.forEach { amenity ->
            if (!room.amenities.contains(amenity)) {
                room.amenities.add(amenity)
                added.add(amenity)
            }
        }

        if (added.isNotEmpty()) {
            dao.save(room)
        }
        return added
    }

    @Transactional
    fun removeAmenity(id: Long, amenityId: Long, tenantId: Long): Boolean {
        val room = get(id, tenantId)
        val amenity = amenityService.get(amenityId)
        if (room.amenities.contains(amenity)) {
            room.amenities.remove(amenity)
            dao.save(room)
            return true
        } else {
            return false
        }
    }

    @Transactional
    fun startPublishing(id: Long, tenantId: Long): RoomEntity? {
        val room = get(id, tenantId)
        try {
            validator.validate(room)
            if (room.status == RoomStatus.DRAFT) {
                room.status = RoomStatus.PUBLISHING
                room.publishedAt = Date()
                room.publishedById = securityService.getCurrentUserIdOrNull()
                return dao.save(room)
            } else if (room.status == RoomStatus.PUBLISHING || room.status == RoomStatus.PUBLISHED) {
                return null
            } else {
                throw ConflictException(
                    error = Error(
                        code = ErrorCode.ROOM_INVALID_STATUS,
                        data = mapOf("status" to room.status)
                    )
                )
            }
        } catch (ex: ValidationException) {
            throw ConflictException(
                error = Error(
                    code = ex.message ?: ""
                )
            )
        }
    }

    @Transactional
    fun setHeroImage(id: Long, request: SetHeroImageRequest, tenantId: Long) {
        val room = get(id, tenantId)
        val image = fileService.get(request.fileId, tenantId)
        if (image.type != FileType.IMAGE) {
            throw ConflictException(error = Error(ErrorCode.ROOM_IMAGE_NOT_VALID))
        }
        if (image.ownerId != id || image.ownerType != ObjectType.ROOM) {
            throw ConflictException(error = Error(ErrorCode.ROOM_IMAGE_NOT_OWNED))
        }

        room.heroImageId = request.fileId
        dao.save(room)
    }
}
