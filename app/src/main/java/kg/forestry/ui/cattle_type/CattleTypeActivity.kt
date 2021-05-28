package kg.forestry.ui.cattle_type

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kotlinx.android.synthetic.main.activity_cattle_type.*

class CattleTypeActivity :
    BaseActivity<CattleTypeViewNodel>(R.layout.activity_cattle_type, CattleTypeViewNodel::class) {

    private var types = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.cattle_type)
            setNavigationOnClickListener { onBackPressed() }
        }
        parseDataFromIntent()
        setupViews()
        initClickListeners()
    }

    private fun setupViews() {
        if (types.contains(getString(R.string.cows_valid), true)) btn_cows.setChecked(true)
        if (types.contains(getString(R.string.sheeps_valid), true)) btn_sheeps.setChecked(true)
        if (types.contains(getString(R.string.horses_valid), true)) btn_horses.setChecked(true)
        if (types.contains(getString(R.string.yaks_valid), true)) btn_yaks.setChecked(true)
    }

    private fun parseDataFromIntent() {
        val name = String::class.java.canonicalName
        val intent = intent.getStringExtra(name)
        if (intent != null) {
            types = intent
        }
    }

    private fun initClickListeners() {
        btn_cows.setOnClickListener { btn_cows.setChecked(!btn_cows.getChecked()) }
        btn_sheeps.setOnClickListener { btn_sheeps.setChecked(!btn_sheeps.getChecked()) }
        btn_horses.setOnClickListener { btn_horses.setChecked(!btn_horses.getChecked()) }
        btn_yaks.setOnClickListener { btn_yaks.setChecked(!btn_yaks.getChecked()) }
        btn_next.setOnClickListener {
            types = " "
            if (btn_cows.getChecked()) types += " " + getString(R.string.cows)
            if (btn_sheeps.getChecked()) types += " " + getString(R.string.sheeps)
            if (btn_horses.getChecked()) types += " " + getString(R.string.horses)
            if (btn_yaks.getChecked()) types += " " + getString(R.string.yaks)
            if (types.isNotEmpty()) types = types.substring(0, types.count() - 1)
            finishActivityWithResultOK(types)
        }
    }

    private fun finishActivityWithResultOK(cattleTypes: String?) {
        val intent = Intent()
        intent.putExtra(Constants.PASTURES, cattleTypes)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val REQUEST_CODE = 10019

        fun start(context: Activity, cattleTypes: String) {
            val intent = Intent(context, CattleTypeActivity::class.java)
            intent.putExtra(String::class.java.canonicalName, cattleTypes)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}
