package com.wutsi.koki.listing.dto

enum class PropertyType(val category: PropertyCategory) {
    UNKNOWN(PropertyCategory.UNKNOWN),
    STUDIO(PropertyCategory.RESIDENTIAL),
    APARTMENT(PropertyCategory.RESIDENTIAL),
    VILLA(PropertyCategory.RESIDENTIAL),
    DUPLEX(PropertyCategory.RESIDENTIAL),
    HOUSE(PropertyCategory.RESIDENTIAL),
    ROOM(PropertyCategory.RESIDENTIAL),
    BUILDING(PropertyCategory.COMMERCIAL),
    LAND(PropertyCategory.LAND),
    COMMERCIAL(PropertyCategory.COMMERCIAL),
    INDUSTRIAL(PropertyCategory.COMMERCIAL),
    OFFICE(PropertyCategory.COMMERCIAL),
}
