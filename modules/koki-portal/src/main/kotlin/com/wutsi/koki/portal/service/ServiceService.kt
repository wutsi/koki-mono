package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.ServiceMapper
import com.wutsi.koki.portal.model.ServiceModel
import com.wutsi.koki.sdk.KokiServices
import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.dto.CreateServiceRequest
import com.wutsi.koki.service.dto.ServiceSortBy
import com.wutsi.koki.service.dto.UpdateServiceRequest
import org.springframework.stereotype.Service

@Service
class ServiceService(
    private val koki: KokiServices,
    private val mapper: ServiceMapper,
) {
    fun create(form: ServiceForm): String {
        return koki.create(
            CreateServiceRequest(
                name = form.name,
                title = form.title,
                description = form.description.ifEmpty { null },
                active = form.active,
                authorizationType = AuthorizationType.valueOf(form.authorizationType),
                apiKey = if (form.authorizationType == AuthorizationType.API_KEY.name) form.apiKey else null,
                username = if (form.authorizationType == AuthorizationType.BASIC.name) form.username else null,
                password = if (form.authorizationType == AuthorizationType.BASIC.name) form.password else null,
                baseUrl = form.baseUrl,
            )
        ).serviceId
    }

    fun update(id: String, form: ServiceForm) {
        koki.update(
            id,
            UpdateServiceRequest(
                name = form.name,
                title = form.title,
                description = form.description.ifEmpty { null },
                active = form.active,
                authorizationType = AuthorizationType.valueOf(form.authorizationType),
                apiKey = if (form.authorizationType == AuthorizationType.API_KEY.name) form.apiKey else null,
                username = if (form.authorizationType == AuthorizationType.BASIC.name) form.username else null,
                password = if (form.authorizationType == AuthorizationType.BASIC.name) form.password else null,
                baseUrl = form.baseUrl,
            )
        )
    }

    fun service(id: String): ServiceModel {
        val service = koki.service(id).service
        return mapper.toServiceModel(service)
    }

    fun delete(id: String) {
        koki.delete(id)
    }

    fun services(
        ids: List<String> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: ServiceSortBy? = null,
        ascending: Boolean = true,
    ): List<ServiceModel> {
        return koki.services(
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending
        ).services
            .map { service -> mapper.toServiceModel(service) }
    }
}
