package com.wutsi.koki.portal.pub.user.service

import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.portal.pub.user.mapper.UserMapper
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.sdk.KokiUsers
import org.springframework.stereotype.Service

@Service
class UserService(
    private val koki: KokiUsers,
    private val mapper: UserMapper,
    private val locationService: LocationService,
) {
    fun get(id: Long): UserModel {
        val user = koki.user(id).user
        val city = user.cityId?.let { cityId -> locationService.get(cityId) }
        return mapper.toUserModel(user, city)
    }

    fun search(
        ids: List<Long> = emptyList(),
        limit: Int = 20,
    ): List<UserModel> {
        val users = koki.users(
            ids = ids,
            limit = limit,
        ).users

        val cityIds = users.mapNotNull { user -> user.cityId }.distinct()
        val cities = if (cityIds.isEmpty()) {
            emptyMap()
        } else {
            locationService.search(
                ids = cityIds,
                limit = cityIds.size
            ).associateBy { city -> city.id }
        }
        return users.map { user -> mapper.toUserModel(user, cities) }
    }
}
