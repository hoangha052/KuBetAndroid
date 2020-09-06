package com.ku.kuvn.utils.date

import java.util.*

object ISO8601Utils {

    fun format(date: Date?): String? {
        return format(date, tz = TimeZone.getTimeZone("GMT"))
    }

    private fun format(date: Date?, millis: Boolean = false, tz: TimeZone): String? {
        val calendar = GregorianCalendar(tz, Locale.US)
        calendar.time = date
        var capacity = "yyyy-MM-ddThh:mm:ss".length
        capacity += if (millis) ".sss".length else 0
        capacity += if (tz.rawOffset == 0) "Z".length else "+hh:mm".length
        val formatted = StringBuilder(capacity)
        padInt(formatted,
                calendar[Calendar.YEAR], "yyyy".length)
        formatted.append('-')
        padInt(formatted,
                calendar[Calendar.MONTH] + 1, "MM".length)
        formatted.append('-')
        padInt(formatted,
                calendar[Calendar.DATE], "dd".length)
        formatted.append('T')
        padInt(formatted,
                calendar[Calendar.HOUR_OF_DAY], "hh".length)
        formatted.append(':')
        padInt(formatted,
                calendar[Calendar.MINUTE], "mm".length)
        formatted.append(':')
        padInt(formatted,
                calendar[Calendar.SECOND], "ss".length)
        if (millis) {
            formatted.append('.')
            padInt(formatted,
                    calendar[Calendar.MILLISECOND], "sss".length)
        }
        val offset = tz.getOffset(calendar.timeInMillis)
        if (offset != 0) {
            val hours = Math.abs(offset / '\uea60'.toInt() / 60)
            val minutes = Math.abs(offset / '\uea60'.toInt() % 60)
            formatted.append((if (offset < 0) '-' else '+'))
            padInt(formatted, hours,
                    "hh".length)
            formatted.append(':')
            padInt(formatted, minutes,
                    "mm".length)
        } else {
            formatted.append('Z')
        }
        return formatted.toString()
    }

    fun parse(date: String): Date? {
        return try {
            val e: Byte = 0
            var var16 = e + 4
            val year = parseInt(date,
                    e.toInt(), var16)
            checkOffset(date, var16, '-')
            ++var16
            var var17 = var16
            var16 += 2
            val month = parseInt(date, var17,
                    var16)
            checkOffset(date, var16, '-')
            ++var16
            var17 = var16
            var16 += 2
            val day = parseInt(date, var17,
                    var16)
            checkOffset(date, var16, 'T')
            ++var16
            var17 = var16
            var16 += 2
            val hour = parseInt(date, var17,
                    var16)
            checkOffset(date, var16, ':')
            ++var16
            var17 = var16
            var16 += 2
            val minutes = parseInt(date,
                    var17, var16)
            checkOffset(date, var16, ':')
            ++var16
            var17 = var16
            var16 += 2
            val seconds = parseInt(date,
                    var17, var16)
            var milliseconds = 0
            if (date[var16].toInt() == 46) {
                checkOffset(date, var16, '.')
                ++var16
                var17 = var16
                var16 += 3
                milliseconds = parseInt(date,
                        var17, var16)
            }
            var16 = date.lastIndex

            val timezoneIndicator = date[var16]
            val timezoneId: String
            timezoneId = if (timezoneIndicator.toInt() != 43 && timezoneIndicator.toInt() != 45) {
                if (timezoneIndicator.toInt() != 90) {
                    throw IndexOutOfBoundsException(
                            "Invalid time zone indicator $timezoneIndicator")
                }
                "GMT"
            } else {
                "GMT" + date.substring(var16)
            }
            val timezone = TimeZone.getTimeZone(timezoneId)
            if (timezone.id != timezoneId) {
                throw IndexOutOfBoundsException()
            } else {
                val calendar = GregorianCalendar(timezone)
                calendar.isLenient = false
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month - 1
                calendar[Calendar.DATE] = day
                calendar[Calendar.HOUR_OF_DAY] = hour
                calendar[Calendar.MINUTE] = minutes
                calendar[Calendar.SECOND] = seconds
                calendar[Calendar.MILLISECOND] = milliseconds
                calendar.time
            }
        } catch (var13: IndexOutOfBoundsException) {
            throw IllegalArgumentException("Failed to parse date $date", var13)
        } catch (var14: NumberFormatException) {
            throw IllegalArgumentException("Failed to parse date $date", var14)
        } catch (var15: IllegalArgumentException) {
            throw IllegalArgumentException("Failed to parse date $date", var15)
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    private fun checkOffset(value: String, offset: Int, expected: Char) {
        val found = value[offset]
        if (found != expected) {
            throw IndexOutOfBoundsException("Expected \'$expected\' character but found \'$found\'")
        }
    }

    @Throws(NumberFormatException::class)
    private fun parseInt(value: String, beginIndex: Int, endIndex: Int): Int {
        return if (beginIndex >= 0 && endIndex <= value.length && beginIndex <= endIndex) {
            var i = beginIndex
            var result = 0
            var digit: Int
            if (beginIndex < endIndex) {
                i = beginIndex + 1
                digit = Character.digit(value[beginIndex], 10)
                if (digit < 0) {
                    throw NumberFormatException("Invalid number: $value")
                }
                result = -digit
            }
            while (i < endIndex) {
                digit = Character.digit(value[i++], 10)
                if (digit < 0) {
                    throw NumberFormatException("Invalid number: $value")
                }
                result *= 10
                result -= digit
            }
            -result
        } else {
            throw NumberFormatException(value)
        }
    }

    private fun padInt(buffer: StringBuilder, value: Int, length: Int) {
        val strValue = value.toString()
        for (i in length - strValue.length downTo 1) {
            buffer.append('0')
        }
        buffer.append(strValue)
    }
}