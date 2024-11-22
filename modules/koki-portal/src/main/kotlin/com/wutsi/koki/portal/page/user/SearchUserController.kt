package com.wutsi.koki.portal.page.user

import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchUserController(
    private val service: UserService,
) : AbstractPageController() {
    @GetMapping("/users/search")
    fun new(
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "role-id") roleId: List<Long> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): List<UserModel> {
        return service.users(
            keyword = keyword,
            roleIds = roleId,
            limit = limit,
            offset = offset
        )
    }
}
