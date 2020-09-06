package com.ku.kuvn.utils.date

import java.text.ParseException
import java.util.*

/** Converting from String to Date **/
fun String.getDateWithServerTimeStamp(): Date? {
    return try {
        ISO8601DateFormat().parse(this)
    } catch (e: ParseException) {
        null
    }
}