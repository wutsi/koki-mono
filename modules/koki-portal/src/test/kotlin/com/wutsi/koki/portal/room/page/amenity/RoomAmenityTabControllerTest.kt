package com.wutsi.koki.portal.room.page.amenity

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.RefDataFixtures
import com.wutsi.koki.RoomFixtures.room
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.room.dto.AddAmenityRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomAmenityTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/room-amenities/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")

        room.amenityIds.forEach { id ->
            assertElementHasNotAttribute(".amenity input", "readonly") // Amenity not disabled
            assertElementHasAttribute("#amenity-$id input", "checked") // Amenity checked
        }
        RefDataFixtures.amenities
            .filter { amenity -> !room.amenityIds.contains(amenity.id) }
            .forEach { amenity ->
                assertElementHasNotAttribute(".amenity input", "readonly") // Amenity not disabled
                assertElementHasNotAttribute("#amenity-${amenity.id} input", "checked") // Amenity not checked
            }
    }

    @Test
    fun `read-only`() {
        navigateTo("/room-amenities/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true&read-only=true")

        room.amenityIds.forEach { id ->
            assertElementHasAttribute(".amenity input", "disabled") // Amenity not disabled
            assertElementHasAttribute("#amenity-$id input", "checked") // Amenity checked
        }
        RefDataFixtures.amenities
            .filter { amenity -> !room.amenityIds.contains(amenity.id) }
            .forEach { amenity ->
                assertElementHasAttribute(".amenity input", "disabled") // Amenity not disabled
                assertElementHasNotAttribute("#amenity-${amenity.id} input", "checked") // Amenity not checked
            }
    }

    @Test
    fun `show - without permission room-amenity-manage`() {
        setUpUserWithoutPermissions(listOf("room-amenity:manage"))

        navigateTo("/room-amenities/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true&read-only=true")

        room.amenityIds.forEach { id ->
            assertElementHasAttribute(".amenity input", "disabled") // Amenity not disabled
            assertElementHasAttribute("#amenity-$id input", "checked") // Amenity checked
        }
        RefDataFixtures.amenities
            .filter { amenity -> !room.amenityIds.contains(amenity.id) }
            .forEach { amenity ->
                assertElementHasAttribute(".amenity input", "disabled") // Amenity not disabled
                assertElementHasNotAttribute("#amenity-${amenity.id} input", "checked") // Amenity not checked
            }
    }

    @Test
    fun `list - without permission room-amenity`() {
        setUpUserWithoutPermissions(listOf("room-amenity"))

        navigateTo("/room-amenities/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true&read-only=true")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `disable amenity`() {
        navigateTo("/room-amenities/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")

        val amenityId = room.amenityIds[0]
        click("#amenity-$amenityId input")
        verify(rest)
            .delete("$sdkBaseUrl/v1/rooms/${room.id}/amenities/$amenityId")
    }

    @Test
    fun `enable amenity`() {
        navigateTo("/room-amenities/tab?owner-id=${room.id}&owner-type=ROOM&test-mode=true")

        val amenityId = RefDataFixtures.amenities
            .filter { amenity -> !room.amenityIds.contains(amenity.id) }
            .first().id

        click("#amenity-$amenityId input")
        val req = argumentCaptor<AddAmenityRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/rooms/${room.id}/amenities"),
            req.capture(),
            eq(Any::class.java)
        )
        assertEquals(listOf(amenityId), req.firstValue.amenityIds)
    }
}
