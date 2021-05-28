package kg.forestry.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.custom.MarkImageButton
import kg.core.custom.MarkTextButton
import kg.core.utils.Constants
import kg.core.utils.Distance
import kg.core.utils.TypeInDistance
import kg.forestry.ui.plant.plant_info.AddPlantViewModel
import kotlinx.android.synthetic.main.activity_input_value.*
import org.parceler.Parcels

class InputValueActivity :
    BaseActivity<AddPlantViewModel>(R.layout.activity_input_value, AddPlantViewModel::class) {
    var distance = Distance()
    var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDataFromIntent()
        val listener10 = View.OnClickListener { v -> setup10(v as MarkImageButton);updateBtnSave() }
        btn_empty_10.setOnClickListener(listener10)
        btn_tree_10.setOnClickListener(listener10)
        btn_bush_10.setOnClickListener(listener10)
        btn_grass_10.setOnClickListener(listener10)
        btn_base_10.setOnClickListener(listener10)
        btn_wind_10.setOnClickListener(listener10)
        btn_stone_10.setOnClickListener(listener10)

        val listener30 = View.OnClickListener { v -> setup30(v as MarkImageButton);updateBtnSave() }
        btn_empty_30.setOnClickListener(listener30)
        btn_tree_30.setOnClickListener(listener30)
        btn_bush_30.setOnClickListener(listener30)
        btn_grass_30.setOnClickListener(listener30)
        btn_base_30.setOnClickListener(listener30)
        btn_wind_30.setOnClickListener(listener30)
        btn_stone_30.setOnClickListener(listener30)

        val listener50 = View.OnClickListener { v -> setup50(v as MarkImageButton);updateBtnSave() }
        btn_empty_50.setOnClickListener(listener50)
        btn_tree_50.setOnClickListener(listener50)
        btn_bush_50.setOnClickListener(listener50)
        btn_grass_50.setOnClickListener(listener50)
        btn_base_50.setOnClickListener(listener50)
        btn_wind_50.setOnClickListener(listener50)
        btn_stone_50.setOnClickListener(listener50)

        val listener70 = View.OnClickListener { v -> setup70(v as MarkImageButton);updateBtnSave() }
        btn_empty_70.setOnClickListener(listener70)
        btn_tree_70.setOnClickListener(listener70)
        btn_bush_70.setOnClickListener(listener70)
        btn_grass_70.setOnClickListener(listener70)
        btn_base_70.setOnClickListener(listener70)
        btn_wind_70.setOnClickListener(listener70)
        btn_stone_70.setOnClickListener(listener70)

        val listener90 = View.OnClickListener { v -> setup90(v as MarkImageButton);updateBtnSave() }
        btn_empty_90.setOnClickListener(listener90)
        btn_tree_90.setOnClickListener(listener90)
        btn_bush_90.setOnClickListener(listener90)
        btn_grass_90.setOnClickListener(listener90)
        btn_base_90.setOnClickListener(listener90)
        btn_wind_90.setOnClickListener(listener90)
        btn_stone_90.setOnClickListener(listener90)

        val height = View.OnClickListener { v -> setupHeight(v as MarkTextButton);updateBtnSave() }
        btn_height_10.setOnClickListener(height)
        btn_height_30.setOnClickListener(height)
        btn_height_50.setOnClickListener(height)
        btn_height_70.setOnClickListener(height)
        btn_height_90.setOnClickListener(height)
        btn_height_other.setOnClickListener(height)

        updateBtnSave()
    }

    private fun updateBtnSave() {
        button.isEnabled = distance.isValid()
        updateBtnHeight()
        button.setOnClickListener {
            finishActivityWithResultOK(distance,type)
        }
    }

    private fun finishActivityWithResultOK(distance : Distance?, type:Int){
        val intent = Intent()
        intent.putExtra(Constants.PLANT_CATALOG,distance)
        intent.putExtra(Constants.LIST_ITEM_TYPE,type)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun parseDataFromIntent() {
        val name = Distance::class.java.canonicalName
        Parcels.unwrap<Distance>(intent.getParcelableExtra(name))?.let {
            distance = it
        }
        type = intent.getIntExtra(Int::class.java.canonicalName,0)
    }

    private fun updateBtnHeight() {
        val isEnabled = distance.isNeedHeight()
        btn_height_10.isEnabled = isEnabled
        btn_height_30.isEnabled = isEnabled
        btn_height_50.isEnabled = isEnabled
        btn_height_70.isEnabled = isEnabled
        btn_height_90.isEnabled = isEnabled
        btn_height_other.isEnabled = isEnabled
        if (!isEnabled) distance.plant_height = ""
    }

    private fun setupHeight(btn: MarkTextButton) {
        btn_height_10.setChecked(false)
        btn_height_30.setChecked(false)
        btn_height_50.setChecked(false)
        btn_height_70.setChecked(false)
        btn_height_90.setChecked(false)
        btn_height_other.setChecked(false)

        btn.setChecked(true)

        distance.plant_height = btn.getLable()
    }

    fun setup10(btn: MarkImageButton) {
        btn_empty_10.setChecked(false)
        btn_tree_10.setChecked(false)
        btn_bush_10.setChecked(false)
        btn_grass_10.setChecked(false)
        btn_base_10.setChecked(false)
        btn_wind_10.setChecked(false)
        btn_stone_10.setChecked(false)

        btn.setChecked(true)
        distance.d10 = getTypeByView(btn)
    }

    fun setup30(btn: MarkImageButton) {
        btn_empty_30.setChecked(false)
        btn_tree_30.setChecked(false)
        btn_bush_30.setChecked(false)
        btn_grass_30.setChecked(false)
        btn_base_30.setChecked(false)
        btn_wind_30.setChecked(false)
        btn_stone_30.setChecked(false)

        btn.setChecked(true)
        distance.d30 = getTypeByView(btn)
    }

    fun setup50(btn: MarkImageButton) {
        btn_empty_50.setChecked(false)
        btn_tree_50.setChecked(false)
        btn_bush_50.setChecked(false)
        btn_grass_50.setChecked(false)
        btn_base_50.setChecked(false)
        btn_wind_50.setChecked(false)
        btn_stone_50.setChecked(false)

        btn.setChecked(true)
        distance.d50 = getTypeByView(btn)
    }

    fun setup70(btn: MarkImageButton) {
        btn_empty_70.setChecked(false)
        btn_tree_70.setChecked(false)
        btn_bush_70.setChecked(false)
        btn_grass_70.setChecked(false)
        btn_base_70.setChecked(false)
        btn_wind_70.setChecked(false)
        btn_stone_70.setChecked(false)

        btn.setChecked(true)
        distance.d70 = getTypeByView(btn)
    }

    fun setup90(btn: MarkImageButton) {
        btn_empty_90.setChecked(false)
        btn_tree_90.setChecked(false)
        btn_bush_90.setChecked(false)
        btn_grass_90.setChecked(false)
        btn_base_90.setChecked(false)
        btn_wind_90.setChecked(false)
        btn_stone_90.setChecked(false)

        btn.setChecked(true)
        distance.d90 = getTypeByView(btn)
    }

    fun getTypeByView(btn: MarkImageButton): String {
        return when (btn) {
            btn_empty_10, btn_empty_30, btn_empty_50, btn_empty_70, btn_empty_90 -> TypeInDistance.EMPTY
            btn_tree_10, btn_tree_30, btn_tree_50, btn_tree_70, btn_tree_90 -> TypeInDistance.TREE
            btn_bush_10, btn_bush_30, btn_bush_50, btn_bush_70, btn_bush_90 -> TypeInDistance.BUSH
            btn_grass_10, btn_grass_30, btn_grass_50, btn_grass_70, btn_grass_90 -> TypeInDistance.GRASS
            btn_base_10, btn_base_30, btn_base_50, btn_base_70, btn_base_90 -> TypeInDistance.BASE
            btn_wind_10, btn_wind_30, btn_wind_50, btn_wind_70, btn_wind_90 -> TypeInDistance.WIND
            btn_stone_10, btn_stone_30, btn_stone_50, btn_stone_70, btn_stone_90 -> TypeInDistance.STONE
            else -> TypeInDistance.NULL
        }.name
    }

    override fun onResume() {
        super.onResume()
        when (TypeInDistance.valueOf(distance.d10)) {
            TypeInDistance.EMPTY -> btn_empty_10
            TypeInDistance.TREE -> btn_tree_10
            TypeInDistance.BUSH -> btn_bush_10
            TypeInDistance.GRASS -> btn_grass_10
            TypeInDistance.BASE -> btn_base_10
            TypeInDistance.WIND -> btn_wind_10
            TypeInDistance.STONE -> btn_stone_10
            else -> null
        }?.setChecked(true)

        when (TypeInDistance.valueOf(distance.d30)) {
            TypeInDistance.EMPTY -> btn_empty_30
            TypeInDistance.TREE -> btn_tree_30
            TypeInDistance.BUSH -> btn_bush_30
            TypeInDistance.GRASS -> btn_grass_30
            TypeInDistance.BASE -> btn_base_30
            TypeInDistance.WIND -> btn_wind_30
            TypeInDistance.STONE -> btn_stone_30
            else -> null
        }?.setChecked(true)

        when (TypeInDistance.valueOf(distance.d50)) {
            TypeInDistance.EMPTY -> btn_empty_50
            TypeInDistance.TREE -> btn_tree_50
            TypeInDistance.BUSH -> btn_bush_50
            TypeInDistance.GRASS -> btn_grass_50
            TypeInDistance.BASE -> btn_base_50
            TypeInDistance.WIND -> btn_wind_50
            TypeInDistance.STONE -> btn_stone_50
            else -> null
        }?.setChecked(true)

        when (TypeInDistance.valueOf(distance.d70)) {
            TypeInDistance.EMPTY -> btn_empty_70
            TypeInDistance.TREE -> btn_tree_70
            TypeInDistance.BUSH -> btn_bush_70
            TypeInDistance.GRASS -> btn_grass_70
            TypeInDistance.BASE -> btn_base_70
            TypeInDistance.WIND -> btn_wind_70
            TypeInDistance.STONE -> btn_stone_70
            else -> null
        }?.setChecked(true)

        when (TypeInDistance.valueOf(distance.d90)) {
            TypeInDistance.EMPTY -> btn_empty_90
            TypeInDistance.TREE -> btn_tree_90
            TypeInDistance.BUSH -> btn_bush_90
            TypeInDistance.GRASS -> btn_grass_90
            TypeInDistance.BASE -> btn_base_90
            TypeInDistance.WIND -> btn_wind_90
            TypeInDistance.STONE -> btn_stone_90
            else -> null
        }?.setChecked(true)

        when (distance.plant_height) {
            btn_height_10.getLable() -> btn_height_10
            btn_height_30.getLable() -> btn_height_30
            btn_height_50.getLable() -> btn_height_50
            btn_height_70.getLable() -> btn_height_70
            btn_height_90.getLable() -> btn_height_90
            else -> null
        }?.setChecked(true)
    }


    companion object {
        const val REQUEST_CODE = 12888

        fun start(context: Activity, plant: Distance, type : Int) {
            val intent = Intent(context, InputValueActivity::class.java)
            intent.putExtra(
                Distance::class.java.canonicalName,
                Parcels.wrap(plant)
            )
            intent.putExtra(
                Int::class.java.canonicalName,
                type
            )
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}
