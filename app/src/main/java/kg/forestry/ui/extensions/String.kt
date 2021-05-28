package kg.forestry.ui.extensions

import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(): Date {
    return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(this)
}
