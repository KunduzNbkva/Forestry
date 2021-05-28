package kg.forestry.ui.start

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.LocaleManager
import kg.forestry.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : BaseActivity<StartViewModel>(R.layout.activity_start,StartViewModel::class){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(LocaleManager.getLanguagePref(this)){
            LocaleManager.LANGUAGE_KEY_KYRGYZ -> rb_kg.isChecked = true
            LocaleManager.LANGUAGE_KEy_ENGLISH -> rb_en.isChecked = true
            else -> rb_ru.isChecked = true
        }
        radioGroup.setOnCheckedChangeListener { _, radioButton ->
            onLocalChanged(radioButton)
        }
        btn_next.setOnClickListener {
            AuthActivity.start(this)
        }
    }


    private fun onLocalChanged(checkedId: Int) {
        val lang = when (checkedId) {
            R.id.rb_kg -> LocaleManager.LANGUAGE_KEY_KYRGYZ
            R.id.rb_en -> LocaleManager.LANGUAGE_KEy_ENGLISH
            else -> LocaleManager.LANGUAGE_KEY_RUSSIAN
        }
        LocaleManager.setNewLocale(this,lang)
        recreate()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, StartActivity::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
