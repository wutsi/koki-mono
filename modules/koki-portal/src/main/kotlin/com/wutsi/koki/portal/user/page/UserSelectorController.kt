package com.wutsi.koki.portal.user.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.tenant.dto.UserType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserSelectorController(
    private val service: UserService,
) : AbstractPageController() {
    @GetMapping("/users/selector/search")
    fun new(
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "role-id") roleId: List<Long> = emptyList(),
        @RequestParam(required = false, name = "permission") permissions: List<String> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): List<UserModel> {
        return service.users(
            keyword = keyword,
            roleIds = roleId,
            permissions = permissions,
            type = UserType.EMPLOYEE,
            limit = limit,
            offset = offset
        )
    }
}
