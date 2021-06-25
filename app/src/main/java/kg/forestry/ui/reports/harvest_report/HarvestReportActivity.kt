package kg.forestry.ui.reports.harvest_report

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.*
import kg.forestry.ui.reports.ReportViewModel
import kg.core.utils.gone
import kg.core.utils.visible
import kotlinx.android.synthetic.main.activity_harvest_report.*
import org.parceler.Parcels

class  HarvestReportActivity : BaseActivity<ReportViewModel>(R.layout.activity_harvest_report, ReportViewModel::class) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.report)
            setNavigationOnClickListener { onBackPressed() }
        }
        year2020.setChecked(true)

        year2019.setOnClickListener {
            clearButtonsStates(); year2019.setChecked(true)
            ll_report.gone()
        }
        year2020.setOnClickListener {
            clearButtonsStates(); year2020.setChecked(true)
            ll_report.visible()
        }
        year2021.setOnClickListener {
            clearButtonsStates(); year2021.setChecked(true)
            ll_report.gone()
        }
        year2022.setOnClickListener {
            clearButtonsStates(); year2022.setChecked(true)
            ll_report.gone()
        }
        parseDataFromIntent()
    }

    private fun parseDataFromIntent() {
        val harvest = Harvest::class.java.canonicalName
        Parcels.unwrap<Harvest>(intent.getParcelableExtra(harvest))?.let {
            setupViews(it)
        }

    }

    private fun clearButtonsStates() {
        year2019.setChecked(false)
        year2020.setChecked(false)
        year2021.setChecked(false)
        year2022.setChecked(false)
    }


    private fun setupViews(harvest: Harvest) {
        harvest.let {
            name_pasture.setTitle(it.pastureName!!)
            plot_name.setTitle(it.plotName!!)
            location.setTitle(it.harvLocation.getLocationAsString())
            date.setTitle(it.date!!)
            setupBarChart(it.sumWetBiomass)
            setupDryBarChart(it.sumDryBiomass)
        }
    }

    fun setupBarChart(sumWetBiomass: Biomass?) {
        val NoOfEmp = mutableListOf<BarEntry>()
        NoOfEmp.add(BarEntry(8f, sumWetBiomass?.eated?.toFloat()?:0f))
        NoOfEmp.add(BarEntry(10f, sumWetBiomass?.nonEated?.toFloat()?:0f))
        val bardataset = BarDataSet(NoOfEmp, null)
        val legend = barchart.legend
        barchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barchart.xAxis.isEnabled = false
        legend.isEnabled = false
        barchart.animateY(1000)
        val data = BarData(bardataset)
        bardataset.setColors(resources.getColor(R.color.light_blue),resources.getColor(R.color.light_magenta))
        barchart.data = data
    }

    fun setupDryBarChart(sumDryBiomass: Biomass?) {
        val NoOfEmp = mutableListOf<BarEntry>()
        NoOfEmp.add(BarEntry(8f, sumDryBiomass?.eated?.toFloat()?:0f))
        NoOfEmp.add(BarEntry(10f, sumDryBiomass?.nonEated?.toFloat()?:0f))
        val bardataset = BarDataSet(NoOfEmp, null)
        val legend = barchart_dry.legend
        legend.isEnabled = false
        barchart_dry.xAxis.isEnabled = false

        barchart_dry.animateY(1000)
        val data = BarData(bardataset)
        bardataset.setColors(resources.getColor(R.color.light_blue),resources.getColor(R.color.light_magenta))
        barchart_dry.data = data
    }


    companion object{
        fun start(context: Context, harvest: Harvest?=null){
            val intent = Intent(context, HarvestReportActivity::class.java)
            harvest?.let {
                intent.putExtra(
                    Harvest::class.java.canonicalName,
                    Parcels.wrap(harvest)
                )
            }
            context.startActivity(intent)
        }
    }
}

