package com.wutsi.koki

import com.wutsi.koki.AccountFixtures.accounts
import com.wutsi.koki.contact.dto.Contact
import com.wutsi.koki.contact.dto.ContactSummary
import com.wutsi.koki.contact.dto.Gender
import com.wutsi.koki.contact.dto.PreferredCommunicationMethod
import com.wutsi.koki.refdata.dto.Address

object ContactFixtures {
    // Contact
    val NEW_CONTACT_ID = 5555L
    val contacts = listOf(
        ContactSummary(
            id = 100,
            contactTypeId = TenantFixtures.types[0].id,
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
            contactTypeId = TenantFixtures.types[1].id,
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
            contactTypeId = TenantFixtures.types[0].id,
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
            contactTypeId = TenantFixtures.types[0].id,
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
        contactTypeId = TenantFixtures.types[0].id,
        accountId = accounts[0].id,
        firstName = "Ray",
        lastName = "Sponsible",
        email = "ray.sponsible@gmail.com",
        phone = "+15147551122",
        mobile = "+15147551133",
        modifiedById = UserFixtures.users[1].id,
        createdById = UserFixtures.users[1].id,
        profession = "Engineer",
        employer = "Google",
        salutation = "Mr.",
        gender = Gender.MALE,
        language = "fr",
        preferredCommunicationMethod = PreferredCommunicationMethod.WHATSAPP,
        address = Address(
            street = "340 Pascal",
            postalCode = "H7K 1C7",
            cityId = RefDataFixtures.locations[2].id,
            stateId = RefDataFixtures.locations[2].parentId,
            country = "CA",
        ),

        )
}
