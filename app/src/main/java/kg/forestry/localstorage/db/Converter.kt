package kg.forestry.localstorage.db

import androidx.room.TypeConverter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kg.core.utils.Biomass
import kg.core.utils.Location
import kg.core.utils.Side
import kg.forestry.localstorage.model.Plant
import kg.forestry.localstorage.model.SoilTexture


class Converter {

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

    @TypeConverter
    fun fromPlant(json: String) : Plant{
        return initMapper().readValue(json, Plant::class.java)
    }

    @TypeConverter
    fun toPlant(plant: List<Plant>) : String{
        return initMapper().writeValueAsString(plant)
    }

    @TypeConverter
    fun fromSoil(json: String) : SoilTexture?{
        return initMapper().readValue(json, SoilTexture::class.java)
    }

    @TypeConverter
    fun toSoil(soilTexture: SoilTexture) : String?{
        return initMapper().writeValueAsString(soilTexture)
    }

    fun initMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper
    }




//        private val gson = Gson()
//        @TypeConverter
//        fun stringToList(data: String?): List<MyListObject> {
//            if (data == null) {
//                return Collections.emptyList()
//            }
//            val listType: Type = object : TypeToken<List<MyListObject?>?>() {}.type
//            return gson.fromJson<List<MyListObject>>(data, listType)
//        }
//
//        @TypeConverter
//        fun ListToString(someObjects: List<MyListObject?>?): String {
//            return gson.toJson(someObjects)
//        }
}
