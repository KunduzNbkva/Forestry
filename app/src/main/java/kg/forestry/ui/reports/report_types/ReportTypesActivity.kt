package kg.forestry.ui.reports.report_types

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.forestry.ui.harvest.HarvestListActivity
import kg.forestry.ui.plant.PlantsListActivity
import kg.forestry.ui.start.StartViewModel
import kotlinx.android.synthetic.main.activity_report_types.*

class ReportTypesActivity :  BaseActivity<StartViewModel>(R.layout.activity_report_types, StartViewModel::class){ // AppCompatActivity(R.layout.activity_report_types) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.report)
            setNavigationOnClickListener { onBackPressed() }
        }
        setupViews()
    }

    private fun setupViews() {

        this.resources.displayMetrics

        plants.setOnClickListener {
            PlantsListActivity.start(this,true)
        }
        harvest.setOnClickListener {
            HarvestListActivity.start(this,true)
        }
    }

    companion object{
        fun start(context: Context){
            val intent = Intent(context, ReportTypesActivity::class.java)
            context.startActivity(intent)
        }
    }
}
