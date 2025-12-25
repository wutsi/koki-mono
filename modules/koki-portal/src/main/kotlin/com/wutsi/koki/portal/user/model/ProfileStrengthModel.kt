package com.wutsi.koki.portal.user.model

data class ProfileStrengthModel(
    val value: Int = 0,
    val basicInfo: ProfileStrengthBreakdownModel = ProfileStrengthBreakdownModel(),
    val profilePicture: ProfileStrengthBreakdownModel = ProfileStrengthBreakdownModel(),
    val socialMedia: ProfileStrengthBreakdownModel = ProfileStrengthBreakdownModel(),
    val biography: ProfileStrengthBreakdownModel = ProfileStrengthBreakdownModel(),
    val address: ProfileStrengthBreakdownModel = ProfileStrengthBreakdownModel(),
    val category: ProfileStrengthBreakdownModel = ProfileStrengthBreakdownModel(),
) {
    val rating: String
        get() = if (value < 40) {
            "poor"
        } else if (value < 75) {
            "medium"
        } else if (value < 90) {
            "good"
        } else {
            "excellent"
        }
}
