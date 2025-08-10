package com.wutsi.koki.common.dto

enum class ObjectType {
    UNKNOWN,
    ACCOUNT,
    CONTACT,
    FILE,
    NOTE,
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

    ROOM,
    ROOM_UNIT,
    MESSAGE,
    LISTING,
}
