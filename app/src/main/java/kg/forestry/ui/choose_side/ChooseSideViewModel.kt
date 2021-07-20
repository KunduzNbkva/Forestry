package kg.forestry.ui.choose_side

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kg.core.Event
import kg.forestry.ui.core.base.BaseViewModel
import kg.core.utils.Helper
import kg.forestry.localstorage.model.Plant
import kg.forestry.repos.PlantsRepository
import java.io.ByteArrayOutputStream
import java.io.File


class ChooseSideViewModel(private val plantsRepository: PlantsRepository) : BaseViewModel<Event>() {
    var plantInfo = Plant()

    fun savePlant(isEditMode:Boolean) {
//        val file = File(plantInfo.plantPhoto)
//        if(file.isFile) {
//            val bitmap = BitmapFactory.decodeFile(plantInfo.plantPhoto)
//            val scaledImage = compressBitmap(bitmap, 10)
//            val base64 = toBase64(scaledImage)
//            plantInfo.plantPhoto = base64
//            Log.v("_Base:", base64)
//        }
        if (isEditMode){
            plantsRepository.removePlantFromServer(plantInfo.id).addOnCompleteListener {
                setProgress(false)
            }
            plantInfo.isInServer = false
            plantsRepository.updateUserPlantInDB(plantInfo)
        }else{
            plantInfo.id = Helper.getRandomString(10)
            plantsRepository.updateUserPlantInDB(plantInfo)
        }
    }

    fun toBase64(fileBytes: ByteArray): String {
        val base64 = android.util.Base64.encodeToString(fileBytes, 0)
        return base64
    }

    private fun compressBitmap(bitmap:Bitmap, quality:Int): ByteArray {
        // Initialize a new ByteArrayStream
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

        val byteArray = stream.toByteArray()

        return byteArray
    }

}
