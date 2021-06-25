package kg.forestry.ui.biomass

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kg.forestry.R
import kg.core.utils.Constants
import kg.core.utils.setVisible
import kg.core.utils.Biomass
import kg.core.utils.BiomassType
import kg.forestry.ui.core.base.BaseActivity
import kotlinx.android.synthetic.main.activity_biomass.*
import java.util.*

class BiomassActivity : BaseActivity<BiomassViewModel>(R.layout.activity_biomass, BiomassViewModel::class) {

    private val biomass = Biomass(0, 0)
    private var biomassType:String ?= null
    private var wetBiomassSum: Int = 0
    private var dryBiomassSum: Int = 0

    private var wetBiomassEaten = 0
    private var wetBiomassNonEaten = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biomassType = intent.getStringExtra(Constants.BIOMASS_TYPE)
        wetBiomassSum = intent.getIntExtra(Constants.BIOMASS_SUM,0)
        dryBiomassSum = intent.getIntExtra(Constants.DRY_BIOMASS_SUM,0)
        wetBiomassEaten = intent.getIntExtra(Constants.BIOMASS_EATEN,0)
        wetBiomassNonEaten = intent.getIntExtra(Constants.BIOMASS_NON_EATEN,0)
        toolbar.apply {
            title = when(isWetBiomass()){
                true -> getString(R.string.wet_biomass)
                else -> getString(R.string.dry_biomass)
            }
            setNavigationOnClickListener { onBackPressed() }
        }
        setupViews()
        nextClick()
    }

    private fun isWetBiomass() = biomassType == BiomassType.WET.value

    private fun setupViews() {
        tv_dry_biomass_info.setVisible(!isWetBiomass())
        btn_add_reminder.setVisible(isWetBiomass())

        btn_add_reminder.setOnClickListener {
            addCalendarEvent()
        }
    }

    private fun nextClick(){
        btn_next.setOnClickListener {
            biomass.eated = inp_eat.getValue().trim().toInt()
            biomass.nonEated = inp_noneat.getValue().trim().toInt()

            if(isWetBiomass() && biomass.getSum() < dryBiomassSum){
                showInvalidRelationAlert(getString(R.string.wet_cant_be_more_than_dry))
            }else if(!isWetBiomass() && biomass.getSum() > wetBiomassSum){
                showInvalidRelationAlert(getString(R.string.dry_cant_be_more_than_wet))
            }
            else{
                finishActivityWithResultOK(biomass,biomassType)
            }
        }
    }

    private fun addCalendarEvent() {
        val calendarEvent: Calendar = Calendar.getInstance()
        val intent = Intent(Intent.ACTION_EDIT)
        calendarEvent.add(Calendar.DAY_OF_YEAR, 15)

        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra("beginTime", calendarEvent.timeInMillis)
        intent.putExtra("allDay", true)
        intent.putExtra("rule", "FREQ=YEARLY")
        intent.putExtra("endTime", calendarEvent.timeInMillis)
        intent.putExtra("title", "Кургак биомассаны өлчөө - Нужно взвесить сухую биомассу")
        startActivity(intent)
    }

    private fun showInvalidRelationAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create().show()
    }

    private fun finishActivityWithResultOK(biomassSum:Biomass, biomassType: String?){
        val intent = Intent()
        intent.putExtra(Constants.BIOMASS_SUM, biomassSum)
        intent.putExtra(Constants.BIOMASS_TYPE,biomassType)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val REQUEST_CODE = 10007

        fun start(context: Activity,biomassType: String,wetBiomass:Int = 0,dryBiomass:Int = 0, eatW: Int = 0, nEatW: Int = 0) {
            val intent = Intent(context, BiomassActivity::class.java)
            intent.putExtra(Constants.BIOMASS_SUM,wetBiomass)
            intent.putExtra(Constants.DRY_BIOMASS_SUM,dryBiomass)
            intent.putExtra(Constants.BIOMASS_TYPE,biomassType)
            intent.putExtra(Constants.BIOMASS_EATEN, eatW)
            intent.putExtra(Constants.BIOMASS_NON_EATEN, nEatW)

            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

}
