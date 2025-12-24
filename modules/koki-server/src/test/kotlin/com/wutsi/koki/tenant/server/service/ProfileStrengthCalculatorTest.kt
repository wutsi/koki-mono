package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProfileStrengthCalculatorTest {
    private val calculator = ProfileStrengthCalculator()

    @Test
    fun `should return 0 for empty profile`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.value)
        assertEquals(0, result.basicInfo.value)
        assertEquals(0, result.basicInfo.percentage)
        assertEquals(0, result.profilePicture.value)
        assertEquals(0, result.profilePicture.percentage)
        assertEquals(0, result.socialMedia.value)
        assertEquals(0, result.socialMedia.percentage)
        assertEquals(0, result.biography.value)
        assertEquals(0, result.biography.percentage)
        assertEquals(0, result.address.value)
        assertEquals(0, result.address.percentage)
        assertEquals(0, result.category.value)
        assertEquals(0, result.category.percentage)
    }

    @Test
    fun `should return 100 for complete profile`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            displayName = "John Doe",
            email = "john@example.com",
            mobile = "+1234567890",
            photoUrl = "https://example.com/photo.jpg",
            facebookUrl = "https://facebook.com/johndoe",
            instagramUrl = "https://instagram.com/johndoe",
            twitterUrl = "https://twitter.com/johndoe",
            tiktokUrl = "https://tiktok.com/@johndoe",
            youtubeUrl = "https://youtube.com/johndoe",
            biography = "a".repeat(1000),
            cityId = 100L,
            categoryId = 200L,
        )

        val result = calculator.calculate(user)

        assertEquals(100, result.value)
        assertEquals(20, result.basicInfo.value)
        assertEquals(100, result.basicInfo.percentage)
        assertEquals(20, result.profilePicture.value)
        assertEquals(100, result.profilePicture.percentage)
        assertEquals(20, result.socialMedia.value)
        assertEquals(100, result.socialMedia.percentage)
        assertEquals(20, result.biography.value)
        assertEquals(100, result.biography.percentage)
        assertEquals(10, result.address.value)
        assertEquals(100, result.address.percentage)
        assertEquals(10, result.category.value)
        assertEquals(100, result.category.percentage)
    }

    @Test
    fun `should calculate basic info - all present`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            displayName = "John Doe",
            email = "john@example.com",
            mobile = "+1234567890",
        )

        val result = calculator.calculate(user)

        assertEquals(20, result.basicInfo.value)
        assertEquals(100, result.basicInfo.percentage)
        assertEquals(20, result.value)
    }

    @Test
    fun `should calculate basic info - missing displayName`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            displayName = null,
            email = "john@example.com",
            mobile = "+1234567890",
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.basicInfo.value)
        assertEquals(0, result.basicInfo.percentage)
    }

    @Test
    fun `should calculate basic info - missing email`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            displayName = "John Doe",
            email = null,
            mobile = "+1234567890",
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.basicInfo.value)
        assertEquals(0, result.basicInfo.percentage)
    }

    @Test
    fun `should calculate basic info - missing mobile`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            displayName = "John Doe",
            email = "john@example.com",
            mobile = null,
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.basicInfo.value)
        assertEquals(0, result.basicInfo.percentage)
    }

    @Test
    fun `should calculate basic info - empty strings treated as missing`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            displayName = "",
            email = "",
            mobile = "",
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.basicInfo.value)
        assertEquals(0, result.basicInfo.percentage)
    }

    @Test
    fun `should calculate profile picture - present`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            photoUrl = "https://example.com/photo.jpg",
        )

        val result = calculator.calculate(user)

        assertEquals(20, result.profilePicture.value)
        assertEquals(100, result.profilePicture.percentage)
        assertEquals(20, result.value)
    }

    @Test
    fun `should calculate profile picture - absent`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            photoUrl = null,
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.profilePicture.value)
        assertEquals(0, result.profilePicture.percentage)
    }

    @Test
    fun `should calculate profile picture - empty string`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            photoUrl = "",
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.profilePicture.value)
        assertEquals(0, result.profilePicture.percentage)
    }

    @Test
    fun `should calculate social media - 0 links`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.socialMedia.value)
        assertEquals(0, result.socialMedia.percentage)
    }

    @Test
    fun `should calculate social media - 1 link`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            facebookUrl = "https://facebook.com/johndoe",
        )

        val result = calculator.calculate(user)

        assertEquals(4, result.socialMedia.value)
        assertEquals(20, result.socialMedia.percentage)
    }

    @Test
    fun `should calculate social media - 3 links`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            facebookUrl = "https://facebook.com/johndoe",
            instagramUrl = "https://instagram.com/johndoe",
            twitterUrl = "https://twitter.com/johndoe",
        )

        val result = calculator.calculate(user)

        assertEquals(12, result.socialMedia.value)
        assertEquals(60, result.socialMedia.percentage)
    }

    @Test
    fun `should calculate social media - 5 links (max)`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            facebookUrl = "https://facebook.com/johndoe",
            instagramUrl = "https://instagram.com/johndoe",
            twitterUrl = "https://twitter.com/johndoe",
            tiktokUrl = "https://tiktok.com/@johndoe",
            youtubeUrl = "https://youtube.com/johndoe",
        )

        val result = calculator.calculate(user)

        assertEquals(20, result.socialMedia.value)
        assertEquals(100, result.socialMedia.percentage)
    }

    @Test
    fun `should calculate social media - more than 5 links capped at max`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            facebookUrl = "https://facebook.com/johndoe",
            instagramUrl = "https://instagram.com/johndoe",
            twitterUrl = "https://twitter.com/johndoe",
            tiktokUrl = "https://tiktok.com/@johndoe",
            youtubeUrl = "https://youtube.com/johndoe",
        )

        val result = calculator.calculate(user)

        // Should still be 20 even if we had more social media fields
        assertEquals(20, result.socialMedia.value)
        assertEquals(100, result.socialMedia.percentage)
    }

    @Test
    fun `should calculate social media - empty strings not counted`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            facebookUrl = "",
            instagramUrl = "https://instagram.com/johndoe",
            twitterUrl = "",
        )

        val result = calculator.calculate(user)

        assertEquals(4, result.socialMedia.value)
        assertEquals(20, result.socialMedia.percentage)
    }

    @Test
    fun `should calculate biography - 0 characters`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            biography = null,
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.biography.value)
        assertEquals(0, result.biography.percentage)
    }

    @Test
    fun `should calculate biography - 250 characters`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            biography = "a".repeat(250),
        )

        val result = calculator.calculate(user)

        assertEquals(5, result.biography.value)
        assertEquals(25, result.biography.percentage)
    }

    @Test
    fun `should calculate biography - 500 characters`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            biography = "a".repeat(500),
        )

        val result = calculator.calculate(user)

        assertEquals(10, result.biography.value)
        assertEquals(50, result.biography.percentage)
    }

    @Test
    fun `should calculate biography - 999 characters`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            biography = "a".repeat(999),
        )

        val result = calculator.calculate(user)

        assertEquals(19, result.biography.value)
        assertEquals(95, result.biography.percentage)
    }

    @Test
    fun `should calculate biography - 1000 characters (max)`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            biography = "a".repeat(1000),
        )

        val result = calculator.calculate(user)

        assertEquals(20, result.biography.value)
        assertEquals(100, result.biography.percentage)
    }

    @Test
    fun `should calculate biography - more than 1000 characters capped at max`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            biography = "a".repeat(1500),
        )

        val result = calculator.calculate(user)

        assertEquals(20, result.biography.value)
        assertEquals(100, result.biography.percentage)
    }

    @Test
    fun `should calculate biography - empty string`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            biography = "",
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.biography.value)
        assertEquals(0, result.biography.percentage)
    }

    @Test
    fun `should calculate address - present`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            cityId = 100L,
        )

        val result = calculator.calculate(user)

        assertEquals(10, result.address.value)
        assertEquals(100, result.address.percentage)
        assertEquals(10, result.value)
    }

    @Test
    fun `should calculate address - absent`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            cityId = null,
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.address.value)
        assertEquals(0, result.address.percentage)
    }

    @Test
    fun `should calculate category - present`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            categoryId = 200L,
        )

        val result = calculator.calculate(user)

        assertEquals(10, result.category.value)
        assertEquals(100, result.category.percentage)
        assertEquals(10, result.value)
    }

    @Test
    fun `should calculate category - absent`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            categoryId = null,
        )

        val result = calculator.calculate(user)

        assertEquals(0, result.category.value)
        assertEquals(0, result.category.percentage)
    }

    @Test
    fun `should calculate partial profile - 50 percent`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            displayName = "John Doe",
            email = "john@example.com",
            mobile = "+1234567890",
            photoUrl = "https://example.com/photo.jpg",
            cityId = 100L,
        )

        val result = calculator.calculate(user)

        assertEquals(50, result.value)
        assertEquals(20, result.basicInfo.value)
        assertEquals(20, result.profilePicture.value)
        assertEquals(0, result.socialMedia.value)
        assertEquals(0, result.biography.value)
        assertEquals(10, result.address.value)
        assertEquals(0, result.category.value)
    }

    @Test
    fun `should calculate realistic profile - 75 percent`() {
        val user = UserEntity(
            id = 1L,
            tenantId = 1L,
            username = "user1",
            status = UserStatus.ACTIVE,
            displayName = "John Doe",
            email = "john@example.com",
            mobile = "+1234567890",
            photoUrl = "https://example.com/photo.jpg",
            facebookUrl = "https://facebook.com/johndoe",
            instagramUrl = "https://instagram.com/johndoe",
            twitterUrl = "https://twitter.com/johndoe",
            biography = "a".repeat(500),
            cityId = 100L,
        )

        val result = calculator.calculate(user)

        assertEquals(72, result.value)
        assertEquals(20, result.basicInfo.value)
        assertEquals(20, result.profilePicture.value)
        assertEquals(12, result.socialMedia.value)
        assertEquals(10, result.biography.value)
        assertEquals(10, result.address.value)
        assertEquals(0, result.category.value)
    }
}
