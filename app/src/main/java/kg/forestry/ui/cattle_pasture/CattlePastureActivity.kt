package kg.forestry.ui.cattle_pasture

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kg.forestry.R
import kg.forestry.localstorage.model.CattlePasture
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kotlinx.android.synthetic.main.activity_cattle_pasture.*

class CattlePastureActivity : BaseActivity<CattlePastureViewModel>(R.layout.activity_cattle_pasture, CattlePastureViewModel::class) {

    private var mark: CattlePasture?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.cattle_pasture)
            setNavigationOnClickListener { onBackPressed() }
        }
        parseDataFromIntent() 
        setupViews()
        initClickListeners()
    }

    private fun setupViews() {
        if (mark!=null){
            (when (getString(mark!!.value)) {
                btn_no_pasture.getLable() -> btn_no_pasture
                btn_little_pasture.getLable() -> btn_little_pasture
                btn_temperately.getLable() -> btn_temperately
                btn_intensely.getLable() -> btn_intensely
                else -> null
            })?.setChecked(true)
        }
        updateButtonState()
    }

    private fun parseDataFromIntent() {
        val name = CattlePasture::class.java.canonicalName
        mark = intent.getSerializableExtra(name) as CattlePasture?
    }

    private fun initClickListeners() {
        btn_no_pasture.setOnClickListener {
            clearButtonsStates(); btn_no_pasture.setChecked(true)
            mark = CattlePasture.NO_CATTLE
            updateButtonState()
        }
        btn_little_pasture.setOnClickListener {
            clearButtonsStates(); btn_little_pasture.setChecked(true)
            mark =  CattlePasture.LITTLE
            updateButtonState()
        }
        btn_temperately.setOnClickListener {
            clearButtonsStates(); btn_temperately.setChecked(true)
            mark = CattlePasture.TEMPERATELY
            updateButtonState()
        }
        btn_intensely.setOnClickListener {
            clearButtonsStates(); btn_intensely.setChecked(true)
            mark = CattlePasture.INTENSELY
            updateButtonState()
        }
        btn_next.setOnClickListener {
            finishActivityWithResultOK(mark)
        }
    }


    private fun updateButtonState() {
        btn_next.isEnabled = btn_no_pasture.getChecked()
                || btn_little_pasture.getChecked()
                || btn_temperately.getChecked()
                || btn_intensely.getChecked()
    }

    private fun clearButtonsStates() {
        btn_no_pasture.setChecked(false)
        btn_little_pasture.setChecked(false)
        btn_temperately.setChecked(false)
        btn_intensely.setChecked(false)
    }

    private fun finishActivityWithResultOK(cattlePasture : CattlePasture?){
        val intent = Intent()
        intent.putExtra(Constants.PASTURES,cattlePasture)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val REQUEST_CODE = 10009

        fun start(context: Activity,cattlePasture: CattlePasture?) {
            val intent = Intent(context, CattlePastureActivity::class.java)
            intent.putExtra(
                CattlePasture::class.java.canonicalName,
                cattlePasture)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}
