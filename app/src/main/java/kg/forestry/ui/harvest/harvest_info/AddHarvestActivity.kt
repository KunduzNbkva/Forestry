package kg.forestry.ui.harvest.harvest_info

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tbruyelle.rxpermissions2.RxPermissions
import kg.core.utils.*
import kg.core.utils.Helper.fromStringToLocation
import kg.core.utils.Helper.getLocationFormattedString
import kg.forestry.R
import kg.forestry.localstorage.model.*
import kg.forestry.ui.biomass.BiomassActivity
import kg.forestry.ui.core.base.BaseActivity
import kg.forestry.ui.extensions.loadImage
import kg.forestry.ui.map.MapsActivity
import kg.forestry.ui.pastures.PastureListActivity
import kg.forestry.ui.plots.PlotListActivity
import kg.forestry.ui.soil_districts.DistrictListActivity
import kg.forestry.ui.soil_regions.RegionListActivity
import kg.forestry.ui.soil_villages.VillageListActivity
import kotlinx.android.synthetic.main.activity_add_plant.*
import kotlinx.android.synthetic.main.activity_harvest_info.*
import kotlinx.android.synthetic.main.activity_harvest_info.btn_next
import kotlinx.android.synthetic.main.activity_harvest_info.fl_take_photo
import kotlinx.android.synthetic.main.activity_harvest_info.location
import kotlinx.android.synthetic.main.activity_harvest_info.name_district
import kotlinx.android.synthetic.main.activity_harvest_info.name_pasture
import kotlinx.android.synthetic.main.activity_harvest_info.name_region
import kotlinx.android.synthetic.main.activity_harvest_info.name_site
import kotlinx.android.synthetic.main.activity_harvest_info.name_village
import kotlinx.android.synthetic.main.activity_harvest_info.toolbar
import kotlinx.android.synthetic.main.activity_harvest_info.tv_take_photo
import org.parceler.Parcels
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddHarvestActivity : BaseActivity<AddHarvestViewModel>(R.layout.activity_harvest_info, AddHarvestViewModel::class) {
    private var village: Village? = null
    private var village_ru: String? = null
    private var village_en: String? = null
    private var village_ky: String? = null

    private var district: District? = null
    private var district_ru: String? = null
    private var district_en: String? = null
    private var district_ky: String? = null

    private var region: Region? = null
    private var region_ru: String? = null
    private var region_ky: String? = null
    private var region_en: String? = null


    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var imagesRef: StorageReference
    private var imageUri: Uri? = null
    private var photoFromCameraURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDataFromIntent()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        imagesRef = storageReference.child(
            "HarvestPhotos/${
                kg.forestry.localstorage.Preferences(
                    this
                ).userToken
            }/harvest_photo"
        )
        toolbar.apply {
            title = when (vm.isEditMode()) {
                true -> getString(R.string.edit_mode)
                else -> getString(R.string.new_value)
            }
            setNavigationOnClickListener { onBackPressed() }
        }
        if (vm.isEditMode()) setupViewsForEditMode(vm.harvestInfo)
        initClickListeners()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.are_you_sure_to_exit))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                super.onBackPressed()
                finish()
            }
            .setNeutralButton(getString(R.string.draft)){ _, _ ->
                val harvest = Harvest(
                    vm.harvestInfo?.id ?: "",
                    vm.getUserId(),
                    name_site.getValue(),
                    name_pasture.getValue(),
                    location.getValue(),
                    vm.wetBiomassValue,
                    vm.dryBiomassValue,
                    getCurrentDate(),
                    vm.photoPath,
                    harvLocation = fromStringToLocation(location.getValue()),
                    region_ky = region_ky,
                    region_en = region_en,
                    region_ru = region_ru,

                    village_kg = village_ky,
                    village_en = village_en,
                    village_ru = village_ru,

                    district_ru = district_ru,
                    district_en = district_en,
                    district_ky = district_ky,
                    isDraft = true)
                vm.saveHarvest(harvest)
                Handler().postDelayed({
                    finish()
                }, 1000)
                super.onBackPressed()
                finish()
                Toast.makeText(this, getString(R.string.draft_saved), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onResume() {
        super.onResume()
        btn_next.isEnabled = name_site.isValidValue()
                && name_pasture.isValidValue()
                && location.isValidValue()
                && wetBiomass.isValidValue()
    }

    private fun Toolbar.addOptionMenu(harvest: Harvest) {
        inflateMenu(R.menu.remove)
        setOnMenuItemClickListener {
            if (it.itemId == R.id.remove) {
                showRemoveEntityQuery(harvest); true
            } else false
        }
    }

    private fun showImagePickerDialog(onFileCreated: ((String) -> Unit)? = null) {
        val items =
            arrayListOf(getString(R.string.capture), getString(R.string.from_gallery))
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.change_picture))
            .setAdapter(adapter) { _, which ->
                    if (which == 0) checkPermissions(Manifest.permission.CAMERA) {
                        openCamera(
                            onFileCreated
                        )
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
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                directory)
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

    private fun showRemoveEntityQuery(harvestInfo: Harvest) {
        if(vm.isNetworkConnected){
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure_to_delete))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    run {
                        vm.removeHarvest(harvestInfo)
                        finish()
                    }
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show()
        }else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        }
    }

    private fun initClickListeners() {
        name_site.setOnClickListener { PlotListActivity.start(this) }
        name_pasture.setOnClickListener { PastureListActivity.start(this) }
        name_region.setOnClickListener { RegionListActivity.start(this) }
        name_village.setOnClickListener {
            if (region != null && district != null){
                VillageListActivity.start(this, district!!.id)
            }else {
                Toast.makeText(this, getString(R.string.choose_region_or_state), Toast.LENGTH_SHORT).show()
            }
        }
        name_district.setOnClickListener {
            if (region != null){
                DistrictListActivity.start(this, false, region!!.id)
            }else {
                Toast.makeText(this, getString(R.string.choose_region), Toast.LENGTH_SHORT).show()
            }
        }


        location.setOnClickListener { MapsActivity.start(this) }

        wetBiomass.setOnClickListener {
            BiomassActivity.start(
                this,
                BiomassType.WET.value,
                dryBiomass = getDryBiomass()
            )
        }
        dryBiomass.setOnClickListener {
            BiomassActivity.start(
                this,
                BiomassType.DRY.value,
                wetBiomass = getWetBiomass(),
                eatW = vm.wetBiomassValue.eated,
                nEatW = vm.wetBiomassValue.nonEated
            )
        }
        btn_next.isEnabled = true
        fl_take_photo.setOnClickListener {
            showMessage()
                showImagePickerDialog { vm.photoPath = it }
        }
        tv_take_photo.setOnClickListener {
            showMessage()
            showImagePickerDialog { vm.photoPath = it }
        }

        btn_next.setOnClickListener {
            btn_next.setOnClickListener {
                vm.saveHarvest(
                    Harvest(
                        vm.harvestInfo?.id ?: "",
                        vm.getUserId(),
                        name_site.getValue(),
                        name_pasture.getValue(),
                        location.getValue(),
                        vm.wetBiomassValue,
                        vm.dryBiomassValue,
                        getCurrentDate(),
                        harvestPhoto = vm.photoPath,
                        harvLocation = fromStringToLocation(location.getValue()),
                        region_ru = region_ru,
                        region_en = region_en,
                        region_ky = region_ky,

                        village_ru = village_ru,
                        village_en = village_en,
                        village_kg = village_ky,

                        district_ru = district_ru,
                        district_en = district_en,
                        district_ky = district_ky,
                        isDraft = false
                    )
                )
                Handler().postDelayed({
                    finish()
                }, 1000)
            }
        }
    }

    private fun showMessage(){
        Toast.makeText(this, R.string.harverst_camera_description, Toast.LENGTH_LONG).show()
    }

    private fun getDryBiomass(): Int {
        return if (dryBiomass.getValue().isNotEmpty()) {
            dryBiomass.getValue().trim().toInt()
        } else {
            0
        }
    }

    private fun getWetBiomass(): Int {
        return if (wetBiomass.getValue().isNotEmpty()) {
            wetBiomass.getValue().trim().toInt()
        } else {
            0
        }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale("RU"))
        val azyr = formatter.format(Date())
        return azyr
    }


    private fun setupViewsForEditMode(harvestInfo: Harvest?) {
        harvestInfo?.run {
            toolbar.addOptionMenu(harvestInfo)
            name_site.setValue(this.plotName!!)
            name_pasture.setValue(this.pastureName!!)
            location.setValue(this.harvLocation.getLocationAsString())
            this.sumWetBiomass?.let {
                wetBiomass.setValue(it.getSum().toString())
                vm.wetBiomassValue = it
            }
            this.sumDryBiomass?.let {
                dryBiomass.setValue(it.getSum().toString())
                vm.dryBiomassValue = it
            }
            when(LocaleManager.getLanguagePref(this@AddHarvestActivity)){
                LocaleManager.LANGUAGE_KEY_RUSSIAN->{
                    harvestInfo.village_ru?.let { name_village.setValue(it) }
                    harvestInfo.region_ru?.let { name_region.setValue(it) }
                    harvestInfo.district_ru?.let { name_district.setValue(it) }
                }
                LocaleManager.LANGUAGE_KEY_KYRGYZ->{
                    harvestInfo.village_kg?.let { name_village.setValue(it) }
                    harvestInfo.region_ky?.let { name_region.setValue(it) }
                    harvestInfo.district_ky?.let { name_district.setValue(it) }
                }
                LocaleManager.LANGUAGE_KEy_ENGLISH->{
                    harvestInfo.village_en?.let { name_village.setValue(it) }
                    harvestInfo.region_en?.let { name_region.setValue(it) }
                    harvestInfo.district_en?.let { name_district.setValue(it) }
                }
            }

            vm.photoPath = harvestInfo.harvestPhoto!!

            if (vm.photoPath != "") {
                fl_take_photo.loadImage(vm.photoPath)
            } else {
                fl_take_photo.loadImage(harvestInfo.harvestPhoto!!)
            }
            if(harvestInfo.isDraft){
                harvestInfo.isDraft = true
            }
        }
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
                BiomassActivity.REQUEST_CODE -> {
                    when (data?.getSerializableExtra(Constants.BIOMASS_TYPE) as String?) {
                        BiomassType.WET.value -> {
                            wetBiomass.setValue(
                                (data?.getSerializableExtra(Constants.BIOMASS_SUM) as Biomass).getSum()
                                    .toString()
                            )
                            vm.wetBiomassValue =
                                data.getSerializableExtra(Constants.BIOMASS_SUM) as Biomass
                        }
                        BiomassType.DRY.value -> {
                            dryBiomass.setValue(
                                (data?.getSerializableExtra(Constants.BIOMASS_SUM) as Biomass).getSum()
                                    .toString()
                            )
                            vm.dryBiomassValue =
                                data.getSerializableExtra(Constants.BIOMASS_SUM) as Biomass
                        }
                    }
                }
                MapsActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.LOCATION) as Location
                    location.setValue(getLocationFormattedString(intent))
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

                VillageListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.Village) as Village
                    village = intent
                    village_en = village!!.name_en
                    village_ky = village!!.name_ky
                    village_ru = village!!.name_ru

                    var name = village!!.name_ru
                    when (LocaleManager.getLanguagePref(this)){
                        LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = village!!.name_ky
                        LocaleManager.LANGUAGE_KEy_ENGLISH -> name = village!!.name_en
                    }
                    name_village.setValue(name)
                }

                RegionListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.Region) as Region
                    region = intent
                    region_ky = region!!.name_ky
                    region_ru = region!!.name_ru
                    region_en = region!!.name_en
                    var name = region!!.name_ru
                    when (LocaleManager.getLanguagePref(this)){
                        LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = region!!.name_ky
                        LocaleManager.LANGUAGE_KEy_ENGLISH -> name = region!!.name_en
                    }
                    name_region.setValue(name)
                }

                DistrictListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.DISTRICTS) as District
                    district = intent
                    district_ru = district!!.name_ru
                    district_ky = district!!.name_ky
                    district_en = district!!.name_en

                    var name = district!!.name_ru
                    when (LocaleManager.getLanguagePref(this)){
                        LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = district!!.name_ky
                        LocaleManager.LANGUAGE_KEy_ENGLISH -> name = district!!.name_en
                    }
                    name_district.setValue(name)
                }
            }
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

    private fun parseDataFromIntent() {
        val name = Harvest::class.java.canonicalName
        Parcels.unwrap<Harvest>(intent.getParcelableExtra(name))?.let {
            vm.harvestInfo = it
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 23
        const val REQUEST_CAMERA = 101

        fun start(context: Context, harvestInfo: Harvest? = null) {
            val intent = Intent(context, AddHarvestActivity::class.java)
            harvestInfo?.let {
                intent.putExtra(
                    Harvest::class.java.canonicalName,
                    Parcels.wrap(harvestInfo)
                )
            }
            context.startActivity(intent)
        }
    }

}
