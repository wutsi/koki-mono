package com.wutsi.koki.common.dto

enum class ObjectType {
    UNKNOWN,
    ACCOUNT,
    CONTACT,
    FILE,
    NOTE,

    @Deprecated("Module retired")
    EMAIL,

    PRODUCT,

    @Deprecated("Module retired")
    EMPLOYEE,

    @Deprecated("Module retired")
    TAX,

    INVOICE,
    PAYMENT,

    @Deprecated("Module retired")
    FORM,

    @Deprecated("Module retired")
    ROOM,

    @Deprecated("Module retired")
    ROOM_UNIT,

    MESSAGE,
    LISTING,
    OFFER,
}
