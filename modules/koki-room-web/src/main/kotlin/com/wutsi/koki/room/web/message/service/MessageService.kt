package com.wutsi.koki.room.web.message.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.room.web.geoip.service.CurrentGeoIPHolder
import com.wutsi.koki.room.web.message.form.SendMessageForm
import com.wutsi.koki.room.web.refdata.model.LocationService
import com.wutsi.koki.sdk.KokiMessages
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val koki: KokiMessages,
    private val currentGeoIp: CurrentGeoIPHolder,
    private val locationService: LocationService,
) {
    fun send(form: SendMessageForm) {
        val geoIp = currentGeoIp.get()
        val city = geoIp?.let {
            locationService.locations(keyword = geoIp.city, limit = 1)
                .firstOrNull()
        }

        val request = SendMessageRequest(
            senderName = form.name,
            senderPhone = form.phoneFull.ifEmpty { null },
            senderEmail = form.email,
            owner = ObjectReference(id = form.roomId, type = ObjectType.ROOM),
            body = form.body,
            language = LocaleContextHolder.getLocale().language,
            cityId = city?.id,
            country = form.phoneCountry
        )
        koki.send(request)
    }
}
