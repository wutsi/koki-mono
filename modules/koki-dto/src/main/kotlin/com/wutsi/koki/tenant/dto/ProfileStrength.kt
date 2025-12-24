package com.wutsi.koki.tenant.dto

data class ProfileStrength(
    val value: Int = 0,
    val basicInfo: ProfileStrengthBreakdown = ProfileStrengthBreakdown(),
    val profilePicture: ProfileStrengthBreakdown = ProfileStrengthBreakdown(),
    val socialMedia: ProfileStrengthBreakdown = ProfileStrengthBreakdown(),
    val biography: ProfileStrengthBreakdown = ProfileStrengthBreakdown(),
    val address: ProfileStrengthBreakdown = ProfileStrengthBreakdown(),
    val category: ProfileStrengthBreakdown = ProfileStrengthBreakdown(),
)
