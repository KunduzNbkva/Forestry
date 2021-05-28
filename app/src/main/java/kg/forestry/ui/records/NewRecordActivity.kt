package kg.forestry.ui.records

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kotlinx.android.synthetic.main.activity_new_record.*

class NewRecordActivity : BaseActivity<NewRecordViewModel>(R.layout.activity_new_record, NewRecordViewModel::class) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseRecordTypeFromIntent()
        setupViews()
    }

    private fun setupViews() {
        toolbar.apply {
            title = getString(R.string.new_value)
            setNavigationOnClickListener { onBackPressed() }
        }
        btn_next.setOnClickListener {
            vm.saveRecord(inp_value.getValue())
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun parseRecordTypeFromIntent() {
        vm.recordType = intent.getStringExtra(Constants.LIST_ITEM_TYPE)
    }


    companion object {
        const val REQUEST_CODE = 11111

        fun start(context: Activity,type: String) {
            val intent = Intent(context, NewRecordActivity::class.java)
            intent.putExtra(Constants.LIST_ITEM_TYPE,type)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}
