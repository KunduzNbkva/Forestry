package kg.forestry.ui.about

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.forestry.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.activity_about_app.*

class AboutAppActivity: BaseActivity<AuthViewModel>(R.layout.activity_about_app, AuthViewModel::class) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_about_app)
        toolbar.apply {
            title = context.getString(R.string.about_app)
            setNavigationOnClickListener { onBackPressed() }
        }

        tv_camp.movementMethod = LinkMovementMethod.getInstance()
    }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, AboutAppActivity::class.java)
            context.startActivity(intent)
        }
    }
}
