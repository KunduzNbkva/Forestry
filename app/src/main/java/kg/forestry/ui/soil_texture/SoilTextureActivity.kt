package kg.forestry.ui.soil_texture

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.custom.RoundedSelectView
import kg.core.utils.Constants
import kg.core.utils.LocaleManager
import kg.forestry.localstorage.model.SoilTexture
import kotlinx.android.synthetic.main.activity_soil_texture.*
import org.parceler.Parcels
import java.io.Serializable

class SoilTextureActivity : BaseActivity<SoilTextureViewModel>(R.layout.activity_soil_texture, SoilTextureViewModel::class) {
    var soilTexture = SoilTexture()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = context.getString(R.string.soil_texture)
            setNavigationOnClickListener { onBackPressed() }
        }
        parseDataFromIntent()
        initViews()
        initClickListeners()
    }
    private fun changeSelectedType(textureString: String, containingType: String,containingSize: String?,view: RoundedSelectView, viewSize: RoundedSelectView? ){
        if(textureString.contains(containingType)) view.changeSelect()
        if (containingSize?.let { textureString.contains(it) } == true) viewSize?.let{ it.changeSelect()  }
    }

    private fun initViews() {
//        when (soilTexture.type) {
//            getString(R.string.glina) -> glina.changeSelect()
//            getString(R.string.suglinok) -> suglinok.changeSelect()
//            getString(R.string.suspes) -> supes.changeSelect()
//            getString(R.string.pesok) -> pesok.changeSelect()
//            getString(R.string.il) -> il.changeSelect()
//        }
//
//        when (soilTexture.size) {
//            getString(R.string.small) -> small.changeSelect()
//            getString(R.string.medium) -> medium.changeSelect()
//            getString(R.string.large) -> large.changeSelect()
//            getString(R.string.ex_large) -> ex_large.changeSelect()
//        }
        when(LocaleManager.getLanguagePref(this)){
            LocaleManager.LANGUAGE_KEY_RUSSIAN -> {
                soilTexture.texture_ru?.let { changeSelectedType(it, getString(R.string.glina),getString(R.string.small), glina, small) }
                soilTexture.texture_ru?.let { changeSelectedType(it, getString(R.string.suglinok),getString(R.string.medium), suglinok,medium) }
                soilTexture.texture_ru?.let { changeSelectedType(it, getString(R.string.suspes), getString(R.string.large), supes,large) }
                soilTexture.texture_ru?.let { changeSelectedType(it, getString(R.string.pesok),getString(R.string.ex_large), pesok,ex_large) }
                soilTexture.texture_ru?.let { changeSelectedType(it, getString(R.string.il),null, il,null) }
            }
            LocaleManager.LANGUAGE_KEy_ENGLISH -> {
                soilTexture.texture_en?.let { changeSelectedType(it, getString(R.string.glina),getString(R.string.small), glina, small) }
                soilTexture.texture_en?.let { changeSelectedType(it, getString(R.string.suglinok),getString(R.string.medium), suglinok,medium) }
                soilTexture.texture_en?.let { changeSelectedType(it, getString(R.string.suspes), getString(R.string.large), supes,large) }
                soilTexture.texture_en?.let { changeSelectedType(it, getString(R.string.pesok),getString(R.string.ex_large), pesok,ex_large) }
                soilTexture.texture_en?.let { changeSelectedType(it, getString(R.string.il),null, il,null) }
            }
            LocaleManager.LANGUAGE_KEY_KYRGYZ -> {
                soilTexture.texture_ky?.let { changeSelectedType(it, getString(R.string.glina),getString(R.string.small), glina, small) }
                soilTexture.texture_ky?.let { changeSelectedType(it, getString(R.string.suglinok),getString(R.string.medium), suglinok,medium) }
                soilTexture.texture_ky?.let { changeSelectedType(it, getString(R.string.suspes), getString(R.string.large), supes,large) }
                soilTexture.texture_ky?.let { changeSelectedType(it, getString(R.string.pesok),getString(R.string.ex_large), pesok,ex_large) }
                soilTexture.texture_ky?.let { changeSelectedType(it, getString(R.string.il),null, il,null) }
            }
        }
        updateButtonState()
    }


    private fun parseDataFromIntent() {
        val name = SoilTexture::class.java.canonicalName
        Parcels.unwrap<SoilTexture>(intent.getParcelableExtra(name))?.let {
            soilTexture = it
        }
    }

    private fun initClickListeners() {
        glina.setOnClickListener(::onTypeClick)
        suglinok.setOnClickListener(::onTypeClick)
        supes.setOnClickListener(::onTypeClick)
        pesok.setOnClickListener(::onTypeClick)
        il.setOnClickListener(::onTypeClick)

        ll_small.setOnClickListener {
            clearSize()
            small.changeSelect()
            updateButtonState()
        }

        ll_medium.setOnClickListener {
            clearSize()
            medium.changeSelect()
            updateButtonState()
        }

        ll_large.setOnClickListener {
            clearSize()
            large.changeSelect()
            updateButtonState()
        }

        ll_ex_large.setOnClickListener {
            clearSize()
            ex_large.changeSelect()
            updateButtonState()
        }

        btn_next.setOnClickListener {
            soilTexture.texture_en = soilTexture.createValidForm(getTypeEn()!!,getSizeEn()!!)
            soilTexture.texture_ru = soilTexture.createValidForm(getTypeRu()!!,getSizeRu()!!)
            soilTexture.texture_ky = soilTexture.createValidForm(getTypeKy()!!,getSizeKy()!!)
            finishActivityWithResultOK(soilTexture)
        }

    }

    private fun finishActivityWithResultOK(soilTexture: SoilTexture?) {
        val intent = Intent()
        intent.putExtra(Constants.SOIL, soilTexture as Serializable)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun onTypeClick(v: View) {
        clearType()
        (v as RoundedSelectView).changeSelect()
        updateButtonState()
    }

    private fun updateButtonState() {
        btn_next.isEnabled = getType() != null && getSize() != null
    }

    private fun clearType() {
        glina.clearSelect()
        suglinok.clearSelect()
        supes.clearSelect()
        pesok.clearSelect()
        il.clearSelect()
    }

    private fun clearSize() {
        small.clearSelect()
        medium.clearSelect()
        large.clearSelect()
        ex_large.clearSelect()
    }

    private fun getTypeRu() = when{
        glina.isMarked() -> "Глина"
        suglinok.isMarked() -> "Суглинок"
        supes.isMarked() -> "Супесь"
        pesok.isMarked() -> "Песок"
        il.isMarked() -> "Ил"
        else -> null
    }
    private fun getSizeRu() = when {
        small.isMarked() -> "Слабокаменистые"
        medium.isMarked() -> "Среднекаменистые"
        large.isMarked() -> "Сильнокаменистые"
        ex_large.isMarked() -> "Очень сильнокаменистые"
        else -> null
    }

    private fun getTypeEn() = when{
        glina.isMarked() -> "Clay"
        suglinok.isMarked() -> "Loam"
        supes.isMarked() -> "Sandy loam"
        pesok.isMarked() -> "Sand"
        il.isMarked() -> "Silt"
        else -> null
    }
    private fun getSizeEn() = when {
        small.isMarked() -> "slightly stony"
        medium.isMarked() -> "medium stony"
        large.isMarked() -> "High"
        ex_large.isMarked() -> "Very high"
        else -> null
    }

    private fun getTypeKy() = when{
        glina.isMarked() -> "Чопо"
        suglinok.isMarked() -> "Чопо,кум"
        supes.isMarked() -> "Кумай"
        pesok.isMarked() -> "Кум"
        il.isMarked() -> "Баткактуу"
        else -> null
    }
    private fun getSizeKy() = when {
        small.isMarked() -> "Аз таштактуу"
        medium.isMarked() -> "Орто таштактуу"
        large.isMarked() -> "Таштактуу"
        ex_large.isMarked() -> "Абдан таштактуу"
        else -> null
    }


    private fun getType() = when {
        glina.isMarked() -> getString(R.string.glina)
        suglinok.isMarked() -> getString(R.string.suglinok)
        supes.isMarked() -> getString(R.string.suspes)
        pesok.isMarked() -> getString(R.string.pesok)
        il.isMarked() -> getString(R.string.il)
        else -> null
    }

    private fun getSize() = when {
        small.isMarked() -> getString(R.string.small)
        medium.isMarked() -> getString(R.string.medium)
        large.isMarked() -> getString(R.string.large)
        ex_large.isMarked() -> getString(R.string.ex_large)
        else -> null
    }


    companion object {
        const val REQUEST_CODE = 10049

        fun start(context: Activity, soilTexture: SoilTexture?) {
            val intent = Intent(context, SoilTextureActivity::class.java)
            intent.putExtra(
                SoilTexture::class.java.canonicalName,
                Parcels.wrap(soilTexture)
            )
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}

