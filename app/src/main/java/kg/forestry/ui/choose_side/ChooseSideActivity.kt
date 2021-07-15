package kg.forestry.ui.choose_side

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.Constants
import kg.forestry.localstorage.model.Plant
import kg.core.utils.Side
import kg.forestry.ui.choose_distance.ChooseDistanceActivity
import kg.forestry.ui.plant.PlantsListActivity
import kotlinx.android.synthetic.main.activity_choose_side.*
import org.parceler.Parcels


class ChooseSideActivity : BaseActivity<ChooseSideViewModel>(R.layout.activity_choose_side, ChooseSideViewModel::class) {

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDataFromIntent()

        toolbar.apply {
            title = context.getString(R.string.direction)
            setNavigationOnClickListener { onBackPressed() }
        }
        btn_east.setOnClickListener {
            ChooseDistanceActivity.start(this, vm.plantInfo.eastSide, 0)
        }
        btn_west.setOnClickListener {
            ChooseDistanceActivity.start(this, vm.plantInfo.westSide, 1)

        }
        btn_north.setOnClickListener {
            ChooseDistanceActivity.start(this, vm.plantInfo.northSide, 2)
        }
        btn_south.setOnClickListener {
            ChooseDistanceActivity.start(this, vm.plantInfo.southSide, 3)

        }
        if (isEditMode){
            updateViews(vm.plantInfo)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ChooseDistanceActivity.REQUEST_CODE -> {
                    val type = data?.getIntExtra(Constants.LIST_ITEM_TYPE, 0)
                    val side = data?.getSerializableExtra(Constants.PLANT_CATALOG) as Side
                    when (type) {
                        0 -> {
                            btn_east.setChecked(true)
                            vm.plantInfo.eastSide = side
                        }
                        1 -> {
                            btn_west.setChecked(true)
                            vm.plantInfo.westSide = side
                        }
                        2 -> {
                            btn_north.setChecked(true)
                            vm.plantInfo.northSide = side
                        }
                        3 -> {
                            btn_south.setChecked(true)
                            vm.plantInfo.southSide = side
                        }
                    }
                }
            }
        }
    }

    private fun parseDataFromIntent() {
        val name = Plant::class.java.canonicalName
        Parcels.unwrap<Plant>(intent.getParcelableExtra(name))?.let {
            vm.plantInfo = it
        }
        isEditMode = intent.getBooleanExtra(Boolean::class.java.canonicalName,false)
    }

    override fun onResume() {
        super.onResume()
        updateViews()
    }

    private fun updateViews() {
        button.isEnabled = btn_west.getChecked()
                && btn_east.getChecked()
                && btn_north.getChecked()
                && btn_south.getChecked()

        button.setOnClickListener {
            vm.savePlant(isEditMode)
            PlantsListActivity.start(this)
        }
    }

    private fun updateViews(plant: Plant) {
        btn_north.setChecked(plant.northSide.isValid())
        btn_east.setChecked(plant.eastSide.isValid())
        btn_south.setChecked(plant.southSide.isValid())
        btn_west.setChecked(plant.westSide.isValid())
    }

    companion  object {
        const val REQUEST_CODE = 11119

        fun start(context: Activity, plant: Plant, isEditMode:Boolean?=false) {
            Log.e("THIS", "context is $context")
            val intent = Intent(context, ChooseSideActivity::class.java)
            intent.putExtra(
                Plant::class.java.canonicalName,
                Parcels.wrap(plant)
            )
            intent.putExtra(
                Boolean::class.java.canonicalName,
                isEditMode
            )
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}