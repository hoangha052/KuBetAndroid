package com.ku.kuvn.utils.date

import java.text.*
import java.util.*

class ISO8601DateFormat : DateFormat() {

    companion object {
        private const val serialVersionUID = 1L
        private val CALENDAR: Calendar = GregorianCalendar()
        private val NUMBER_FORMAT: NumberFormat = DecimalFormat()
    }

    init {
        numberFormat = NUMBER_FORMAT
        calendar = CALENDAR
    }

    override fun format(date: Date, toAppendTo: StringBuffer, fieldPosition: FieldPosition): StringBuffer {
        toAppendTo.append(ISO8601Utils.format(date))
        return toAppendTo
    }

    override fun parse(source: String, pos: ParsePosition): Date? {
        pos.index = source.length
        return ISO8601Utils.parse(source)
    }

    override fun clone() = this
}