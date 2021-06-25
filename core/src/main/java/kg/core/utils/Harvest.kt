package kg.core.utils

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.parceler.Parcel
import java.io.Serializable

@Entity(tableName = "harvests")
@Parcel
data class Harvest(
    @PrimaryKey
    var id: String = "",
    var userId: String? = null,
    val plotName: String? = null,
    val pastureName: String? = null,
    var harvestLocation: String? = null,
    var sumWetBiomass: Biomass? = null,
    var sumDryBiomass: Biomass? = null,
    val date: String? = null,
    var harvestPhoto: String? = "",
    var isInServer: Boolean = false,
    var harvLocation: Location = Location(0.0, 0.0),
    var region: String? = null,
    var village: String? = null,
    var district: String? = null,
    var isDraft: Boolean = false
)

@Entity(tableName = "plots")
data class PlotRecord(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    val title: String = "",
    var isInServer: Boolean = false
)

@Entity(tableName = "pastures")
data class PastureRecord(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    val title: String = "",
    var isInServer: Boolean = false
)

data class Location(var lat: Double = 0.0, var lon: Double = 0.0) : Serializable {

    fun getLocationAsString(): String = "${this.lat} ${this.lon}"

}

data class Biomass(var eated: Int = 0, var nonEated: Int = 0) : Serializable {
    fun getSum(): Int = eated + nonEated

    fun restore(eated: Int, nonEated: Int) {
        this.eated = eated; this.nonEated = nonEated
    }
}

enum class BiomassType(val value: String) {
    DRY("DRY"),
    WET("WET")
}

@Parcel
data class Erosion(
    var scours: String = "",
    var pedestal: String = "",
    var cattle_trails: String = "",
    var bare_areas: String = "",
    var indicator: String = "",
    var otherInfo: String = ""
) : Serializable {
    fun asDesc(): String {
        var value = ""
        if (scours.isNotEmpty()) value += scours + ", "
        if (pedestal.isNotEmpty()) value += pedestal + ", "
        if (cattle_trails.isNotEmpty()) value += cattle_trails + ", "
        if (bare_areas.isNotEmpty()) value += bare_areas + ", "
        if (indicator.isNotEmpty()) value += indicator + ", "

        if (value.isNotEmpty()) value = value.substring(0, value.count() - 2)
        return value
    }
}

@Parcel
data class Side(
    var m5: NewDistance = NewDistance(),
    var m10: NewDistance= NewDistance(),
    var m15: NewDistance= NewDistance(),
    var m20: NewDistance= NewDistance(),
    var m25: NewDistance= NewDistance()
) : Serializable {
    fun isValid() = m5.isValid()
            && m10.isValid()
            && m15.isValid()
            && m20.isValid()
            && m25.isValid()
}

@Parcel
data class NewDistance(
    var empty: String? = null,
    var bush: String? = null,
    var eatenPlant: String? = null,
    var nonEatenPlant: String? = null,
    var opad: String? = null,
    var stone: String? = null,
    var base: String? = null,
    var plant_height: String? = null
) : Serializable {

    fun isValid(): Boolean {
        return (!empty.equals(null)
                && !bush.equals(null)
                && !eatenPlant.equals(null)
                && !nonEatenPlant.equals(null)
                && !opad.equals(null)
                && !stone.equals(null)
                && !plant_height.equals(null))
        }
    }



