package id.indosat.ml.util

import java.util.*


internal val suffixes:NavigableMap<Long,String> by lazy {
    TreeMap(mapOf(1_000L to "K",
        1_000_000L to "M",
        1_000_000L to "B"))
}

fun Int.formatAbbrv():String{
    val value = this.toLong()
    if (value == Long.MIN_VALUE) return String.format("${Long.MIN_VALUE + 1}")
    if (value < 0) return "-" + String.format("${-this}")
    if (value < 1000) return value.toString()

    val e = suffixes.floorEntry(value)
    val divideBy = e.key
    val suffix = e.value

    val truncated = value / (divideBy!! / 10) //the number part of the output times 10
    val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
    //return if (hasDecimal) truncated / 10.0 + suffix else truncated / 10 + suffix
    return if (hasDecimal) "${truncated / 10.0}$suffix" else "${truncated / 10}$suffix"
}