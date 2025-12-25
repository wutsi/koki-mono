package com.wutsi.koki.portal.user.mapper
import com.wutsi.koki.portal.user.model.ProfileStrengthBreakdownModel
import com.wutsi.koki.portal.user.model.ProfileStrengthModel
import com.wutsi.koki.tenant.dto.ProfileStrength
import com.wutsi.koki.tenant.dto.ProfileStrengthBreakdown
import org.springframework.stereotype.Service
@Service
class ProfileStrengthMapper {
    fun toProfileStrengthModel(entity: ProfileStrength?): ProfileStrengthModel? {
        return entity?.let {
            ProfileStrengthModel(
                value = it.value,
                basicInfo = toProfileStrengthBreakdownModel(it.basicInfo),
                profilePicture = toProfileStrengthBreakdownModel(it.profilePicture),
                socialMedia = toProfileStrengthBreakdownModel(it.socialMedia),
                biography = toProfileStrengthBreakdownModel(it.biography),
                address = toProfileStrengthBreakdownModel(it.address),
                category = toProfileStrengthBreakdownModel(it.category),
            )
        }
    }
    private fun toProfileStrengthBreakdownModel(entity: ProfileStrengthBreakdown): ProfileStrengthBreakdownModel {
        return ProfileStrengthBreakdownModel(
            value = entity.value,
            percentage = entity.percentage,
        )
    }
}
