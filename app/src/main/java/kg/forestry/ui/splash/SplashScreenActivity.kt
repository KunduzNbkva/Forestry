package kg.forestry.ui.splash

import android.os.Bundle
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.forestry.ui.auth.AuthViewModel
import kg.forestry.ui.main.MainActivity
import kg.forestry.ui.start.StartActivity

class SplashScreenActivity: BaseActivity<AuthViewModel>(R.layout.activity_splash_screen, AuthViewModel::class) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfUserLoggedIn()
    }

    private fun checkIfUserLoggedIn() {
        when (vm.isUserLoggedIn()) {
            true -> MainActivity.start(this)
            false -> openStartActivity()
        }
    }

    private fun openStartActivity() {
        StartActivity.start(this)
    }
}
