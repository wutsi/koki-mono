package com.wutsi.koki.portal.pub.user.service

import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.portal.pub.user.mapper.UserMapper
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.portal.user.model.UserForm
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import org.springframework.stereotype.Service

@Service
class UserService(
    private val koki: KokiUsers,
    private val mapper: UserMapper,
    private val locationService: LocationService,
) {
    fun get(id: Long, fullGraph: Boolean = true): UserModel {
        val user = koki.user(id).user
        val city = if (user.cityId == null || !fullGraph) {
            null
        } else {
            locationService.get(user.cityId ?: -1)
        }
        return mapper.toUserModel(user, city)
    }

    fun search(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        permissions: List<String> = emptyList(),
        status: UserStatus? = null,
        username: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<UserModel> {
        val users = koki.users(
            keyword = keyword,
            ids = ids,
            roleIds = roleIds,
            permissions = permissions,
            status = status,
            username = username,
            limit = limit,
            offset = offset
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

    fun create(form: UserForm): Long {
        return koki.create(
            CreateUserRequest(
                displayName = form.displayName,
                username = form.username,
                email = form.email,
                roleIds = form.roleIds,
                language = form.language,
                password = form.password,
            )
        ).userId
    }

    fun update(id: Long, form: UserForm) {
        val user = koki.user(id).user
        koki.update(
            id,
            UpdateUserRequest(
                displayName = form.displayName,
                email = form.email,
                roleIds = form.roleIds,
                language = form.language,

                mobile = user.mobile,
                categoryId = user.categoryId,
                employer = user.employer,
                cityId = user.cityId,
                country = user.country,
            )
        )
    }
}
