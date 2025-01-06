package com.wutsi.koki

import com.wutsi.koki.AccountFixtures.accounts
import com.wutsi.koki.contact.dto.Contact
import com.wutsi.koki.contact.dto.ContactSummary
import com.wutsi.koki.contact.dto.ContactType
import com.wutsi.koki.contact.dto.ContactTypeSummary
import com.wutsi.koki.contact.dto.Gender

object ContactFixtures {
    // Contact Types
    val contactTypes = listOf(
        ContactTypeSummary(
            id = 100,
            name = "principal",
            title = "Principal",
        ),
        ContactTypeSummary(
            id = 101,
            name = "spouse",
            title = "Spouse",
        ),
        ContactTypeSummary(
            id = 102,
            name = "child",
        ),
    )

    val contactType = ContactType(
        id = 100,
        name = "principal",
        title = "Principal",
        description = "Contact for personal user",
    )

    // Contact
    val NEW_CONTACT_ID = 5555L
    val contacts = listOf(
        ContactSummary(
            id = 100,
            contactTypeId = contactTypes[0].id,
            accountId = accounts[0].id,
            firstName = "Ray",
            lastName = "Sponsible",
            email = "ray.sponsible@gmail.com",
            phone = "+9188880000",
            mobile = "+9188880011",
            modifiedById = UserFixtures.users[1].id,
            createdById = UserFixtures.users[1].id,
        ),
        ContactSummary(
            id = 101,
            contactTypeId = contactTypes[1].id,
            accountId = accounts[0].id,
            firstName = "Jane",
            lastName = "Doe",
            email = "jane.doe@gmail.com",
            phone = null,
            mobile = "+9188880022",
            modifiedById = UserFixtures.users[0].id,
            createdById = UserFixtures.users[0].id,
        ),
        ContactSummary(
            id = 102,
            contactTypeId = contactTypes[0].id,
            accountId = accounts[1].id,
            firstName = "John",
            lastName = "Smith",
            email = null,
            phone = null,
            mobile = null,
            modifiedById = UserFixtures.users[0].id,
            createdById = UserFixtures.users[1].id,
        ),
        ContactSummary(
            id = 103,
            contactTypeId = contactTypes[0].id,
            accountId = accounts[2].id,
            firstName = "Roger",
            lastName = "Milla",
            email = null,
            phone = null,
            mobile = null,
            modifiedById = UserFixtures.users[1].id,
            createdById = UserFixtures.users[1].id,
        ),
    )
    val contact = Contact(
        id = 100,
        contactTypeId = contactTypes[0].id,
        accountId = accounts[0].id,
        firstName = "Ray",
        lastName = "Sponsible",
        email = "ray.sponsible@gmail.com",
        phone = "+9188880000",
        mobile = "+9188880011",
        modifiedById = UserFixtures.users[1].id,
        createdById = UserFixtures.users[1].id,
        profession = "Engineer",
        employer = "Google",
        salutation = "Mr.",
        gender = Gender.MALE,
    )
}
