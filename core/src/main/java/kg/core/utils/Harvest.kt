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
    var userId: String = "",
    val plotName: String = "",
    val pastureName: String = "",
    var harvestLocation: String = "",
    var sumWetBiomass: Biomass? = null,
    var sumDryBiomass: Biomass? = null,
    val date: String = "",
    var harvestPhoto: String? = "",
    var isInServer: Boolean = false,
    var harvLocation: Location = Location(0.0,0.0),
    var region: String = "",
    var village: String = "",
    var district: String = ""
    )

@Entity(tableName = "plots")
data class PlotRecord(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    val title: String = "",
    var isInServer:Boolean = false)

@Entity(tableName = "pastures")
data class PastureRecord(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    val title: String = "",
    var isInServer:Boolean = false)

data class Location(var lat: Double = 0.0, var lon: Double = 0.0) : Serializable{

    fun getLocationAsString():String = "${this.lat} ${this.lon}"

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
    var m5: Distance = Distance(),
    var m10: Distance = Distance(),
    var m15: Distance = Distance(),
    var m20: Distance = Distance(),
    var m25: Distance = Distance()
) : Serializable {
    fun isValid() = m5.isValid()
            && m10.isValid()
            && m15.isValid()
            && m20.isValid()
            && m25.isValid()
}
@Parcel
data class Distance(
    var d10: String = "NULL",
    var d30: String = "NULL",
    var d50: String = "NULL",
    var d70: String = "NULL",
    var d90: String = "NULL",
    var plant_height: String = ""
) : Serializable {
    private val excludeTypes = arrayOf("EMPTY", "WIND", "STONE")

    fun isValid(): Boolean {
        return ((!d10.contentEquals("NULL")
                && !d30.contentEquals("NULL")
                && !d50.contentEquals("NULL")
                && !d70.contentEquals("NULL")
                && !d90.contentEquals("NULL")
                && plant_height.isNotEmpty())
                || !isNeedHeight())
    }

    fun isNeedHeight() =
        (d10 !in excludeTypes || d30 !in excludeTypes || d50 !in excludeTypes || d70 !in excludeTypes || d90 !in excludeTypes)
}



//@Parcel
//data class Distance(
//    var d10: ArrayList<String>? = null,
//    var d30: ArrayList<String>? = null,
//    var d50: ArrayList<String>? = null,
//    var d70: ArrayList<String>? = null,
//    var d90: ArrayList<String>? = null,
//    var plant_height: String = ""
//) : Serializable {
//    private val excludeTypes = arrayOf("EMPTY", "WIND", "STONE")
//
//    fun isValid(): Boolean {
//        return ((d10.isNullOrEmpty()
//                && d30.isNullOrEmpty()
//                && d50.isNullOrEmpty()
//                && d70.isNullOrEmpty()
//                && d90.isNullOrEmpty()
//                && plant_height.isNotEmpty())
//                || !isNeedHeight())
//    }
//
//    fun isNeedHeight() =
//        (d10?.get(0) !in excludeTypes || d30?.get(0) !in excludeTypes || d50?.get(0) !in excludeTypes || d70?.get(0) !in excludeTypes || d90 !in excludeTypes)
//}


enum class TypeInDistance {
    NULL,
    EMPTY,
    TREE,
    BUSH,
    GRASS,
    BASE,
    WIND,
    STONE
}
