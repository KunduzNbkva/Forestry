package kg.forestry.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kg.forestry.R
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.model.User
import kg.forestry.ui.core.base.BaseActivity
import kg.forestry.ui.main.MainActivity
import kg.forestry.ui.user_profile.UserProfileActivity
import kotlinx.android.synthetic.main.activity_auth.*


class AuthActivity : BaseActivity<AuthViewModel>(R.layout.activity_auth, AuthViewModel::class) {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        btn_auth.setOnClickListener {
            checkIfUserLoggedIn()
        }
        btn_without_auth.setOnClickListener {
            mAuth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        user?.let {
                            vm.saveUserSession(user.uid)
                            vm.isUserAnonymous(true)
                        }
                        MainActivity.start(this)
                    } else {
                        Toast.makeText(
                            this@AuthActivity,
                            "Для первого входа необходимо интернет соединение",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
        }
    }


    private fun checkIfUserLoggedIn() {
        when (vm.isUserLoggedIn()) {
            true -> MainActivity.start(this)
            false -> openFirebaseAuthActivity()
        }
    }

    private fun openFirebaseAuthActivity() {
        val providers = arrayListOf(AuthUI.IdpConfig.PhoneBuilder().build())
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser

                vm.saveUserSession(user?.uid)
                vm.isUserAnonymous(false)
                checkIsUserProfileExists(user)
            } else { // Sign in failed
                if (response == null) { // User pressed back button
                    Toast.makeText(this, "Вход отменен", Toast.LENGTH_SHORT).show()
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Проблемы с интернет соединением", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                Toast.makeText(this, "Непредвиденная ошибка", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "Sign-in error: ", response.error)
            }
        }
    }

    private fun checkIsUserProfileExists(user: FirebaseUser?) {
        vm.getDataSnapshotLiveData(user!!.uid).observe(this, Observer {
            val userInfo = it?.getValue(User::class.java)
            Log.d("USERINFO",userInfo.toString() + Preferences.instance.userToken)
            if (userInfo == null || !userInfo.isUserExists()) {
                UserProfileActivity.start(this, user.phoneNumber)
            }else {
                MainActivity.start(this)
            }
        })
    }

    companion object {
        const val RC_SIGN_IN = 77
        fun start(context: Context) {
            val intent = Intent(context, AuthActivity::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}
