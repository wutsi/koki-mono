package com.wutsi.koki.refdata.server.mapper

import com.wutsi.koki.refdata.dto.Juridiction
import com.wutsi.koki.refdata.server.domain.JuridictionEntity
import org.springframework.stereotype.Service

@Service
class JuridictionMapper {
    fun toJuridiction(entity: JuridictionEntity): Juridiction {
        return Juridiction(
            id = entity.id!!,
            country = entity.country,
            stateId = entity.stateId,
        )
    }
}
