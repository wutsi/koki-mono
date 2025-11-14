package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.common.model.MoneyModel
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import jdk.internal.joptsimple.internal.Messages.message
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

abstract class AbstractLeadDetailsController : AbstractLeadController() {
    override fun getModuleName(): String {
        return MODULE_NAME
    }

    protected fun findLead(id: Long): LeadModel {
        return LeadModel(
            id = id,
            firstName = "Thomas",
            lastName = "Nkono",
            displayName = "Thomas Nkono",
            status = LeadStatus.CONTACT_LATER,
            createdAt = DateUtils.addDays(Date(), -5),
            nextContactAt = DateUtils.addDays(Date(), -7),
            nextVisitAt = DateUtils.addDays(Date(), -10),
            nextContactAtText = "1 Aout 2025",
            nextVisitAtText = "10 Sept 2025",
            visitRequestedAtText = "10 Sept 2025",
            createdAtText = "30 Sept 2025",
            email = null,
            phoneNumberFormatted = "(514) 758 0000",
            whatsappUrl = "https://wa.me/15147580000",
            message = "Bonjour je suis interesse par la propriete.\nMerci",
            listing = ListingModel(
                id = 111,
                heroImageUrl = "https://picsum.photos/800/600",
                title = "Bel appartement a Bastos\nMerci",
                address = AddressModel(
                    street = "340 Pascal",
                    neighbourhood = LocationModel(name = "Bastos"),
                    city = LocationModel(name = "Yaounde"),
                    country = "CM",
                    countryName = "Cameroun",
                ),
                status = ListingStatus.ACTIVE,
                price = MoneyModel(displayText = "250,000,000 FCFA"),
                listingType = ListingType.SALE,
                propertyType = PropertyType.APARTMENT,
                bedrooms = 3,
                bathrooms = 3,
                lotArea = 100,
            ),
        )
    }
}
