package com.wutsi.koki.portal.client.security

import com.wutsi.koki.portal.client.tenant.service.CurrentTenantHolder
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

class RequiresModuleInterceptor(private val tenantHolder: CurrentTenantHolder) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val module = getModule(handler)
        if (module != null) {
            val tenant = tenantHolder.get()
            if (tenant?.hasModule(module) == true) {
                return true
            } else {
                response.sendRedirect("/error/access-denied")
                return false
            }
        }
        return true
    }

    private fun getModule(handler: Any): String? {
        if (handler is HandlerMethod) {
            val annotations = handler.beanType.getAnnotationsByType(RequiresModule::class.java)
            return annotations.firstOrNull()?.name
        }
        return null
    }
}
