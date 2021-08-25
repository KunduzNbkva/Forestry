package kg.forestry.localstorage.model

import android.content.Context
import android.os.Parcelable
import androidx.room.*
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.annotations.SerializedName
import kg.core.utils.LocaleManager
import kg.core.utils.Location
import kg.core.utils.Side
import org.parceler.Parcel
import java.io.Serializable

@Entity(tableName = "plants")
@Parcel
data class Plant(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    val plotName: String = "",
    val pastureName: String = "",
    var harvestLocation: String = "",
    val pointDescription: String = "",
    val plotDescription: String = "",
    val plants: String = "",
    val trees: String = "",
    @JsonIgnore
    val soilTexture: SoilTexture? = null,
    val soilColor: String = "",
    val erosionDegree: String = "",
    val cattlePasture: String = "",
    val typePasture: String = "",
    var bareGround: Long = 0,
    var shrubs: Long = 0,
    var eatenPlant: Long = 0,
    var nonEatenPlant: Long = 0,
    var plantBase: Long = 0,
    var stone: Long = 0,
    var litterFall: Long = 0,
    val date: String = "",
    var plantPhoto: String = "",
    var eastSide: Side = Side(),
    var westSide: Side = Side(),
    var northSide: Side = Side(),
    var southSide: Side = Side(),
    var isInServer:Boolean = false,
    var plantLocation: Location = Location(0.0,0.0),

    var region_ru: String = "",
    var region_ky: String = "",
    var region_en: String = "",

    var village_ru: String = "",
    var village_kg: String = "",
    var village_en: String = "",

    var district_ru: String = "",
    var district_en: String = "",
    var district_ky: String = "",
    var isDraft: Boolean = false


) : Serializable
@Entity(tableName = "plant_type")
@Parcel
data class PlantType(
    @PrimaryKey
    var id: Long = 0,
    val imgLink: String = "",
    val imgBase64: String = "",
    val name_en: String = "",
    val name_ky: String = "",
    var name_ru: String = "",
    var type: String = "",
    var userId: String = "",
    var eatable: Boolean = false
) : Serializable

@Entity(tableName = "tree_type")
@Parcel
data class TreeType(
    @PrimaryKey
    var id: Long = 0,
    val imgLink: String = "",
    val name_en: String = "",
    val name_ky: String = "",
    var name_ru: String = "",
    var type: String = "",
    var userId: String = ""
) : Serializable

@Parcel
data class SoilTexture(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("soilId")
    val soilId: Int? = null,
    @SerializedName("texture_ru")
    var texture_ru: String? = null,
    @SerializedName("texture_ky")
    var texture_ky: String? = null,
    @SerializedName("texture_en")
    var texture_en: String? = null
) : Serializable, Parcelable {
    constructor(parcel: android.os.Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    fun createValidForm(type: String, size: String): String{
         return "$type (${size})"
     }

    fun setLocaleSoilTexture(context:Context): String{
        var string = String()
        when(LocaleManager.getLanguagePref(context)){
            LocaleManager.LANGUAGE_KEY_KYRGYZ -> string =  texture_ky!!
            LocaleManager.LANGUAGE_KEy_ENGLISH -> string =  texture_en!!
            LocaleManager.LANGUAGE_KEY_RUSSIAN -> string =  texture_ru!!
        }
        return  string
    }

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeValue(soilId)
        parcel.writeString(texture_ru)
        parcel.writeString(texture_ky)
        parcel.writeString(texture_en)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SoilTexture> {
        override fun createFromParcel(parcel: android.os.Parcel): SoilTexture {
            return SoilTexture(parcel)
        }

        override fun newArray(size: Int): Array<SoilTexture?> {
            return arrayOfNulls(size)
        }
    }
}


@Entity(tableName = "regions")
@Parcel
data class Region(
    @PrimaryKey
    var id: Int = 0,
    val name_en: String = "",
    val name_ky: String = "",
    var name_ru: String = "" ) : Serializable

@Entity(tableName = "villages")
@Parcel
data class Village(
    @PrimaryKey
    var id: Long = 0,
    val name_en: String = "",
    val name_ky: String = "",
    var name_ru: String = "",
    var districtId: Int = 0
) : Serializable

@Entity(tableName = "districts")
@Parcel
data class District(
    @PrimaryKey
    var id: Int = 0,
    val name_en: String = "",
    val name_ky: String = "",
    var name_ru: String = "",
    var regionId: Int = 0

) : Serializable


