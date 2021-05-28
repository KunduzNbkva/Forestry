package kg.core.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Base64
import android.widget.ImageView
import kotlin.random.Random


object Helper {

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun getRandomLongId(): Long {
        return Random.nextInt(1000).toLong()
    }

     fun setExistImage(imageView: ImageView, base64String: String) {
        if (!base64String.isEmpty()) {
            val bytes: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
        }
    }

    fun fromStringToLocation(strLocation:String): Location {
        val location = Location(0.0,0.0)
        location.lat = strLocation.split(" ")[0].toDouble()
        location.lon = strLocation.split(" ")[1].toDouble()
        return location
    }

    fun getLocationFormattedString(location: Location):String{
        val formattedLat =
            location.lat.toString().substring(0, (location.lat.toString().length - 9))
        val formattedLon =
            location.lon.toString().substring(0, (location.lon.toString().length - 9))
        return "$formattedLat $formattedLon"
    }


     fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return if (activeNetwork?.isConnected != null) {
            activeNetwork.isConnected
        } else {
            //Toast.makeText(context,"Проверьте интернет соединение",Toast.LENGTH_SHORT).show()
            false
        }
    }
}