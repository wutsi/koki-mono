package com.wutsi.koki.room.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.room.dto.AddAmenityRequest
import com.wutsi.koki.room.dto.AddImageRequest
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.UpdateRoomRequest
import com.wutsi.koki.room.server.dao.RoomRepository
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
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
        cityId: Long? = null,
        status: RoomStatus? = null,
        type: RoomType? = null,
        totalGuests: Int? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<RoomEntity> {
        val jql = StringBuilder("SELECT A FROM RoomEntity A WHERE A.deleted=false AND A.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (cityId != null) {
            jql.append(" AND A.cityId IN :cityId")
        }
        if (status != null) {
            jql.append(" AND A.status IN :status")
        }
        if (type != null) {
            jql.append(" AND A.type IN :type")
        }
        if (totalGuests != null) {
            jql.append(" AND A.maxGuests >= :totalGuests")
        }
        jql.append(" ORDER BY A.pricePerNight")

        val query = em.createQuery(jql.toString(), RoomEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (cityId != null) {
            query.setParameter("cityId", cityId)
        }
        if (status != null) {
            query.setParameter("status", status)
        }
        if (type != null) {
            query.setParameter("type", type)
        }
        if (totalGuests != null) {
            query.setParameter("totalGuests", totalGuests)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateRoomRequest, tenantId: Long): RoomEntity {
        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        val city = locationService.get(request.cityId, LocationType.CITY)
        return dao.save(
            RoomEntity(
                tenantId = tenantId,
                type = request.type,
                status = RoomStatus.DRAFT,
                title = request.title,
                description = request.description,
                maxGuests = request.maxGuests,
                numberOfBathrooms = request.numberOfBathrooms,
                numberOfRooms = request.numberOfRooms,
                numberOfBeds = request.numberOfBeds,
                pricePerNight = request.pricePerNight,
                currency = request.currency,

                street = request.street,
                postalCode = request.postalCode,
                cityId = request.cityId,
                stateId = city.parentId,
                country = city.country,

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
        val city = locationService.get(request.cityId, LocationType.CITY)

        room.type = request.type
        room.title = request.title
        room.description = request.description
        room.maxGuests = request.maxGuests
        room.numberOfBathrooms = request.numberOfBathrooms
        room.numberOfRooms = request.numberOfRooms
        room.numberOfBeds = request.numberOfBeds
        room.pricePerNight = request.pricePerNight
        room.currency = request.currency

        room.street = request.street
        room.postalCode = request.postalCode
        room.cityId = request.cityId
        room.stateId = city.parentId
        room.country = city.country

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
    fun addFiles(id: Long, request: AddImageRequest, tenantId: Long): List<FileEntity> {
        val room = get(id, tenantId)
        val files = fileService.search(
            tenantId = tenantId,
            ids = request.fileIds,
            limit = request.fileIds.size,
        ).filter { file -> file.contentType.startsWith("image/") }

        val added = mutableListOf<FileEntity>()
        files.forEach { file ->
            if (!room.images.contains(file)) {
                room.images.add(file)
                added.add(file)
            }
        }

        if (added.isNotEmpty()) {
            dao.save(room)
        }
        return added
    }

    @Transactional
    fun removeFile(id: Long, fileId: Long, tenantId: Long): Boolean {
        val room = get(id, tenantId)
        val amenity = fileService.get(fileId, tenantId)
        if (room.images.contains(amenity)) {
            room.images.remove(amenity)
            dao.save(room)
            return true
        } else {
            return false
        }
    }
}
