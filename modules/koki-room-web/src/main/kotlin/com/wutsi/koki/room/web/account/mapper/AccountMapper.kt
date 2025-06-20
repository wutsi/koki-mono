package com.wutsi.koki.room.web.account.mapper

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.room.web.account.model.AccountModel
import com.wutsi.koki.room.web.common.mapper.TenantAwareMapper
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class AccountMapper : TenantAwareMapper() {
    fun toAccountModel(entity: AccountSummary): AccountModel {
        return AccountModel(
            id = entity.id,
            name = entity.name,
            phone = entity.phone,
            email = entity.email,
            mobile = entity.mobile,
        )
    }

    fun toAccountModel(entity: Account): AccountModel {
        createDateTimeFormat()
        return AccountModel(
            id = entity.id,
            name = entity.name,
            phone = entity.phone,
            email = entity.email,
            mobile = entity.mobile,
            description = entity.description,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
            website = entity.website,
        )
    }
}
