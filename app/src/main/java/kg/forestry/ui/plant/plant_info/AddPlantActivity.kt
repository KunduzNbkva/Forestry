package kg.forestry.ui.plant.plant_info

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tbruyelle.rxpermissions2.RxPermissions
import kg.core.utils.*
import kg.core.utils.Helper.getLocationFormattedString
import kg.forestry.R
import kg.forestry.localstorage.model.*
import kg.forestry.ui.choose_side.ChooseSideActivity
import kg.forestry.ui.core.base.BaseActivity
import kg.forestry.ui.extensions.loadImage
import kg.forestry.ui.map.MapsActivity
import kg.forestry.ui.pastures.PastureListActivity
import kg.forestry.ui.plant_type.PlantTypeActivity
import kg.forestry.ui.plots.PlotListActivity
import kg.forestry.ui.soil_districts.DistrictListActivity
import kg.forestry.ui.soil_erossion.SoilErossionActivity
import kg.forestry.ui.soil_regions.RegionListActivity
import kg.forestry.ui.soil_texture.SoilTextureActivity
import kg.forestry.ui.soil_villages.VillageListActivity
import kg.forestry.ui.tree_type.TreeTypeActivity
import kotlinx.android.synthetic.main.activity_add_plant.*
import kotlinx.android.synthetic.main.activity_add_plant.btn_next
import kotlinx.android.synthetic.main.activity_add_plant.fl_take_photo
import kotlinx.android.synthetic.main.activity_add_plant.location
import kotlinx.android.synthetic.main.activity_add_plant.name_district
import kotlinx.android.synthetic.main.activity_add_plant.name_pasture
import kotlinx.android.synthetic.main.activity_add_plant.name_region
import kotlinx.android.synthetic.main.activity_add_plant.name_site
import kotlinx.android.synthetic.main.activity_add_plant.name_village
import kotlinx.android.synthetic.main.activity_add_plant.toolbar
import kotlinx.android.synthetic.main.activity_add_plant.tv_take_photo
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.expansion_cattle.view.*
import org.parceler.Parcels
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class ExpansionModel(
    var img: Int,
    var title: String,
    var isColor: Boolean
)
data class AnimalModel(
    var img: Int,
    var title: String,
    var isSelected: Boolean
)

