package kg.forestry.localstorage.db

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kg.core.utils.Biomass
import kg.core.utils.Distance
import kg.core.utils.Location
import kg.core.utils.Side

class Converter {

    @TypeConverter
    fun fromDistanceToJson(distance: Distance?): String {
        val gson = GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
        return gson.toJson(distance)
    }

    @TypeConverter
    fun fromJsonToDistance(json: String?): Distance? {
        val gson = GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
        return gson.fromJson<Distance>(json, object : TypeToken<Distance>() {}.type)
    }
    @TypeConverter
    fun fromSideToJson(side: Side?): String {
        val gson = GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
        return gson.toJson(side)
    }

    @TypeConverter
    fun fromJsonToSide(json: String?): Side? {
        val gson = GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
        return gson.fromJson<Side>(json, object : TypeToken<Side>() {}.type)
    }
    @TypeConverter
    fun fromLocationToJson(location: Location?): String {
        val gson = GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
        return gson.toJson(location)
    }

    @TypeConverter
    fun fromJsonToLocation(json: String?): Location? {
        val gson = GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
        return gson.fromJson<Location>(json, object : TypeToken<Location>() {}.type)
    }

    @TypeConverter
    fun fromBiomassToJson(biomass: Biomass?): String {
        val gson = GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
        return gson.toJson(biomass)
    }

    @TypeConverter
    fun fromJsonToBiomass(json: String?): Biomass? {
        val gson = GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
        return gson.fromJson<Biomass>(json, object : TypeToken<Biomass>() {}.type)
    }
}