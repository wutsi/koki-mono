package com.wutsi.koki.listing.server.service.email

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.context.support.ResourceBundleMessageSource

abstract class AbstractListingMailetTest {
    protected val listingService = mock<ListingService>()
    protected val userService = mock<UserService>()
    protected val locationService = mock<LocationService>()
    protected val tenantService = mock<TenantService>()
    protected val templateResolver = EmailTemplateResolver(MustacheTemplatingEngine(DefaultMustacheFactory()))
    protected val fileService = mock<FileService>()
    protected val sender = mock<Sender>()
    protected val messages = ResourceBundleMessageSource().apply {
        setBasename("messages")
        setDefaultEncoding("UTF-8")
    }
    protected val logger: KVLogger = DefaultKVLogger()

    protected val city = LocationEntity(id = 111, name = "Yaounde", country = "CM")
    protected val neighbourhood = LocationEntity(id = 222, name = "Bastos", country = "CM")
    protected val tenant = TenantEntity(
        id = 1L,
        name = "Test",
        clientPortalUrl = "https://realtor.com",
        logoUrl = "https://picsum.photos/200/200",
        monetaryFormat = "C\$ #,###,##0",
        numberFormat = "#,###,##0.00",
        portalUrl = "https://realtor.com",
        dateFormat = "YYYY-MM-DD",
        dateTimeFormat = "YYYY-MM-DD HH:mm:ss",
    )
    protected val sellerAgent = UserEntity(
        id = 333L,
        tenantId = 1L,
        displayName = "John Smith",
        email = "john.smith@gmail.com",
        employer = "REIMAX LAVAL",
        mobile = "+15147580011",
        photoUrl = "https://picsum.photos/200/200"
    )
    protected val listing = ListingEntity(
        id = 111L,
        listingNumber = 111L,
        tenantId = tenant.id!!,
        status = ListingStatus.ACTIVE,
        listingType = ListingType.RENTAL,
        sellerContactId = 111L,
        sellerAgentUserId = sellerAgent.id,
        buyerAgentUserId = 777L,
        street = "340 Pascal",
        cityId = city.id,
        neighbourhoodId = neighbourhood.id,
        price = 150000,
        salePrice = 175000,
        description = "Beautiful house close to downtown.",
        heroImageId = 333L,
        bedrooms = 3,
        bathrooms = 2,
        lotArea = 750,
        buyerAgentCommission = 3.0,
        sellerAgentCommission = 6.5,
    )
    protected val image = FileEntity(
        url = "https://picsum.photos/1200/800"
    )

    @BeforeEach
    open fun setUp() {
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(tenant).whenever(tenantService).get(any())
        doReturn(sellerAgent).whenever(userService).get(any(), any())
        doReturn(city).whenever(locationService).get(city.id!!)
        doReturn(neighbourhood).whenever(locationService).get(neighbourhood.id!!)
        doReturn(image).whenever(fileService).get(any(), any())
        doReturn(true).whenever(sender).send(any<Party>(), any(), any(), any(), any())
        doReturn(true).whenever(sender).send(any<UserEntity>(), any(), any(), any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }
}
