package kg.forestry.localstorage.model

import kg.forestry.R

enum class ListType(val value: String) {
    PLOT("Plot"),
    PASTURE("Pasture"),
    PLANT("Plant"),
    REGION("Region")
}
enum class CattlePasture(val value: Int){
    NO_CATTLE(R.string.no_pastures),
    LITTLE(R.string.little),
    TEMPERATELY(R.string.temperately),
    INTENSELY(R.string.intensively)
}
