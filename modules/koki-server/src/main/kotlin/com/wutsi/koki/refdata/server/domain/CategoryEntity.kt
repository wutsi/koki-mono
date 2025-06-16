package com.wutsi.koki.refdata.server.domain

import com.wutsi.koki.refdata.dto.CategoryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_CATEGORY")
data class CategoryEntity(
    @Id val id: Long = 0,
    var type: CategoryType = CategoryType.UNKNOWN,

    @Column(name = "parent_fk") var parentId: Long? = null,

    var name: String = "",
    var longName: String = "",
    var nameFr: String? = null,
    var longNameFr: String? = null,
    var level: Int = 0,
    var active: Boolean = true,
)
