package com.wutsi.koki.webscraping.server.domain

import com.wutsi.koki.util.jpa.StringListConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_WEBPAGE")
data class WebpageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1,
    @Column(name = "listing_fk") var listingId: Long? = null,

    @ManyToOne
    @JoinColumn(name = "website_fk")
    val website: WebsiteEntity = WebsiteEntity(),

    var url: String = "",
    val urlHash: String = "",
    var content: String? = null,

    @Convert(converter = StringListConverter::class)
    var imageUrls: List<String> = emptyList(),

    var active: Boolean = true,
    val createdAt: Date = Date(),
    var updatedAt: Date = Date(),
)
