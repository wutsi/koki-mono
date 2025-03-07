package com.wutsi.koki.platform.config

import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.koki.KokiStorageServiceBuilder
import com.wutsi.koki.platform.storage.local.LocalStorageServiceBuilder
import com.wutsi.koki.platform.storage.local.LocalStorageServlet
import com.wutsi.koki.platform.storage.s3.S3StorageServiceBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StorageConfiguration(
    @Value("\${koki.storage.type}") private val type: String,
    @Value("\${koki.storage.local.directory}") private val directory: String,
    @Value("\${koki.storage.local.base-url}") private val baseUrl: String,
    @Value("\${koki.storage.local.servlet-path}") private val servletPath: String,
    @Value("\${koki.storage.s3.bucket}") private val s3Bucket: String,
    @Value("\${koki.storage.s3.region}") private val s3Region: String,
    @Value("\${koki.storage.s3.access-key}") private val s3AccessKey: String,
    @Value("\${koki.storage.s3.secret-key}") private val s3SecretKey: String,
) {
    @Bean
    fun storageServiceBuilder(): StorageServiceBuilder {
        return StorageServiceBuilder(
            local = localStorageServiceBuilder(),
            s3 = s3StorageServiceBuilder(),
            koki = kokiStorageServiceBuilder()
        )
    }

    @Bean
    fun localStorageServiceBuilder(): LocalStorageServiceBuilder {
        return LocalStorageServiceBuilder(directory, "$baseUrl$servletPath")
    }

    @Bean
    fun s3StorageServiceBuilder(): S3StorageServiceBuilder {
        return S3StorageServiceBuilder()
    }

    @Bean
    fun kokiStorageServiceBuilder(): KokiStorageServiceBuilder {
        return KokiStorageServiceBuilder(
            type = type,
            directory = directory,
            baseUrl = "$baseUrl$servletPath",
            s3SecretKey = s3SecretKey,
            s3AccessKey = s3AccessKey,
            s3Region = s3Region,
            s3Bucket = s3Bucket,
        )
    }

    @Bean
    open fun storageServlet(): ServletRegistrationBean<*> {
        return ServletRegistrationBean(LocalStorageServlet(directory), "$servletPath/*")
    }
}
