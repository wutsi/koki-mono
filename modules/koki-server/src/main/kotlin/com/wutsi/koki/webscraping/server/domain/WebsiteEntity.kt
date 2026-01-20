package com.wutsi.koki.webscraping.server.domain

import com.wutsi.koki.util.jpa.StringListConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_WEBSITE")
data class WebsiteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1,
    @Column(name = "user_fk") val userId: Long = -1,

    var baseUrl: String = "",
    val baseUrlHash: String = "",
    var listingUrlPrefix: String = "",

    @Convert(converter = StringListConverter::class)
    var homeUrls: List<String> = emptyList(),

    var contentSelector: String? = null,
    var imageSelector: String? = null,
    var active: Boolean = true,
    val createdAt: Date = Date(),
)
