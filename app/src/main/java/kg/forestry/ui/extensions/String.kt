package kg.forestry.ui.extensions

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


fun String.toDate(): Date {
    return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(this)
}

fun ImageView.loadImage(url: String) {
    this.rotation = -360f
    Glide.with(this.context)
        .load(url)
        .into(this)
}

fun getExifOrientation(filepath: String?): Int {
    var degree = 0
    var exif: ExifInterface? = null
    try {
        exif = ExifInterface(filepath!!)
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
    if (exif != null) {
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
        if (orientation != -1) {
            // We only recognise a subset of orientation tag values.
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        }
    }
    return degree
}

fun rotatingImageView(angle: Int, bitmap: Bitmap): Bitmap? {
    // rotate the picture action
    val matrix = Matrix()
    matrix.postRotate(angle.toFloat())
    // Create a new picture
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}





