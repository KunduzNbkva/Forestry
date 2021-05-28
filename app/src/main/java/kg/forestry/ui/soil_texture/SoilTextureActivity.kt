package kg.forestry.ui.soil_texture

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.custom.RoundedSelectView
import kg.core.utils.Constants
import kg.forestry.localstorage.model.SoilTexture
import kotlinx.android.synthetic.main.activity_soil_texture.*
import org.parceler.Parcels

class SoilTextureActivity : BaseActivity<SoilTextureViewModel>(
    R.layout.activity_soil_texture,
    SoilTextureViewModel::class
) {

    var soilTexture = SoilTexture()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDataFromIntent()
        toolbar.apply {
            title = context.getString(R.string.soil_texture)
            setNavigationOnClickListener { onBackPressed() }
        }
        initViews()
        initClickListeners()
    }

    private fun initViews() {
        when (soilTexture.type) {
            getString(R.string.glina) -> glina.changeSelect()
            getString(R.string.suglinok) -> suglinok.changeSelect()
            getString(R.string.suspes) -> supes.changeSelect()
            getString(R.string.pesok) -> pesok.changeSelect()
            getString(R.string.il) -> il.changeSelect()
        }

        when (soilTexture.size) {
            getString(R.string.small) -> small.changeSelect()
            getString(R.string.medium) -> medium.changeSelect()
            getString(R.string.large) -> large.changeSelect()
            getString(R.string.ex_large) -> ex_large.changeSelect()
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
            soilTexture.size = getSize()
            soilTexture.type = getType()
            finishActivityWithResultOK(soilTexture)
        }

    }

    private fun finishActivityWithResultOK(soilTexture: SoilTexture?) {
        val intent = Intent()
        intent.putExtra(Constants.SOIL, soilTexture)
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
