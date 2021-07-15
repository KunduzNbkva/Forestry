package kg.forestry.ui.user_profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tbruyelle.rxpermissions2.RxPermissions
import kg.core.utils.Helper
import kg.core.utils.LocaleManager
import kg.forestry.R
import kg.forestry.localstorage.model.User
import kg.forestry.ui.core.base.BaseActivity
import kg.forestry.ui.extensions.getExifOrientation
import kg.forestry.ui.extensions.loadImage
import kg.forestry.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_harvest_info.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.toolbar
import kotlinx.android.synthetic.main.activity_user_profile.tv_take_photo
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class EditProfileActivity : BaseActivity<UserProfileViewModel>(R.layout.activity_user_profile, UserProfileViewModel::class) {
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLanguage()
        setupViews()
        if (!Helper.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        } else {
            subscribeToLiveData()
        }
    }

    private fun setupLanguage() {
        when (LocaleManager.getLanguagePref(this)) {
            LocaleManager.LANGUAGE_KEY_KYRGYZ -> kvv_language.setValue("Кыргызча")
            LocaleManager.LANGUAGE_KEy_ENGLISH -> kvv_language.setValue("English")
            else -> kvv_language.setValue("Русский")
        }
    }

    private fun setupImage(imageView: ImageView, imageBase64: String) {
        val file = File(imageBase64)
        if(file.exists()){
            Log.e("file","file path is $file.absolutePath")
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun subscribeToLiveData() {
        vm.setProgress(true)
        vm.getDataSnapshotLiveData().observe(this, Observer {
            vm.setProgress(false)
            if (it != null) { //
                val userInfo = it.getValue(User::class.java)
                inp_layout_name.editText?.setText(userInfo?.username)
                inp_layout_organization.editText?.setText(userInfo?.organization)
                inp_layout_post.editText?.setText(userInfo?.post)
                inp_layout_email.editText?.setText(userInfo?.email)
                kvv_date_reg.setValue(userInfo?.date)
                phone.setValue(userInfo?.phone)

                if (vm.photoPath != "") {
                    setupImage(img_profile, vm.photoPath)
                } else {
                    userInfo?.userPhoto?.let { it1 -> setupImage(img_profile, it1) }
                }
            }
        })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    if (data != null && data.data != null) {
                        filePath = data.data
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                            vm.photoPath = getRealPathFromURI(filePath!!)
                            Log.e("photo","profile path is $filePath and ${vm.photoPath} ")
                            img_profile.setImageURI(filePath)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                }
                REQUEST_CAMERA -> {
                    vm.photoPath.let {
                        val bitmap = BitmapFactory.decodeFile(it)
                        vm.photoPath = it
                        Log.e("photo","camera profile path is $filePath and ${vm.photoPath} ")
                        img_profile.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun setupViews() {
        toolbar.apply {
            title = context.getString(R.string.user_profile)
            setNavigationOnClickListener { onBackPressed() }
        }
        inp_email.addTextChangedListener(getTextWatcher())
        inp_name.addTextChangedListener(getTextWatcher())
        inp_organization.addTextChangedListener(getTextWatcher())
        inp_post.addTextChangedListener(getTextWatcher())
        btn_save.setOnClickListener {
            validateEmail()
        }

        kvv_language.setOnClickListener { showLangDialog() }
        img_profile.setOnClickListener {
            if (Helper.isNetworkConnected(this)) {
                showImagePickerDialog {
                    vm.photoPath = it
                }
            } else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
        tv_take_photo.setOnClickListener {
            if (Helper.isNetworkConnected(this)) {
                showImagePickerDialog {
                    vm.photoPath = it
                }
            } else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUser() {
        vm.userInfo.email = inp_layout_email.editText?.text.toString().trim()
        vm.userInfo.username = inp_layout_name.editText?.text.toString().trim()
        vm.userInfo.post = inp_layout_post.editText?.text.toString().trim()
        vm.userInfo.organization = inp_layout_organization.editText?.text.toString().trim()
        vm.userInfo.date = kvv_date_reg.getValue()
        vm.userInfo.phone = phone.getValue()
        if (vm.photoPath != "") {
            vm.userInfo.userPhoto = vm.photoPath
        }
        if (Helper.isNetworkConnected(this)) {
            vm.updateUserInfo(vm.userInfo)
        }
    }

    private fun validateEmail() {
        val emailTxt = inp_email.text.toString().trim()

        if (emailTxt.isEmpty()) {
            inp_layout_email.error = getString(R.string.email_error)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            inp_layout_email.error = getString(R.string.enter_valid_email)
        } else {
            inp_layout_email.error = null
            updateUser()
            MainActivity.start(this)
        }
    }

    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                toggleButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun toggleButtonState() {
        btn_save.isEnabled = inp_layout_email.editText?.text.toString().trim().isNotEmpty()
                && inp_layout_name.editText?.text.toString().trim().isNotEmpty()
                && inp_layout_organization.editText?.text.toString().trim().isNotEmpty()
                && inp_layout_post.editText?.text.toString().trim().isNotEmpty()
    }

    private fun toBase64(fileBytes: ByteArray): String {
        return Base64.encodeToString(fileBytes, 0)
    }

    private fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }

    private fun showLangDialog() {
        val arrayOf = arrayOf("Кыргызча", "Русский", "English")
        var index = 0
        AlertDialog.Builder(this)
            .setSingleChoiceItems(
                arrayOf,
                arrayOf.indexOf(kvv_language.getValue())
            ) { _, i -> index = i }
            .setPositiveButton(getString(R.string.save)) { _, i -> changeLanguage(arrayOf[index]) }
            .setNegativeButton("Отмена", null)
            .create().show()
    }

    private fun changeLanguage(lang: String) {
        kvv_language.setValue(lang)
        when (lang) {
            "Русский" -> LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_KEY_RUSSIAN)
            "English" -> LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_KEy_ENGLISH)
            "Кыргызча" -> LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_KEY_KYRGYZ)
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
            .setNegativeButton(getString(R.string.cancel_txt)) { dialog, _ -> dialog.dismiss() }
        builder.create().show()

    }

    private fun pickPhotoFromGallery() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        this.startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST)
    }

    @SuppressLint("CheckResult", "ShowToast")
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
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),directory
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

    companion object {
        private const val PICK_IMAGE_REQUEST = 22
        const val REQUEST_CAMERA = 101

        fun start(context: Context) {
            val intent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(intent)
        }
    }
}
