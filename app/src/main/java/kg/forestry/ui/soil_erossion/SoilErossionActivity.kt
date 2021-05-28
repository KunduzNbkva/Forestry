package kg.forestry.ui.soil_erossion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.custom.MarkTextButton
import kg.core.utils.Constants
import kg.core.utils.Erosion
import kotlinx.android.synthetic.main.activity_soil_erossion.*
import org.parceler.Parcels

class SoilErossionActivity : BaseActivity<SoilErossionViewModel>(R.layout.activity_soil_erossion, SoilErossionViewModel::class) {

    private var erosion = Erosion()

    lateinit var no: String
    lateinit var few: String
    lateinit var several: String
    lateinit var many: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        no = getString(R.string.no)
        few = getString(R.string.bit)
        several = getString(R.string.few)
        many = getString(R.string.much)
        parseDataFromIntent()
        toolbar.apply {
            title = getString(R.string.erossion_degree)
            setNavigationOnClickListener { onBackPressed() }
        }
        setupViews()
        updateButton()

        mtb_1_1.setOnClickListener { clearMtb(1);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_1_2.setOnClickListener { clearMtb(1);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_1_3.setOnClickListener { clearMtb(1);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_1_4.setOnClickListener { clearMtb(1);(it as? MarkTextButton)?.setChecked(true);updateButton() }

        // ----
        mtb_2_1.setOnClickListener { clearMtb(2);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_2_2.setOnClickListener { clearMtb(2);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_2_3.setOnClickListener { clearMtb(2);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_2_4.setOnClickListener { clearMtb(2);(it as? MarkTextButton)?.setChecked(true);updateButton() }

        // ----
        mtb_3_1.setOnClickListener { clearMtb(3);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_3_2.setOnClickListener { clearMtb(3);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_3_3.setOnClickListener { clearMtb(3);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_3_4.setOnClickListener { clearMtb(3);(it as? MarkTextButton)?.setChecked(true);updateButton() }

        // ----
        mtb_4_1.setOnClickListener { clearMtb(4);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_4_2.setOnClickListener { clearMtb(4);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_4_3.setOnClickListener { clearMtb(4);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_4_4.setOnClickListener { clearMtb(4);(it as? MarkTextButton)?.setChecked(true);updateButton() }

        // ----
        mtb_5_1.setOnClickListener { clearMtb(5);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_5_2.setOnClickListener { clearMtb(5);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_5_3.setOnClickListener { clearMtb(5);(it as? MarkTextButton)?.setChecked(true);updateButton() }
        mtb_5_4.setOnClickListener { clearMtb(5);(it as? MarkTextButton)?.setChecked(true);updateButton() }

        button.setOnClickListener { saveValue() }
    }

    private fun updateButton() {
        button.isEnabled =
            (mtb_1_1.getChecked() || mtb_1_2.getChecked() || mtb_1_3.getChecked() || mtb_1_4.getChecked())
                    && (mtb_2_1.getChecked() || mtb_2_2.getChecked() || mtb_2_3.getChecked() || mtb_2_4.getChecked())
                    && (mtb_3_1.getChecked() || mtb_3_2.getChecked() || mtb_3_3.getChecked() || mtb_3_4.getChecked())
                    && (mtb_4_1.getChecked() || mtb_4_2.getChecked() || mtb_4_3.getChecked() || mtb_4_4.getChecked())
    }

    private fun parseDataFromIntent() {
        val name = Erosion::class.java.canonicalName
        Parcels.unwrap<Erosion>(intent.getParcelableExtra(name))?.let {
            erosion = it
        }
    }

    private fun saveValue() {
        erosion.scours = when {
            mtb_1_1.getChecked() -> no
            mtb_1_2.getChecked() -> few
            mtb_1_3.getChecked() -> several
            mtb_1_4.getChecked() -> many
            else -> ""
        }

        erosion.pedestal = when {
            mtb_2_1.getChecked() -> no
            mtb_2_2.getChecked() -> few
            mtb_2_3.getChecked() -> several
            mtb_2_4.getChecked() -> many
            else -> ""
        }

        erosion.cattle_trails = when {
            mtb_3_1.getChecked() -> no
            mtb_3_2.getChecked() -> few
            mtb_3_3.getChecked() -> several
            mtb_3_4.getChecked() -> many
            else -> ""
        }

        erosion.bare_areas = when {
            mtb_4_1.getChecked() -> no
            mtb_4_2.getChecked() -> few
            mtb_4_3.getChecked() -> several
            mtb_4_4.getChecked() -> many
            else -> ""
        }

        erosion.indicator = when {
            mtb_5_1.getChecked() -> no
            mtb_5_2.getChecked() -> few
            mtb_5_3.getChecked() -> several
            mtb_5_4.getChecked() -> many
            else -> ""
        }
        erosion.otherInfo = desc.getValue()
        finishActivityWithResultOK(erosion)
    }

    private fun finishActivityWithResultOK(soilColor: Erosion?) {
        val intent = Intent()
        intent.putExtra(Constants.SOIL, soilColor)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun clearMtb(i: Int) {
        when (i) {
            1 -> {
                mtb_1_1.setChecked(false)
                mtb_1_2.setChecked(false)
                mtb_1_3.setChecked(false)
                mtb_1_4.setChecked(false)
            }

            2 -> {
                mtb_2_1.setChecked(false)
                mtb_2_2.setChecked(false)
                mtb_2_3.setChecked(false)
                mtb_2_4.setChecked(false)
            }

            3 -> {
                mtb_3_1.setChecked(false)
                mtb_3_2.setChecked(false)
                mtb_3_3.setChecked(false)
                mtb_3_4.setChecked(false)
            }

            4 -> {
                mtb_4_1.setChecked(false)
                mtb_4_2.setChecked(false)
                mtb_4_3.setChecked(false)
                mtb_4_4.setChecked(false)
            }

            5 -> {
                mtb_5_1.setChecked(false)
                mtb_5_2.setChecked(false)
                mtb_5_3.setChecked(false)
                mtb_5_4.setChecked(false)
            }
        }
    }

    private fun setupViews() {
        (when (erosion.scours) {
            no -> mtb_1_1
            few -> mtb_1_2
            several -> mtb_1_3
            many -> mtb_1_4
            else -> null
        })?.setChecked(true)

        (when (erosion.pedestal) {
            no -> mtb_2_1
            few -> mtb_2_2
            several -> mtb_2_3
            many -> mtb_2_4
            else -> null
        })?.setChecked(true)

        (when (erosion.cattle_trails) {
            no -> mtb_3_1
            few -> mtb_3_2
            several -> mtb_3_3
            many -> mtb_3_4
            else -> null
        })?.setChecked(true)

        (when (erosion.bare_areas) {
            no -> mtb_4_1
            few -> mtb_4_2
            several -> mtb_4_3
            many -> mtb_4_4
            else -> null
        })?.setChecked(true)

        (when (erosion.indicator) {
            no -> mtb_5_1
            few -> mtb_5_2
            several -> mtb_5_3
            many -> mtb_5_4
            else -> null
        })?.setChecked(true)

        desc.setValue(erosion.otherInfo)
    }

    companion object {
        const val REQUEST_CODE = 16789

        fun start(context: Activity,erosion: Erosion) {
            val intent = Intent(context, SoilErossionActivity::class.java)
            intent.putExtra(
                Erosion::class.java.canonicalName,
                Parcels.wrap(erosion)
            )
            context.startActivityForResult(intent, REQUEST_CODE)
        }

    }
}
