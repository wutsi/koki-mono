package com.wutsi.koki.portal.pub.refdata.mapper

import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class LocationMapper : TenantAwareMapper() {
    fun toLocationModel(entity: Location): LocationModel {
        val tenant = currentTenant.get()
        val locale = LocaleContextHolder.getLocale()
        return LocationModel(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            type = entity.type,
            country = entity.country,
            countryName = Locale(locale.language, entity.country).displayCountry,
            geoLocation = toGeoLocationModel(entity),
            publicUrl = if (entity.type == LocationType.NEIGHBORHOOD) {
                StringUtils.toSlug("${tenant.clientPortalUrl}/neighbourhoods/${entity.id}", entity.name)
            } else {
                "/"
            },
        )
    }

    private fun toGeoLocationModel(entity: Location): GeoLocationModel? {
        return if (entity.latitude != null && entity.longitude != null) {
            GeoLocationModel(
                latitude = entity.latitude!!,
                longitude = entity.longitude!!,
            )
        } else {
            null
        }
    }
}
