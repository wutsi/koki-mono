package com.wutsi.koki.portal.account.service

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.portal.account.mapper.AccountMapper
import com.wutsi.koki.portal.account.model.AccountTypeModel
import com.wutsi.koki.sdk.KokiAccounts
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AccountTypeService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper,
) {
    fun accountType(id: Long): AccountTypeModel {
        val accountType = koki.type(id).accountType
        return mapper.toAccountTypeModel(accountType)
    }

    fun accountTypes(
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<AccountTypeModel> {
        val accountTypes = koki.types(
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset
        ).accountTypes

        return accountTypes.map { accountType -> mapper.toAccountTypeModel(accountType) }
    }

    fun upload(file: MultipartFile): ImportResponse {
        return koki.uploadTypes(file)
    }
}
