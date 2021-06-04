package kg.forestry.ui.harvest.harvest_info

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.*
import kg.forestry.localstorage.model.ListType
import kg.core.utils.Helper.fromStringToLocation
import kg.core.utils.Helper.getLocationFormattedString
import kg.forestry.localstorage.model.District
import kg.forestry.localstorage.model.Region
import kg.forestry.ui.biomass.BiomassActivity
import kg.forestry.ui.map.MapsActivity
import kg.forestry.ui.pastures.PastureListActivity
import kg.forestry.ui.plots.PlotListActivity
import kg.forestry.ui.soil_districts.DistrictListActivity
import kg.forestry.ui.soil_regions.RegionListActivity
import kg.forestry.ui.soil_villages.VillageListActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kg.core.utils.Constants
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
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddHarvestActivity :
    BaseActivity<AddHarvestViewModel>(R.layout.activity_harvest_info, AddHarvestViewModel::class) {
    private var filePath: Uri? = null
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var region: Region? = null
    private var district: District? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseDataFromIntent()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
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
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                // Dismiss the dialog
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
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
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
                directory
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
                Toast.makeText(this,getString(R.string.choose_region_or_state), Toast.LENGTH_SHORT).show()
            }
        }
        name_district.setOnClickListener {
            if (region != null){
                DistrictListActivity.start(this, false, region!!.id)
            }else {
                Toast.makeText(this,getString(R.string.choose_region), Toast.LENGTH_SHORT).show()
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
                    vm.photoPath,
                    harvLocation = fromStringToLocation(location.getValue()),
                    region = name_region.getValue(),
                    village = name_village.getValue(),
                    district = name_district.getValue()
                )
            )
            Handler().postDelayed({
                finish()
            }, 1000)
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
            name_site.setValue(this.plotName)
            name_pasture.setValue(this.pastureName)
            location.setValue(this.harvLocation.getLocationAsString())
            this.sumWetBiomass?.let {
                wetBiomass.setValue(it.getSum().toString())
                vm.wetBiomassValue = it
            }
            this.sumDryBiomass?.let {
                dryBiomass.setValue(it.getSum().toString())
                vm.dryBiomassValue = it
            }
            name_village.setValue(this.village)
            name_region.setValue(this.region)
            name_district.setValue(this.district)

            if (!harvestInfo.harvestPhoto.isNullOrEmpty()) {
                setupImage(fl_take_photo, harvestInfo.harvestPhoto!!)
            }
        }
    }

    private fun setupImage(imageView: ImageView, imageBase64: String) {
        if(!File(imageBase64).isFile){
            val imageAsBytes: ByteArray = Base64.decode(imageBase64.toByteArray(), Base64.DEFAULT)
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))
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
                            wetBiomass.setValue((data?.getSerializableExtra(Constants.BIOMASS_SUM) as Biomass).getSum().toString())
                            vm.wetBiomassValue =
                                data.getSerializableExtra(Constants.BIOMASS_SUM) as Biomass
                        }
                        BiomassType.DRY.value -> {
                            dryBiomass.setValue((data?.getSerializableExtra(Constants.BIOMASS_SUM) as Biomass).getSum().toString())
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
                        filePath = data.data
                        try { // Setting image on image view using Bitmap
                            val bitmap = MediaStore.Images.Media
                                .getBitmap(contentResolver, filePath)
                            fl_take_photo.setImageBitmap(bitmap)
                            vm.photoPath = getRealPathFromURI(filePath!!)//data.data?.path.toString()

                        } catch (e: IOException) { // Log the exception
                            e.printStackTrace()
                        }
                    }
                }
                REQUEST_CAMERA -> {
                    vm.photoPath?.let {
                        val bitmap = BitmapFactory.decodeFile(it)
                        saveImage(bitmap)
                        fl_take_photo.setImageBitmap(bitmap)
                        vm.photoPath = saveTemporarilyCapturedImage(bitmap)
                    }
                }

                VillageListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.Village) as String
                    name_village.setValue(intent)
                }

                RegionListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.Region) as Region
                    region = intent
                    name_region.setValue(intent.name)
                }

                DistrictListActivity.REQUEST_CODE -> {
                    val intent = data?.getSerializableExtra(Constants.DISTRICTS) as District
                    district = intent
                    name_district.setValue(intent.name)
                }
            }
        }
    }

    private fun saveImage(bitmap: Bitmap) {
        // Save image to gallery
        val fileName = "IMG_" + SimpleDateFormat("yyyyMMddHHmmss").format(Date()) + ".jpg"

        MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            fileName,
            "Image of $title"
        )
    }

    private fun saveTemporarilyCapturedImage(bitmap: Bitmap?): String {
        val fileName = "IMG_" + SimpleDateFormat("yyyyMMddHHmmss").format(Date()) + ".jpg"
        if (bitmap != null) {
            val mediaStorageDir = this.getExternalStorage("/forestry")
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) return ""
            val filePath = mediaStorageDir!!.path + File.separator + fileName
            val file = File(filePath)
            try {
                val fileOutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
                return filePath

            } catch (exception: Exception) {
                Log.i("TAG", "saveCapturedImage: could not save picture")
            }
        }
        return ""
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val filePath: String?
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            filePath = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(idx)
            cursor.close()
        }
        return filePath!!
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
