package com.wutsi.koki.portal.client.security

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresModule(val name: String)
