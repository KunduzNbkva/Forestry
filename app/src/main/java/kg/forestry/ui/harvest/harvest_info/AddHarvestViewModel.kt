package kg.forestry.ui.harvest.harvest_info

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kg.core.Event
import kg.forestry.ui.core.base.BaseViewModel
import kg.core.utils.Biomass
import kg.core.utils.Helper.getRandomString
import kg.forestry.localstorage.Preferences
import kg.core.utils.Harvest
import kg.forestry.repos.HarvestRepository
import java.io.ByteArrayOutputStream
import java.io.File

class AddHarvestViewModel(private val harvestRepository: HarvestRepository, private val prefs: Preferences) : BaseViewModel<Event>() {
    var harvestInfo: Harvest? = null
    var photoPath = ""
    var wetBiomassValue = Biomass(0,0)
    var dryBiomassValue = Biomass(0,0)

    fun isEditMode() = harvestInfo != null

    fun getUserId() = harvestRepository.getUserToken()

    fun saveHarvest(harvest: Harvest) {
        val file = File(harvest.harvestPhoto!!)
        if(file.isFile) {
            val bitmap = BitmapFactory.decodeFile(harvest.harvestPhoto)
            val scaledImage = compressBitmap(bitmap, 10)
            val base64 = toBase64(scaledImage)
            harvest.harvestPhoto = base64
        }
        if (isEditMode()){
            setProgress(true)
            harvest.isInServer = false
            harvestRepository.updateUserHarvestInDB(harvest)
        } else {
            harvest.id = getRandomString(10)
            harvestRepository.updateUserHarvestInDB(harvest)
        }
    }

    fun removeHarvest(harvest: Harvest) {
        setProgress(true)
        if (prefs.isUserAuthorized()) {
            harvestRepository.removeHarvestFromServer(harvest.id).addOnCompleteListener {
                setProgress(false)
            }
            harvestRepository.removeHarvestFromDB(harvest)
        }else{
            harvestRepository.removeHarvestFromDB(harvest)
        }
    }

    fun toBase64(fileBytes: ByteArray): String {
        val base64 = android.util.Base64.encodeToString(fileBytes, 0)
        return base64
    }

    private fun compressBitmap(bitmap: Bitmap, quality:Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

        val byteArray = stream.toByteArray()

        return byteArray
    }
}