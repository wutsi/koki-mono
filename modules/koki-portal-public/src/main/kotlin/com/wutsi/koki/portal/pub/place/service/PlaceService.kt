package com.wutsi.koki.portal.pub.place.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.portal.pub.common.model.ResultSetModel
import com.wutsi.koki.portal.pub.file.model.FileModel
import com.wutsi.koki.portal.pub.file.service.FileService
import com.wutsi.koki.portal.pub.place.mapper.PlaceMapper
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.sdk.KokiPlaces
import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val koki: KokiPlaces,
    private val mapper: PlaceMapper,
    private val fileService: FileService,
) {
    fun get(id: Long, fullGraph: Boolean = true): PlaceModel {
        val place = koki.get(id).place

        val images = if (!fullGraph) {
            emptyMap<Long, FileModel>()
        } else {
            fileService.search(
                type = FileType.IMAGE,
                ownerId = id,
                ownerType = ObjectType.PLACE,
                limit = 100,
            ).associateBy { image -> image.id }
        }

        return mapper.toPlaceModel(
            entity = place,
            images = images,
        )
    }

    fun search(
        neighbourhoodIds: List<Long> = emptyList(),
        cityIds: List<Long> = emptyList(),
        types: List<PlaceType> = emptyList(),
        statuses: List<PlaceStatus> = emptyList(),
        keyword: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): ResultSetModel<PlaceModel> {
        val response = koki.search(
            neighbourhoodIds = neighbourhoodIds,
            cityIds = cityIds,
            types = types,
            statuses = statuses,
            keyword = keyword,
            limit = limit,
            offset = offset,
        )
        val places = response.places

        val imageIds = places.mapNotNull { place -> place.heroImageId }
        val images = if (!fullGraph || imageIds.isEmpty()) {
            emptyMap<Long, FileModel>()
        } else {
            fileService.search(
                ids = imageIds,
                limit = imageIds.size
            ).associateBy { image -> image.id }
        }

        return ResultSetModel(
            total = places.size.toLong(),
            items = places.map { place ->
                mapper.toPlaceModel(
                    entity = place,
                    images = images
                )
            }
        )
    }
}
