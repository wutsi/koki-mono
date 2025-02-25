package com.wutsi.koki.portal.security

import com.wutsi.koki.portal.user.service.CurrentUserHolder
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

class

RequirePermissionInterceptor(private val currentUser: CurrentUserHolder) : HandlerInterceptor {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RequirePermissionInterceptor::class.java)
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val permissions = getPermissions(handler)
        if (permissions.isNotEmpty()) {
            val user = currentUser.get()
            val permissionNames = user?.permissionNames
            if (permissionNames == null || !permissionNames.containsAll(permissions)) {
                if (LOGGER.isDebugEnabled) {
                    LOGGER.debug("Expecting permissions $permissions, but User#${user?.id} has $permissionNames")
                }
                response.sendRedirect("/error/access-denied")
                return false
            }
        }
        return true
    }

    private fun getPermissions(handler: Any): List<String> {
        if (handler is HandlerMethod) {
            var annotations = handler.method.getAnnotationsByType(RequiresPermission::class.java)
            if (annotations.isEmpty()) {
                annotations = handler.beanType.getAnnotationsByType(RequiresPermission::class.java)
            }

            if (annotations.isNotEmpty()) {
                return annotations[0].permissions.toList()
            }
        }
        return emptyList()
    }
}
