package kg.forestry.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kg.forestry.ui.core.base.BaseActivity
import kg.core.custom.MarkImageButton
import kg.core.custom.MarkTextButton
import kg.core.utils.Constants
import kg.core.utils.NewDistance
import kg.forestry.R
import kg.forestry.ui.plant.plant_info.AddPlantViewModel
import kotlinx.android.synthetic.main.activity_input_value.*
import org.parceler.Parcels


class InputValueActivity : BaseActivity<AddPlantViewModel>(R.layout.activity_input_value, AddPlantViewModel::class) {
    private var emptyValue: String? = null
    private  var bushValue: String? = null
    private  var eatenValue: String? = null
    private  var nonEatenValue: String? = null
    private  var opadValue: String?= null
    private  var baseValue: String? = null
    private  var stoneValue: String? = null
    private  var plantHeightValue: String? = null
    private var distance : NewDistance? = null
    private var type = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDataFromIntent()
        toolbar.apply {
            title = getString(R.string.input_indicators)
            setNavigationOnClickListener { onBackPressed() }
        }
        onEmptyClickListener()
        onHeightClick()
        onEatenClickListener()
        onTreeClickListener()
        onNonEatenClickListener()
        onBaseClickListener()
        onOpadClickListener()
        onStoneClickListener()
        updateBtnSave()
    }

    private fun updateBtnSave() {
        if(!emptyValue.isNullOrEmpty()
            &&!bushValue.isNullOrEmpty()
            &&!eatenValue.isNullOrEmpty()
            &&!nonEatenValue.isNullOrEmpty()
            &&!opadValue.isNullOrEmpty()
            &&!baseValue.isNullOrEmpty()
            &&!stoneValue.isNullOrEmpty()
            &&!plantHeightValue.isNullOrEmpty()){
           button.isEnabled = true
        }
        button.setOnClickListener {
            distance = NewDistance(
                emptyValue,
                bushValue,
                eatenValue,
                nonEatenValue,
                opadValue,
                stoneValue,
                baseValue,
                plantHeightValue
            )
            Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
            finishActivityWithResultOK(distance!!, type)
        }
        updateBtnHeight()
    }

    private fun finishActivityWithResultOK(distance: NewDistance, type: Int){
        val intent = Intent()
        intent.putExtra(Constants.PLANT_CATALOG, distance)
        intent.putExtra(Constants.LIST_ITEM_TYPE, type)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun parseDataFromIntent() {
        val name = NewDistance::class.java.canonicalName
        Parcels.unwrap<NewDistance>(intent.getParcelableExtra(name))?.let {
            distance = it
        }
        type = intent.getIntExtra(Int::class.java.canonicalName, 0)
    }


    private fun updateBtnHeight() {
        if(distance?.isValid() == true){
            val isEnabled = distance!!.isValid()
            btn_height_10.isEnabled = isEnabled
            btn_height_30.isEnabled = isEnabled
            btn_height_50.isEnabled = isEnabled
            btn_height_70.isEnabled = isEnabled
            btn_height_90.isEnabled = isEnabled
            btn_height_other.isEnabled = isEnabled
            if (!isEnabled) distance!!.plant_height = ""
        }

    }


    private fun onHeightClick(){
        val height = View.OnClickListener { v -> setupHeight(v as MarkTextButton);updateBtnSave() }
        btn_height_10.setOnClickListener(height)
        btn_height_30.setOnClickListener(height)
        btn_height_50.setOnClickListener(height)
        btn_height_70.setOnClickListener(height)
        btn_height_90.setOnClickListener(height)
        btn_height_other.setOnClickListener(height)
    }

    private fun setupHeight(btn: MarkTextButton) {
        btn_height_10.setChecked(false)
        btn_height_30.setChecked(false)
        btn_height_50.setChecked(false)
        btn_height_70.setChecked(false)
        btn_height_90.setChecked(false)
        btn_height_other.setChecked(false)

        btn.setChecked(true)

        plantHeightValue = btn.getLable()
    }


    private fun onEmptyClickListener(){
        val empty = View.OnClickListener { v -> setUpEmpty(v as MarkImageButton);updateBtnSave() }
        btn_empty_0.setOnClickListener(empty)
        btn_empty_10.setOnClickListener(empty)
        btn_empty_30.setOnClickListener(empty)
        btn_empty_50.setOnClickListener(empty)
        btn_empty_70.setOnClickListener(empty)
        btn_empty_90.setOnClickListener(empty)
    }

    private fun setUpEmpty(btn: MarkImageButton){
        btn_empty_0.setChecked(false)
        btn_empty_10.setChecked(false)
        btn_empty_30.setChecked(false)
        btn_empty_50.setChecked(false)
        btn_empty_70.setChecked(false)
        btn_empty_90.setChecked(false)

        btn.setChecked(true)
        getEmptyLabel(btn)
    }

    private fun getEmptyLabel(btn: MarkImageButton){
        when(btn.getChecked()){
            btn_empty_0.getChecked() -> emptyValue = getString(R.string.not_observe)
            btn_empty_10.getChecked() -> emptyValue = getString(R.string.ten)
            btn_empty_30.getChecked() -> emptyValue = getString(R.string.thirty)
            btn_empty_50.getChecked() -> emptyValue = getString(R.string.fifty)
            btn_empty_70.getChecked() -> emptyValue = getString(R.string.seventy)
            btn_empty_90.getChecked() -> emptyValue = getString(R.string.ninety)
            else -> emptyValue = "null"
        }
    }


    private fun onEatenClickListener(){
        val eaten = View.OnClickListener { v -> setUpEaten(v as MarkImageButton);updateBtnSave() }
        btn_bush_0.setOnClickListener(eaten)
        btn_bush_10.setOnClickListener(eaten)
        btn_bush_30.setOnClickListener(eaten)
        btn_bush_50.setOnClickListener(eaten)
        btn_bush_70.setOnClickListener(eaten)
        btn_bush_90.setOnClickListener(eaten)
    }

    private fun setUpEaten(btn: MarkImageButton){
        btn_bush_0.setChecked(false)
        btn_bush_10.setChecked(false)
        btn_bush_30.setChecked(false)
        btn_bush_50.setChecked(false)
        btn_bush_70.setChecked(false)
        btn_bush_90.setChecked(false)
        btn.setChecked(true)
        getEatenLabel(btn)
    }

    private fun getEatenLabel(btn: MarkImageButton){
        when(btn.getChecked()){
            btn_bush_0.getChecked() -> eatenValue = getString(R.string.not_observe)
            btn_bush_10.getChecked() -> eatenValue = getString(R.string.ten)
            btn_bush_30.getChecked() -> eatenValue = getString(R.string.thirty)
            btn_bush_50.getChecked() -> eatenValue = getString(R.string.fifty)
            btn_bush_70.getChecked() -> eatenValue = getString(R.string.seventy)
            btn_bush_90.getChecked() -> eatenValue = getString(R.string.ninety)
            else -> eatenValue = "null"
        }
    }


    private fun onTreeClickListener(){
        val tree = View.OnClickListener { v -> setUpTree(v as MarkImageButton);updateBtnSave() }
        btn_tree_0.setOnClickListener(tree)
        btn_tree_10.setOnClickListener(tree)
        btn_tree_30.setOnClickListener(tree)
        btn_tree_50.setOnClickListener(tree)
        btn_tree_70.setOnClickListener(tree)
        btn_tree_90.setOnClickListener(tree)
    }

    private fun setUpTree(btn: MarkImageButton){
        btn_tree_0.setChecked(false)
        btn_tree_10.setChecked(false)
        btn_tree_30.setChecked(false)
        btn_tree_50.setChecked(false)
        btn_tree_70.setChecked(false)
        btn_tree_90.setChecked(false)
        btn.setChecked(true)
        getTreeLabel(btn)
    }

    private fun getTreeLabel(btn: MarkImageButton){
        when(btn.getChecked()){
            btn_tree_0.getChecked() -> bushValue = getString(R.string.not_observe)
            btn_tree_10.getChecked() -> bushValue = getString(R.string.ten)
            btn_tree_30.getChecked() -> bushValue = getString(R.string.thirty)
            btn_tree_50.getChecked() -> bushValue = getString(R.string.fifty)
            btn_tree_70.getChecked() -> bushValue = getString(R.string.seventy)
            btn_tree_90.getChecked() -> bushValue = getString(R.string.ninety)
            else -> bushValue = "null"
        }
    }


    private fun onNonEatenClickListener(){
        val nonEaten = View.OnClickListener { v -> setUpNonEaten(v as MarkImageButton);updateBtnSave() }
        btn_grass_0.setOnClickListener(nonEaten)
        btn_grass_10.setOnClickListener(nonEaten)
        btn_grass_30.setOnClickListener(nonEaten)
        btn_grass_50.setOnClickListener(nonEaten)
        btn_grass_70.setOnClickListener(nonEaten)
        btn_grass_90.setOnClickListener(nonEaten)
    }

    private fun setUpNonEaten(btn: MarkImageButton){
        btn_grass_0.setChecked(false)
        btn_grass_10.setChecked(false)
        btn_grass_30.setChecked(false)
        btn_grass_50.setChecked(false)
        btn_grass_70.setChecked(false)
        btn_grass_90.setChecked(false)
        btn.setChecked(true)
        getNonEatenLabel(btn)
    }

    private fun getNonEatenLabel(btn: MarkImageButton){
        when(btn.getChecked()){
            btn_grass_0.getChecked() -> nonEatenValue = getString(R.string.not_observe)
            btn_grass_10.getChecked() -> nonEatenValue = getString(R.string.ten)
            btn_grass_30.getChecked() -> nonEatenValue = getString(R.string.thirty)
            btn_grass_50.getChecked() -> nonEatenValue = getString(R.string.fifty)
            btn_grass_70.getChecked() -> nonEatenValue = getString(R.string.seventy)
            btn_grass_90.getChecked() -> nonEatenValue = getString(R.string.ninety)
            else -> nonEatenValue = "null"
        }
    }


    private fun onBaseClickListener(){
        val base = View.OnClickListener { v -> setBase(v as MarkImageButton);updateBtnSave() }
        btn_base_0.setOnClickListener(base)
        btn_base_10.setOnClickListener(base)
        btn_base_30.setOnClickListener(base)
        btn_base_50.setOnClickListener(base)
        btn_base_70.setOnClickListener(base)
        btn_base_90.setOnClickListener(base)
    }

    private fun setBase(btn: MarkImageButton){
        btn_base_0.setChecked(false)
        btn_base_10.setChecked(false)
        btn_base_30.setChecked(false)
        btn_base_50.setChecked(false)
        btn_base_70.setChecked(false)
        btn_base_90.setChecked(false)
        btn.setChecked(true)
        getBaseLabel(btn)
    }

    private fun getBaseLabel(btn: MarkImageButton){
        when(btn.getChecked()){
            btn_base_0.getChecked() -> baseValue = getString(R.string.not_observe)
            btn_base_10.getChecked() -> baseValue = getString(R.string.ten)
            btn_base_30.getChecked() -> baseValue = getString(R.string.thirty)
            btn_base_50.getChecked() -> baseValue = getString(R.string.fifty)
            btn_base_70.getChecked() -> baseValue = getString(R.string.seventy)
            btn_base_90.getChecked() -> baseValue = getString(R.string.ninety)
            else -> baseValue = "null"
        }
    }


    private fun onOpadClickListener(){
        val opad = View.OnClickListener { v -> setUpOpad(v as MarkImageButton);updateBtnSave() }
        btn_wind_0.setOnClickListener(opad)
        btn_wind_10.setOnClickListener(opad)
        btn_wind_30.setOnClickListener(opad)
        btn_wind_50.setOnClickListener(opad)
        btn_wind_70.setOnClickListener(opad)
        btn_wind_90.setOnClickListener(opad)
    }

    private fun setUpOpad(btn: MarkImageButton){
        btn_wind_0.setChecked(false)
        btn_wind_10.setChecked(false)
        btn_wind_30.setChecked(false)
        btn_wind_50.setChecked(false)
        btn_wind_70.setChecked(false)
        btn_wind_90.setChecked(false)
        btn.setChecked(true)
        getOpadLabel(btn)
    }

    private fun getOpadLabel(btn: MarkImageButton){
        when(btn.getChecked()){
           btn_wind_0.getChecked() -> opadValue = getString(R.string.not_observe)
            btn_wind_10.getChecked() -> opadValue = getString(R.string.ten)
            btn_wind_30.getChecked() -> opadValue = getString(R.string.thirty)
            btn_wind_50.getChecked() -> opadValue = getString(R.string.fifty)
            btn_wind_70.getChecked() -> opadValue = getString(R.string.seventy)
            btn_wind_90.getChecked() -> opadValue = getString(R.string.ninety)
            else -> opadValue = "null"
        }
    }


    private fun onStoneClickListener(){
        val stone = View.OnClickListener { v -> setUpStone(v as MarkImageButton);updateBtnSave() }
        btn_stone_0.setOnClickListener(stone)
        btn_stone_10.setOnClickListener(stone)
        btn_stone_30.setOnClickListener(stone)
        btn_stone_50.setOnClickListener(stone)
        btn_stone_70.setOnClickListener(stone)
        btn_stone_90.setOnClickListener(stone)
    }

    private fun setUpStone(btn: MarkImageButton){
        btn_stone_0.setChecked(false)
        btn_stone_10.setChecked(false)
        btn_stone_30.setChecked(false)
        btn_stone_50.setChecked(false)
        btn_stone_70.setChecked(false)
        btn_stone_90.setChecked(false)
        btn.setChecked(true)
        getStoneLabel(btn)
    }

    private fun getStoneLabel(btn: MarkImageButton){
        when(btn.getChecked()){
            btn_stone_0.getChecked() -> stoneValue = getString(R.string.not_observe)
            btn_stone_10.getChecked() -> stoneValue = getString(R.string.ten)
            btn_stone_30.getChecked() -> stoneValue = getString(R.string.thirty)
            btn_stone_50.getChecked() -> stoneValue = getString(R.string.fifty)
            btn_stone_70.getChecked() -> stoneValue = getString(R.string.seventy)
            btn_stone_90.getChecked() -> stoneValue = getString(R.string.ninety)
            else -> stoneValue = "null"
        }
    }

    override fun onResume() {
        super.onResume()
            when (distance?.empty) {
                getString(R.string.not_observe) -> btn_empty_0
                getString(R.string.ten) -> btn_empty_10
                getString(R.string.thirty) -> btn_empty_30
                getString(R.string.fifty) -> btn_empty_50
                getString(R.string.seventy) -> btn_empty_70
                getString(R.string.ninety) -> btn_empty_90
                else -> null
            }?.setChecked(true)

            when (distance?.eatenPlant) {
                getString(R.string.not_observe) -> btn_bush_0
                getString(R.string.ten) ->  btn_bush_10
                getString(R.string.thirty) -> btn_bush_30
                getString(R.string.fifty) -> btn_bush_50
                getString(R.string.seventy) -> btn_bush_70
                getString(R.string.ninety) -> btn_bush_90
                else -> null
            }?.setChecked(true)

            when (distance?.nonEatenPlant) {
                getString(R.string.not_observe) -> btn_grass_0
                getString(R.string.ten) ->  btn_grass_10
                getString(R.string.thirty) -> btn_grass_30
                getString(R.string.fifty) ->btn_grass_50
                getString(R.string.seventy) -> btn_grass_70
                getString(R.string.ninety) -> btn_grass_90
                else -> null
            }?.setChecked(true)

            when (distance?.bush) {
                getString(R.string.not_observe) ->  btn_tree_0
                getString(R.string.ten) ->   btn_tree_10
                getString(R.string.thirty) ->  btn_tree_30
                getString(R.string.fifty) -> btn_tree_50
                getString(R.string.seventy) ->  btn_tree_70
                getString(R.string.ninety) ->  btn_tree_90
                else -> null
            }?.setChecked(true)

            when (distance?.opad) {
                getString(R.string.not_observe) ->  btn_wind_0
                getString(R.string.ten) -> btn_wind_10
                getString(R.string.thirty) ->  btn_wind_30
                getString(R.string.fifty) -> btn_wind_50
                getString(R.string.seventy) ->  btn_wind_70
                getString(R.string.ninety) ->  btn_wind_90
                else -> null
            }?.setChecked(true)

            when (distance?.stone) {
                getString(R.string.not_observe) ->  btn_stone_0
                getString(R.string.ten) -> btn_stone_10
                getString(R.string.thirty) ->  btn_stone_30
                getString(R.string.fifty) -> btn_stone_50
                getString(R.string.seventy) ->  btn_stone_70
                getString(R.string.ninety) ->  btn_stone_90
                else -> null
            }?.setChecked(true)


        when (distance?.base) {
            getString(R.string.not_observe) ->  btn_base_0
            getString(R.string.ten) -> btn_base_10
            getString(R.string.thirty) ->  btn_base_30
            getString(R.string.fifty) -> btn_base_50
            getString(R.string.seventy) ->  btn_base_70
            getString(R.string.ninety) ->  btn_base_90
            else -> null
        }?.setChecked(true)

        when (distance?.plant_height) {
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

        fun start(context: Activity, plant: NewDistance, type: Int) {
            val intent = Intent(context, InputValueActivity::class.java)
            intent.putExtra(
                NewDistance::class.java.canonicalName,
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
