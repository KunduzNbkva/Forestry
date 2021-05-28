package kg.forestry.ui.soil_color

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kotlinx.android.synthetic.main.activity_soil_color.*
import org.parceler.Parcels

class SoilColorActivity :
    BaseActivity<SoilColorViewModel>(R.layout.activity_soil_color, SoilColorViewModel::class) {

    private var mark = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDataFromIntent()
        toolbar.apply {
            title = context.getString(R.string.soil_color)
            setNavigationOnClickListener { onBackPressed() }
        }
        initViews()
        initClickListeners()
    }

    private fun initViews() {
        when (mark) {
            getString(R.string.black) -> rcv_black.changeSelect()
            getString(R.string.gray) -> rcv_grey.changeSelect()
            getString(R.string.white) -> rcv_white.changeSelect()
            getString(R.string.red) -> rcv_red.changeSelect()
            getString(R.string.yellow) -> rcv_yellow.changeSelect()
            getString(R.string.brown) -> rcv_brown.changeSelect()


        }
    }

    private fun parseDataFromIntent() {
        val name = String::class.java.canonicalName
        Parcels.unwrap<String>(intent.getParcelableExtra(name))?.let {
            mark = it
        }
    }

    private fun initClickListeners() {
        ll_black.setOnClickListener {
            reset()
            rcv_black.changeSelect()
            mark = getString(R.string.black)
        }

        ll_grey.setOnClickListener {
            reset()
            rcv_grey.changeSelect()
            mark = getString(R.string.gray)
        }

        ll_white.setOnClickListener {
            reset()
            rcv_white.changeSelect()
            mark = getString(R.string.white)
        }

        ll_yellow.setOnClickListener {
            reset()
            rcv_yellow.changeSelect()
            mark = getString(R.string.yellow)
        }

        ll_red.setOnClickListener {
            reset()
            rcv_red.changeSelect()
            mark = getString(R.string.red)
        }

        ll_brown.setOnClickListener {
            reset()
            rcv_brown.changeSelect()
            mark = getString(R.string.brown)
        }
        btn_next.setOnClickListener { finishActivityWithResultOK(mark) }
    }

    private fun finishActivityWithResultOK(soilColor: String?) {
        val intent = Intent()
        intent.putExtra(Constants.SOIL, soilColor)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun reset() {
        rcv_black.clearSelect()
        rcv_grey.clearSelect()
        rcv_white.clearSelect()
        rcv_yellow.clearSelect()
        rcv_red.clearSelect()
        rcv_brown.clearSelect()
    }

    companion object {
        const val REQUEST_CODE = 10089

        fun start(context: Activity, soilColor: String) {
            val intent = Intent(context, SoilColorActivity::class.java)
            intent.putExtra(
                String::class.java.canonicalName,
                Parcels.wrap(soilColor)
            )
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}
