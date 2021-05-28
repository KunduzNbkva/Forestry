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
import kg.core.utils.Helper.getLocationFormattedString
import kg.forestry.localstorage.model.*
import kg.forestry.ui.cattle_pasture.CattlePastureActivity
import kg.forestry.ui.cattle_type.CattleTypeActivity
import kg.forestry.ui.choose_side.ChooseSideActivity
import kg.forestry.ui.map.MapsActivity
import kg.forestry.ui.pastures.PastureListActivity
import kg.forestry.ui.plant_type.PlantTypeActivity
import kg.forestry.ui.plots.PlotListActivity
import kg.forestry.ui.soil_color.SoilColorActivity
import kg.forestry.ui.soil_erossion.SoilErossionActivity
import kg.forestry.ui.soil_districts.DistrictListActivity
import kg.forestry.ui.soil_regions.RegionListActivity
import kg.forestry.ui.soil_villages.VillageListActivity
import kg.forestry.ui.soil_texture.SoilTextureActivity
import kg.forestry.ui.tree_type.TreeTypeActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kg.core.utils.Constants
import kg.core.utils.Helper
import kg.core.utils.gone
import kg.core.utils.visible
import kg.forestry.localstorage.model.District
import kg.forestry.localstorage.model.Plant
import kg.forestry.localstorage.model.Region
import kg.forestry.localstorage.model.SoilTexture
import kotlinx.android.synthetic.main.activity_add_plant.*
import org.parceler.Parcels
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddPlantActivity :
    BaseActivity<AddPlantViewModel>(R.layout.activity_add_plant, AddPlantViewModel::class) {

    private var filePath: Uri? = null
    lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
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
        if (vm.isEditMode()) setupViewsForEditMode(vm.plantInfo)
        initClickListeners()
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

    private fun showRemoveEntityQuery(plantInfo: Plant) {
        if(vm.isNetworkConnected){
            AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить запись?")
                .setPositiveButton("Да") { _, _ ->
                    run {
                        vm.removePlant(plantInfo)
                        finish()
                    }
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show()
        }else {
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
            soil_color.setValue(this.soilColor)
            degree_erosion.setValue(this.erosionDegree)
            cattle_pasture.setValue(this.cattlePasture)
            type_pasture.setValue(this.typePasture)
            name_village.setValue(this.village)
            name_region.setValue(this.region)
            name_district.setValue(this.district)
            if (plantInfo.plantPhoto.isNotEmpty()) {
                setupImage(fl_take_photo, plantInfo.plantPhoto)
//                fl_take_photo.setImageURI(Uri.parse("file:/" + plantInfo.plantPhoto))
            }
        }
    }

    private fun setupImage(imageView: ImageView, imageBase64: String) {
        if(File(imageBase64).isFile == false){
            val imageAsBytes: ByteArray = Base64.decode(imageBase64.toByteArray(), Base64.DEFAULT)
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))
        }
    }

    private fun updateButtonState() {
        btn_next.isEnabled = name_site.isValidValue()
                && name_pasture.isValidValue()
                && location.isValidValue()
                && plant_type.isValidValue()
                && soil_texture.isValidValue()
                && soil_color.isValidValue()
                && cattle_pasture.isValidValue()
                && isValidPastureType()
                && degree_erosion.isValidValue()
    }

    private fun isValidPastureType(): Boolean {
        return cattle_pasture.getValue() == getString(CattlePasture.NO_CATTLE.value) || type_pasture.isValidValue()
    }

    private fun initClickListeners() {
        name_site.setOnClickListener { PlotListActivity.start(this) }
        name_pasture.setOnClickListener { PastureListActivity.start(this) }

        name_region.setOnClickListener { RegionListActivity.start(this) }
        name_village.setOnClickListener {
            if (region != null && district != null){
                VillageListActivity.start(this, district!!.id)
            }else {
                Toast.makeText(this,"Выберите сначала область/район", Toast.LENGTH_SHORT).show()
            }
        }
        name_district.setOnClickListener {
            if (region != null){
                DistrictListActivity.start(this, false, region!!.id)
            }else {
                Toast.makeText(this,"Выберите сначала область", Toast.LENGTH_SHORT).show()
            }
        }


//        if (!vm.isNetworkConnected){
//            if (!vm.isEditMode()){
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return
//                }
//                fusedLocationClient.lastLocation.addOnSuccessListener {
//                    if (it != null) {
//                        location.setValue("${it.latitude} ${it.longitude}")
//                    }
//                }
//
//            }
//            location.setOnClickListener { Toast.makeText(this,"Проверьте интернет соединение",Toast.LENGTH_SHORT).show() }
//        }else {
//            location.setOnClickListener { MapsActivity.start(this) }
//        }
        location.setOnClickListener { MapsActivity.start(this) }

        cattle_pasture.setOnClickListener { CattlePastureActivity.start(this,vm.cattlePasture) }
        type_pasture.setOnClickListener { CattleTypeActivity.start(this,vm.cattleType) }
        soil_color.setOnClickListener { SoilColorActivity.start(this,vm.soilColor) }
        soil_texture.setOnClickListener { SoilTextureActivity.start(this,vm.soilTexture) }
        degree_erosion.setOnClickListener { SoilErossionActivity.start(this,vm.soilErossion) }
        plant_type.setOnClickListener { PlantTypeActivity.start(this,plant_type.getValue()?:"") }
        tree_type.setOnClickListener { TreeTypeActivity.start(this,tree_type.getValue()?:"") }
        fl_take_photo.setOnClickListener {showImagePickerDialog { vm.photoPath = it }} //if (Helper.isNetworkConnected(this)) {  showImagePickerDialog { vm.photoPath = it } }}
        tv_take_photo.setOnClickListener {showImagePickerDialog { vm.photoPath = it }} //if (Helper.isNetworkConnected(this)) {  showImagePickerDialog { vm.photoPath = it } }}


            btn_next.setOnClickListener {
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
                soil_color.getValue(),
                degree_erosion.getValue(),
                cattle_pasture.getValue(),
                type_pasture.getValue(),
                date = getCurrentDate(),
                plantPhoto = vm.photoPath,
                plantLocation = Helper.fromStringToLocation(location.getValue()),
                region = name_region.getValue(),
                village = name_village.getValue(),
                district = name_district.getValue()
            )
            if (vm.isEditMode()){
                plant.southSide = vm.plantInfo!!.southSide
                plant.northSide = vm.plantInfo!!.northSide
                plant.westSide = vm.plantInfo!!.westSide
                plant.eastSide = vm.plantInfo!!.eastSide
            }
            ChooseSideActivity.start(this, plant, vm.isEditMode())
        }
    }

    @SuppressLint("SimpleDateFormat")
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

    private fun showImagePickerDialog(onFileCreated: ((String) -> Unit)? = null) {
        val items =
            arrayListOf<String>(getString(R.string.capture), getString(R.string.from_gallery))
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

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
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

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale("RU"))
        val azyr = formatter.format(Date())
        return azyr
    }

    private fun parseDataFromIntent() {
        val name = Plant::class.java.canonicalName
        Parcels.unwrap<Plant>(intent.getParcelableExtra(name))?.let {
            vm.plantInfo = it
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Вы действительно хотите выйти, все несохраненные данные будут потеряны ?")
            .setCancelable(false)
            .setPositiveButton("Да") { dialog, id ->
                super.onBackPressed()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
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
                CattlePastureActivity.REQUEST_CODE -> {
                    vm.cattlePasture = data?.getSerializableExtra(Constants.PASTURES) as CattlePasture
                    decideCattleTypeVisibility(vm.cattlePasture)
                    cattle_pasture.setValue(getString(vm.cattlePasture!!.value))
                }
                CattleTypeActivity.REQUEST_CODE -> {
                    vm.cattleType = data?.getSerializableExtra(Constants.PASTURES) as String
                    type_pasture.setValue(vm.cattleType)
                }
                SoilColorActivity.REQUEST_CODE -> {
                    vm.soilColor = data?.getSerializableExtra(Constants.SOIL) as String
                    soil_color.setValue(vm.soilColor)
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
                    if(treeType.take(1) == ","){
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
                        filePath = data.data
                        try { // Setting image on image view using Bitmap
                            val bitmap = MediaStore.Images.Media
                                .getBitmap(contentResolver, filePath)
                            fl_take_photo.setImageBitmap(bitmap)
                           // val path = RealPathUtil.getRealPathFromURI_API19(this, filePath)
//                            val bOut = ByteArrayOutputStream()
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bOut)
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
            }
        }
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

    private fun saveTemporarilyCapturedImage(bitmap: Bitmap?): String {
        val fileName = "IMG_" + SimpleDateFormat("yyyyMMddHHmmss").format(Date()) + ".jpg"
        if (bitmap != null) {
            val mediaStorageDir = this.getExternalStorage("/forestry")
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) return ""
            val filePath = mediaStorageDir!!.path + File.separator + fileName
            val file = File(filePath)
            try {
                val fileOutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
                return filePath

            } catch (exception: Exception) {
                Log.i("TAG", "saveCapturedImage: could not save picture")
            }
        }
        return ""
    }

    private fun decideCattleTypeVisibility(cattlePasture: CattlePasture?) {
        when(cattlePasture){
            CattlePasture.NO_CATTLE -> type_pasture.gone()
            else -> type_pasture.visible()
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
