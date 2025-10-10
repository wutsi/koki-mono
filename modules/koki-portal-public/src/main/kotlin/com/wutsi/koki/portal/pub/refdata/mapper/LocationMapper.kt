package com.wutsi.koki.portal.pub.refdata.mapper

import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import org.springframework.stereotype.Service

@Service
class LocationMapper : TenantAwareMapper() {
    fun toLocationModel(entity: Location): LocationModel {
        return LocationModel(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            type = entity.type,
            country = entity.country,
            longitude = entity.longitude,
            latitude = entity.latitude,
            url = StringUtils.toSlug("/l/${entity.id}", entity.name)
        )
    }
}
