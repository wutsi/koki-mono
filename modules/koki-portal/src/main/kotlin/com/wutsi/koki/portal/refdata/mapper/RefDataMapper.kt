package com.wutsi.koki.portal.refdata.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.model.JuridictionModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.model.SalesTaxModel
import com.wutsi.koki.portal.refdata.model.UnitModel
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Amenity
import com.wutsi.koki.refdata.dto.Category
import com.wutsi.koki.refdata.dto.Juridiction
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.SalesTax
import com.wutsi.koki.refdata.dto.Unit
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class RefDataMapper : TenantAwareMapper() {
    fun toUnitModel(entity: Unit): UnitModel {
        return UnitModel(
            id = entity.id,
            name = entity.name,
            abbreviation = entity.abbreviation,
        )
    }

    fun toLocationModel(entity: Location): LocationModel {
        return LocationModel(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            type = entity.type,
            country = entity.country,
            longitude = entity.longitude,
            latitude = entity.latitude,
        )
    }

    fun toCategoryModel(entity: Category): CategoryModel {
        val language = LocaleContextHolder.getLocale().language
        return CategoryModel(
            id = entity.id,
            parentId = entity.parentId,
            type = entity.type,
            level = entity.level,
            active = entity.active,
            name = when (language) {
                "fr" -> entity.nameFr ?: entity.name
                else -> entity.name
            },
            longName = when (language) {
                "fr" -> entity.longNameFr ?: entity.longName
                else -> entity.longName
            },
        )
    }

    fun toJuridictionModel(entity: Juridiction, locations: Map<Long, LocationModel>): JuridictionModel {
        val state = entity.stateId?.let { id -> locations[id] }
        val displayCountry = Locale("en", entity.country).displayCountry
        return JuridictionModel(
            id = entity.id,
            state = state,
            country = entity.country,
            name = if (state == null) displayCountry else "${state.name}, $displayCountry",
        )
    }

    fun toAddressModel(entity: Address, locations: Map<Long, LocationModel>): AddressModel {
        val city = entity.cityId?.let { id -> locations[id] }
        return AddressModel(
            street = entity.street?.ifEmpty { null },
            postalCode = entity.postalCode?.ifEmpty { null },
            city = city,
            state = city?.parentId?.let { id -> locations[id] },
            country = entity.country?.ifEmpty { null },
            countryName = entity.country?.let { country ->
                Locale(LocaleContextHolder.getLocale().language, country).getDisplayCountry()
            }
        )
    }

    fun toSalesTaxModel(entity: SalesTax): SalesTaxModel {
        return SalesTaxModel(
            id = entity.id,
            rate = entity.rate,
            priority = entity.priority,
            name = entity.name,
            juridictionId = entity.juridictionId,
            active = entity.active,
        )
    }

    fun toAmenityModel(entity: Amenity): AmenityModel {
        val language = LocaleContextHolder.getLocale().language
        return AmenityModel(
            id = entity.id,
            active = entity.active,
            categoryId = entity.categoryId,
            icon = entity.icon,
            name = when (language) {
                "fr" -> entity.nameFr ?: entity.name
                else -> entity.name
            },
        )
    }
}
