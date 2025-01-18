package com.wutsi.koki.module.server.domain

import com.wutsi.koki.common.dto.ObjectType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_MODULE")
data class ModuleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val homeUrl: String? = null,
    val tabUrl: String? = null,
    val settingsUrl: String? = null,
    val objectType: ObjectType = ObjectType.UNKNOWN,
)
