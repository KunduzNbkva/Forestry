package kg.forestry.ui.choose_distance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kg.core.utils.Distance
import kg.core.utils.Side
import kg.forestry.ui.InputValueActivity
import kotlinx.android.synthetic.main.activity_choose_distance.*
import org.parceler.Parcels

class ChooseDistanceActivity  :
    BaseActivity<ChooseDistanceViewModel>(R.layout.activity_choose_distance, ChooseDistanceViewModel::class) {

    private var sideInfo = Side()
    private var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDataFromIntent()
        toolbar.apply {
            title = context.getString(R.string.distance)
            setNavigationOnClickListener { onBackPressed() }
        }
        btn_5.setOnClickListener {
            InputValueActivity.start(this,sideInfo.m5,5)
        }
        btn_10.setOnClickListener {
            InputValueActivity.start(this,sideInfo.m10,10)
        }
        btn_15.setOnClickListener {
            InputValueActivity.start(this,sideInfo.m15,15)
        }
        btn_20.setOnClickListener {
            InputValueActivity.start(this,sideInfo.m20,20)
        }
        btn_25.setOnClickListener {
            InputValueActivity.start(this,sideInfo.m25,25)
        }
        button.setOnClickListener { finishActivityWithResultOK(sideInfo,type) }
    }

    private fun finishActivityWithResultOK(side : Side?, type:Int){
        val intent = Intent()
        intent.putExtra(Constants.PLANT_CATALOG,side)
        intent.putExtra(Constants.LIST_ITEM_TYPE,type)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun parseDataFromIntent() {
        val name = Side::class.java.canonicalName
        Parcels.unwrap<Side>(intent.getParcelableExtra(name))?.let {
            sideInfo = it
        }
        type = intent.getIntExtra(Int::class.java.canonicalName,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                InputValueActivity.REQUEST_CODE -> {
                    val type = data?.getIntExtra(Constants.LIST_ITEM_TYPE,0)
                    val distance = data?.getSerializableExtra(Constants.PLANT_CATALOG) as Distance
                    when(type){
                        5 -> {
                            btn_5.setChecked(true)
                            sideInfo.m5 = distance
                        }
                        10 -> {
                            btn_10.setChecked(true)
                            sideInfo.m10 = distance
                        }
                        15 -> {
                            btn_15.setChecked(true)
                            sideInfo.m15 = distance
                        }
                        20 -> {
                            btn_20.setChecked(true)
                            sideInfo.m20 = distance
                        }
                        25 -> {
                            btn_25.setChecked(true)
                            sideInfo.m25 = distance
                        }

                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateViews(sideInfo)
        button.isEnabled = btn_5.getChecked()
                && btn_10.getChecked()
                && btn_15.getChecked()
                && btn_20.getChecked()
                && btn_25.getChecked()
    }

    private fun updateViews(side: Side) {
        btn_5.setChecked(side.m5.isValid())
        btn_10.setChecked(side.m10.isValid())
        btn_15.setChecked(side.m15.isValid())
        btn_20.setChecked(side.m20.isValid())
        btn_25.setChecked(side.m25.isValid())
    }

    companion object {
        const val REQUEST_CODE = 12000

        fun start(context: Activity, side: Side, type: Int) {
            val intent = Intent(context, ChooseDistanceActivity::class.java)
            intent.putExtra(
                Side::class.java.canonicalName,
                Parcels.wrap(side)
            )
            intent.putExtra(
                Int::class.java.canonicalName,
                type
            )
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

}

