package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.tenant.dto.ProfileStrength
import com.wutsi.koki.tenant.dto.ProfileStrengthBreakdown
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ProfileStrengthCalculator {
    companion object {
        private const val MAX_BASIC_INFO_SCORE = 20
        private const val MAX_PROFILE_PICTURE_SCORE = 20
        private const val MAX_SOCIAL_MEDIA_SCORE = 20
        private const val MAX_BIOGRAPHY_SCORE = 20
        private const val MAX_ADDRESS_SCORE = 10
        private const val MAX_CATEGORY_SCORE = 10
        private const val MAX_TOTAL_SCORE = 100

        private const val MAX_BIOGRAPHY_LENGTH = 1000
        private const val MAX_SOCIAL_MEDIA_LINKS = 5
        private const val POINTS_PER_SOCIAL_MEDIA_LINK = 4
    }

    fun calculate(user: UserEntity): ProfileStrength {
        val basicInfo = calculateBasicInfo(user)
        val profilePicture = calculateProfilePicture(user)
        val socialMedia = calculateSocialMedia(user)
        val biography = calculateBiography(user)
        val address = calculateAddress(user)
        val category = calculateCategory(user)

        val totalScore = basicInfo.value +
            profilePicture.value +
            socialMedia.value +
            biography.value +
            address.value +
            category.value

        return ProfileStrength(
            value = totalScore,
            basicInfo = basicInfo,
            profilePicture = profilePicture,
            socialMedia = socialMedia,
            biography = biography,
            address = address,
            category = category,
        )
    }

    private fun calculateBasicInfo(user: UserEntity): ProfileStrengthBreakdown {
        val hasDisplayName = !user.displayName.isNullOrEmpty()
        val hasEmail = !user.email.isNullOrEmpty()
        val hasMobile = !user.mobile.isNullOrEmpty()

        val value = if (hasDisplayName && hasEmail && hasMobile) {
            MAX_BASIC_INFO_SCORE
        } else {
            0
        }

        return ProfileStrengthBreakdown(
            value = value,
            percentage = calculatePercentage(value, MAX_BASIC_INFO_SCORE),
        )
    }

    private fun calculateProfilePicture(user: UserEntity): ProfileStrengthBreakdown {
        val value = if (!user.photoUrl.isNullOrEmpty()) {
            MAX_PROFILE_PICTURE_SCORE
        } else {
            0
        }

        return ProfileStrengthBreakdown(
            value = value,
            percentage = calculatePercentage(value, MAX_PROFILE_PICTURE_SCORE),
        )
    }

    private fun calculateSocialMedia(user: UserEntity): ProfileStrengthBreakdown {
        var linkCount = 0

        if (!user.facebookUrl.isNullOrEmpty()) linkCount++
        if (!user.instagramUrl.isNullOrEmpty()) linkCount++
        if (!user.twitterUrl.isNullOrEmpty()) linkCount++
        if (!user.tiktokUrl.isNullOrEmpty()) linkCount++
        if (!user.youtubeUrl.isNullOrEmpty()) linkCount++

        val effectiveLinks = min(linkCount, MAX_SOCIAL_MEDIA_LINKS)
        val value = effectiveLinks * POINTS_PER_SOCIAL_MEDIA_LINK

        return ProfileStrengthBreakdown(
            value = value,
            percentage = calculatePercentage(value, MAX_SOCIAL_MEDIA_SCORE),
        )
    }

    private fun calculateBiography(user: UserEntity): ProfileStrengthBreakdown {
        val biographyLength = user.biography?.length ?: 0
        val value = min(
            MAX_BIOGRAPHY_SCORE,
            (biographyLength * MAX_BIOGRAPHY_SCORE) / MAX_BIOGRAPHY_LENGTH,
        )

        return ProfileStrengthBreakdown(
            value = value,
            percentage = calculatePercentage(value, MAX_BIOGRAPHY_SCORE),
        )
    }

    private fun calculateAddress(user: UserEntity): ProfileStrengthBreakdown {
        val value = if (user.cityId != null) {
            MAX_ADDRESS_SCORE
        } else {
            0
        }

        return ProfileStrengthBreakdown(
            value = value,
            percentage = calculatePercentage(value, MAX_ADDRESS_SCORE),
        )
    }

    private fun calculateCategory(user: UserEntity): ProfileStrengthBreakdown {
        val value = if (user.categoryId != null) {
            MAX_CATEGORY_SCORE
        } else {
            0
        }

        return ProfileStrengthBreakdown(
            value = value,
            percentage = calculatePercentage(value, MAX_CATEGORY_SCORE),
        )
    }

    private fun calculatePercentage(value: Int, maxScore: Int): Int {
        return if (maxScore > 0) {
            (value * 100) / maxScore
        } else {
            0
        }
    }
}