class AddPlantActivity : BaseActivity<AddPlantViewModel>(R.layout.activity_add_plant, AddPlantViewModel::class), ExpansionClick, ExpansionGrazeClick {
    private var region: Region? = null
    private var district: District? = null
    private lateinit var cattleList:ArrayList<AnimalModel>

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var imagesRef: StorageReference
    private var imageUri: Uri? = null
    private var photoFromCameraURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        imagesRef = storageReference.child(
            "PlantPhotos/${
                kg.forestry.localstorage.Preferences(
                    this
                ).userToken
            }/plant_photo"
        )
        parseDataFromIntent()
        toolbar.apply {
            title = when (vm.isEditMode()) {
                true -> getString(R.string.edit_mode)
                else -> getString(R.string.new_value)
            }
            setNavigationOnClickListener { onBackPressed() }
        }
        if (vm.isEditMode()) setupViewsForEditMode(vm.plantInfo)
        initClickListeners()
        setExpansionCattle()
        setExpansionGraze()
        setExpansionSoilColor()
    }

    override fun onResume() {
        super.onResume()
        updateButtonState()
    }

    private fun Toolbar.addOptionMenu(plant: Plant) {
        inflateMenu(R.menu.remove)
        setOnMenuItemClickListener {
            if (it.itemId == R.id.remove) {
                showRemoveEntityQuery(plant); true
            } else false
        }
    }

    private fun uploadPhoto(imageUri: Uri) {
        vm.setProgress(true)
        imagesRef.putFile(imageUri)
            .continueWithTask { imagesRef.downloadUrl }
            .addOnSuccessListener {
                Log.e("photo", "uploadPhoto: ${it}")
                vm.photoPath = it.toString()
                fl_take_photo.loadImage(it.toString())
                vm.setProgress(false)
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "uploadPhotoERROR: ${e.message}")
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
    }


    private fun showRemoveEntityQuery(plantInfo: Plant) {
        if (vm.isNetworkConnected) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure_to_delete))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    run {
                        vm.removePlant(plantInfo)
                        finish()
                    }
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show()
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        }
    }

    private fun setupViewsForEditMode(plantInfo: Plant?) {
        plantInfo?.run {
            toolbar.addOptionMenu(plantInfo)
            name_site.setValue(this.plotName)
            name_pasture.setValue(this.pastureName)
            location.setValue(this.plantLocation.getLocationAsString())
            desc_point.setValue(this.pointDescription)
            desc_site.setValue(this.plotDescription)
            plant_type.setValue(this.plants)
            tree_type.setValue(this.trees)
            soil_texture.setValue(this.soilTexture)
            soilcolor.tv_text_expansion.text = this.soilColor
            degree_erosion.setValue(this.erosionDegree)
            cattle_pasture.tv_text_expansion.text = this.cattlePasture
            pasture_cattle.tv_text_expansion.text = this.typePasture
            name_village.setValue(this.village)
            name_region.setValue(this.region)
            name_district.setValue(this.district)

            vm.photoPath = plantInfo.plantPhoto
            if (vm.photoPath != "") {
                fl_take_photo.loadImage(vm.photoPath)
            } else {
                fl_take_photo.loadImage(plantInfo.plantPhoto)
            }
            if(plantInfo.isDraft){
                plantInfo.isDraft = true
            }
        }
    }


    private fun setExpansionCattle(){
        cattleList = ArrayList()
        cattleList.add(AnimalModel(R.drawable.cow, getString(R.string.cows_valid), false))
        cattleList.add(AnimalModel(R.drawable.sheep, getString(R.string.sheeps_valid), false))
        cattleList.add(AnimalModel(R.drawable.horse, getString(R.string.horses_valid), false))
        cattleList.add(AnimalModel(R.drawable.yak, getString(R.string.yaks_valid), false))
        val recyclerCattle = findViewById<RecyclerView>(R.id.cattle_list)
        val adapter = AnimalAdapter(cattleList, this)
        recyclerCattle.adapter = adapter
    }
    private fun setExpansionGraze(){
        val grazeList = ArrayList<String>()
        grazeList.add(getString(R.string.no_pastures))
        grazeList.add(getString(R.string.little))
        grazeList.add(getString(R.string.intensively))
        grazeList.add(getString(R.string.temperately))
        val recyclerCattle = findViewById<RecyclerView>(R.id.graze_list)
        val adapter = GrazeAdapter(grazeList, this)
        recyclerCattle.adapter = adapter
    }

    private fun setExpansionSoilColor(){
        val soilColorList = ArrayList<ExpansionModel>()
        soilColorList.add(ExpansionModel(R.drawable.soil_black, getString(R.string.black), true))
        soilColorList.add(ExpansionModel(R.drawable.soil_gray, getString(R.string.gray), true))
        soilColorList.add(ExpansionModel(R.drawable.soil_white, getString(R.string.white), true))
        soilColorList.add(ExpansionModel(R.drawable.soil_yellow, getString(R.string.yellow), true))
        soilColorList.add(ExpansionModel(R.drawable.soil_red, getString(R.string.red), true))
        soilColorList.add(ExpansionModel(R.drawable.soil_brown, getString(R.string.brown), true))
        val recyclerCattle = findViewById<RecyclerView>(R.id.soil_color_list)
        val adapter = ColorAdapter(soilColorList, this)
        recyclerCattle.adapter = adapter
    }

    override fun colorClick(model: ExpansionModel) {
        soilcolor.tv_text_expansion.text = model.title
    }

    override fun animalClick(model: AnimalModel) {
        var text = ""
        for (model in cattleList ) {
            if (model.isSelected) {
                text += model.title +" "
            }
        }
        pasture_cattle.tv_text_expansion.text = text
    }

    override fun expansionItemClick(string: String) {
        cattle_pasture.tv_text_expansion.text = string
    }

    private fun updateButtonState() {
        btn_next.isEnabled = name_site.isValidValue()
                && name_pasture.isValidValue()
                && location.isValidValue()
                && plant_type.isValidValue()
                && soil_texture.isValidValue()
                && soilcolor.tv_text_expansion.text != null
                && cattle_pasture.tv_text_expansion.text != null
                && isValidPastureType()
                && degree_erosion.isValidValue()
    }

    private fun isValidPastureType(): Boolean {
        return cattle_pasture.tv_text_expansion.text == getString(CattlePasture.NO_CATTLE.value)
                || pasture_cattle.tv_text_expansion.text != null
    }

    private fun initClickListeners() {
        name_site.setOnClickListener { PlotListActivity.start(this) }
        name_pasture.setOnClickListener { PastureListActivity.start(this) }

        name_region.setOnClickListener { RegionListActivity.start(this) }
        name_village.setOnClickListener {
            if (region != null && district != null) {
                VillageListActivity.start(this, district!!.id)
            } else {
                Toast.makeText(this, getString(R.string.choose_region_or_state), Toast.LENGTH_SHORT).show()
            }
        }
        name_district.setOnClickListener {
            if (region != null) {
                DistrictListActivity.start(this, false, region!!.id)
            } else {
                Toast.makeText(this, getString(R.string.choose_region), Toast.LENGTH_SHORT).show()
            }
        }

        location.setOnClickListener { MapsActivity.start(this) }
        soil_texture.setOnClickListener { SoilTextureActivity.start(this, vm.soilTexture) }
        degree_erosion.setOnClickListener { SoilErossionActivity.start(this, vm.soilErossion) }
        plant_type.setOnClickListener { PlantTypeActivity.start(this, plant_type.getValue()) }
        tree_type.setOnClickListener { TreeTypeActivity.start(this, tree_type.getValue()) }
        fl_take_photo.setOnClickListener {
            showImagePickerDialog {
                vm.photoPath = it
            }
        }
        tv_take_photo.setOnClickListener {
            showImagePickerDialog {
                vm.photoPath = it
            }
        }
        btn_next.setOnClickListener {
          getInfo()
        }
    }

    private fun getInfo(){
        val cattleType = pasture_cattle.tv_text_expansion.text.toString()
        val grazeType = cattle_pasture.tv_text_expansion.text.toString()
        val soilColor = soilcolor.tv_text_expansion.text.toString()
        val plant = Plant(
            vm.plantInfo?.id ?: "",
            vm.getUserId(),
            name_site.getValue(),
            name_pasture.getValue(),
            location.getValue(),
            desc_point.getValue(),
            desc_site.getValue(),
            plant_type.getValue(),
            tree_type.getValue(),
            soil_texture.getValue(),
            soilColor,
            degree_erosion.getValue(),
            grazeType,
            cattleType,
            date = getCurrentDate(),
            plantPhoto = vm.photoPath,
            plantLocation = Helper.fromStringToLocation(location.getValue()),
            region = name_region.getValue(),
            village = name_village.getValue(),
            district = name_district.getValue(),
            isDraft = false
        )
        if (vm.isEditMode()) {
            plant.southSide = vm.plantInfo!!.southSide
            plant.northSide = vm.plantInfo!!.northSide
            plant.westSide = vm.plantInfo!!.westSide
            plant.eastSide = vm.plantInfo!!.eastSide
        }
        ChooseSideActivity.start(this, plant, vm.isEditMode())
    }

    private fun showImagePickerDialog(onFileCreated: ((String) -> Unit)? = null) {
        val items =
            arrayListOf(getString(R.string.capture), getString(R.string.from_gallery))
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.change_picture))
            .setAdapter(adapter) { _, which ->
                if (which == 0) checkPermissions(Manifest.permission.CAMERA) {
                    openCamera(onFileCreated)
                }
                else checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) { pickPhotoFromGallery() }
            }
            .setCancelable(true)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        builder.create().show()

    }

    private fun pickPhotoFromGallery() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        this.startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST)
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @SuppressLint("CheckResult")
    private fun checkPermissions(permission: String, onRequestGranted: () -> Unit) {
        RxPermissions(this)
            .requestEachCombined(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe({ result ->
                when {
                    result.granted -> {
                        onRequestGranted()
                    }
                    result.shouldShowRequestPermissionRationale -> {
                    }
                    else -> {
                        Toast.makeText(
                            this,
                            getString(R.string.need_permission),
                            Toast.LENGTH_SHORT
                        )
                    }
                }
            }, { it.printStackTrace() })

    }

    private fun createImageTempFile(): File {
        val directory = getExternalStorage("/.forestry")
        if (!directory.exists()) directory.mkdirs()
        return File.createTempFile("IMG_", ".jpg", directory)
    }

    private fun getExternalStorage(directory: String): File {
        return when (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
            true -> File(
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directory
            )
            else -> File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), directory)
        }
    }

    private fun openCamera(onFileCreated: ((String) -> Unit)? = null) {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        onFileCreated?.let {
            val imageTempFile = createImageTempFile()
            it.invoke(imageTempFile.absolutePath)
            val uriFromFile = getUriFromFile(imageTempFile)
            photoFromCameraURI = uriFromFile
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uriFromFile)
        }
        this.startActivityForResult(takePicture, REQUEST_CAMERA)
    }

    private fun getUriFromFile(file: File): Uri {
        return FileProvider.getUriForFile(
            applicationContext,
            "${packageName}.fileprovider", file
        )
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale("RU"))
        val azyr = formatter.format(Date())
        return azyr
    }

    private fun parseDataFromIntent() {
        val name = Plant::class.java.canonicalName
        Parcels.unwrap<Plant>(intent.getParcelableExtra(name))?.let {
            vm.plantInfo = it
            Log.e("data", "plantInfo is $it")
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.are_you_sure_to_exit))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                super.onBackPressed()
            }
            .setNeutralButton(getString(R.string.draft)){ _, _ ->
                vm.plantInfo = getDraftInfo()
                vm.saveDraftPlant()
                super.onBackPressed()
                Toast.makeText(this, getString(R.string.draft_saved), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

    }

    private fun getDraftInfo(): Plant{
        val cattleType = pasture_cattle.tv_text_expansion.text.toString()
        val grazeType = cattle_pasture.tv_text_expansion.text.toString()
        val soilColor = soilcolor.tv_text_expansion.text.toString()
        return  Plant(
            vm.plantInfo?.id ?: "",
            vm.getUserId(),
            name_site.getValue(),
            name_pasture.getValue(),
            location.getValue(),
            desc_point.getValue(),
            desc_site.getValue(),
            plant_type.getValue(),
            tree_type.getValue(),
            soil_texture.getValue(),
            soilColor,
            degree_erosion.getValue(),
            grazeType,
            cattleType,
            date = getCurrentDate(),
            plantPhoto = vm.photoPath,
            plantLocation = Helper.fromStringToLocation(location.getValue()),
            region = name_region.getValue(),
            village = name_village.getValue(),
            district = name_district.getValue(),
            isDraft = true
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PlotListActivity.REQUEST_CODE -> {
                    val type = data?.getSerializableExtra(Constants.LIST_ITEM_TYPE) as ListType
                    if (type == ListType.PLOT) {
                        name_site.setValue(data.getStringExtra(Constants.LIST_ITEM)!!)
                    }
                }
                PastureListActivity.REQUEST_CODE -> {
                    val type = data?.getSerializableExtra(Constants.LIST_ITEM_TYPE) as ListType
                    if (type == ListType.PASTURE) {
                        name_pasture.setValue(data.getStringExtra(Constants.LIST_ITEM)!!)
                    }
                }
                SoilErossionActivity.REQUEST_CODE -> {
                    vm.soilErossion = data?.getSerializableExtra(Constants.SOIL) as Erosion
                    degree_erosion.setValue(vm.soilErossion.asDesc())
                }
                PlantTypeActivity.REQUEST_CODE -> {
                    val plantType = data?.getSerializableExtra(Constants.PLANT_CATALOG) as String
                    plant_type.setValue(plantType)
                }
                TreeTypeActivity.REQUEST_CODE -> {
                    var treeType = data?.getSerializableExtra(Constants.TREES_CATALOG) as String
                    if (treeType.take(1) == ",") {
                        treeType = treeType.substring(1)
                    }
                    tree_type.setValue(treeType)
                }
                SoilTextureActivity.REQUEST_CODE -> {
                    vm.soilTexture = data?.getSerializableExtra(Constants.SOIL) as SoilTexture
                    soil_texture.setValue(vm.soilTexture.toValidFormat())
                }
                MapsActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.LOCATION) as Location
                    location.setValue(getLocationFormattedString(intent))
                }
                RegionListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.Region) as Region
                    region = intent
                    name_region.setValue(intent.name)
                }
                VillageListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.Village) as String
                    name_village.setValue(intent)
                }
                DistrictListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.DISTRICTS) as District
                    district = intent
                    name_district.setValue(intent.name)
                }
                PICK_IMAGE_REQUEST -> {
                    if (data != null && data.data != null) {
                        imageUri = data.data
                        uploadPhoto(imageUri!!)
                    }
                }
                REQUEST_CAMERA -> {
                    vm.photoPath.let {
                        photoFromCameraURI?.let {
                            Log.e("photo", "camera profile data is $data and ${vm.photoPath} ")
                            uploadPhoto(it)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 27
        const val REQUEST_CAMERA = 1001

        fun start(context: Context, plantInfo: Plant? = null) {
            val intent = Intent(context, AddPlantActivity::class.java)
            plantInfo?.let {
                intent.putExtra(
                    Plant::class.java.canonicalName,
                    Parcels.wrap(plantInfo)
                )
            }
            context.startActivity(intent)
        }
    }
}
