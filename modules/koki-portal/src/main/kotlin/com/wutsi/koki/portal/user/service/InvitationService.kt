package com.wutsi.koki.portal.user.service

import com.wutsi.koki.portal.user.mapper.InvitationMapper
import com.wutsi.koki.portal.user.model.InvitationForm
import com.wutsi.koki.portal.user.model.InvitationModel
import com.wutsi.koki.sdk.KokiInvitations
import com.wutsi.koki.tenant.dto.CreateInvitationRequest
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationType
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Collections.emptyList

@Service
class InvitationService(
    private val koki: KokiInvitations,
    private val mapper: InvitationMapper
) {
    fun get(id: String): InvitationModel {
        val invitation = koki.get(id).invitation
        return mapper.toInvitationModel(invitation)
    }

    fun delete(id: String) {
        koki.delete(id)
    }

    fun search(
        ids: List<String> = emptyList(),
        statuses: List<InvitationStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<InvitationModel> {
        val invitations = koki.search(ids, statuses, limit, offset).invitations
        return invitations.map { invitation -> mapper.toInvitationModel(invitation) }
    }

    fun create(form: InvitationForm): String? {
        return koki.create(
            CreateInvitationRequest(
                displayName = form.displayName,
                email = form.email,
                type = form.type ?: InvitationType.AGENT,
                language = LocaleContextHolder.getLocale().language
            )
        ).invitationId
    }
}
