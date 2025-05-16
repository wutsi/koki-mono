package com.wutsi.koki.room.server.service

import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.room.server.domain.RoomEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomPublisherValidatorTest {
    private val room = RoomEntity()

    @Test
    fun success() {
        val rule1 = mock<PublishRule>()
        val rule2 = mock<PublishRule>()
        val rule3 = mock<PublishRule>()
        val validator = RoomPublisherValidator(listOf(rule1, rule2, rule3))

        validator.validate(room)

        verify(rule1).validate(room)
        verify(rule2).validate(room)
        verify(rule3).validate(room)
    }

    @Test
    fun failure() {
        val rule1 = mock<PublishRule>()
        val rule2 = mock<PublishRule>()
        val rule3 = mock<PublishRule>()

        doThrow(ValidationException("failed")).whenever(rule2).validate(room)

        val validator = RoomPublisherValidator(listOf(rule1, rule2, rule3))

        val ex = assertThrows<ValidationException> { validator.validate(room) }

        assertEquals("failed", ex.message)
        verify(rule1).validate(room)
        verify(rule2).validate(room)
        verify(rule3, never()).validate(room)
    }
}
