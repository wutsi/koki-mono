package com.wutsi.koki.refdata.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_JURIDICTION")
data class JuridictionEntity(
    @Id
    val id: Long? = null,

    @Column(name = "state_fk")
    var stateId: Long? = null,

    var country: String = "",
)
