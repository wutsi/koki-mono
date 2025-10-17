package com.wutsi.koki.common.dto

enum class ObjectType {
    UNKNOWN,
    ACCOUNT,
    CONTACT,
    FILE,
    NOTE,

    @Deprecated("Module retired")
    EMAIL,

    @Deprecated("Module retired")
    PRODUCT,

    @Deprecated("Module retired")
    EMPLOYEE,

    @Deprecated("Module retired")
    TAX,

    @Deprecated("Module retired")
    INVOICE,

    @Deprecated("Module retired")
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
    AGENT,
}
