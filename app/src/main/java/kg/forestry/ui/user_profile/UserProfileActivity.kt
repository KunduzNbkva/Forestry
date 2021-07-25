package kg.forestry.ui.user_profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.Constants
import kg.forestry.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_profile.*
import java.text.SimpleDateFormat
import java.util.*

class UserProfileActivity: BaseActivity<UserProfileViewModel>(R.layout.activity_profile, UserProfileViewModel::class) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        inp_email.addTextChangedListener(getTextWatcher())
        inp_name.addTextChangedListener(getTextWatcher())
        inp_organization.addTextChangedListener(getTextWatcher())
        inp_post.addTextChangedListener(getTextWatcher())
        btn_save.setOnClickListener {
            validateEmail()
        }
    }

    private fun updateUserData(){
        vm.userInfo.email = inp_layout_email.editText?.text.toString().trim()
        vm.userInfo.username = inp_layout_name.editText?.text.toString().trim()
        vm.userInfo.post = inp_layout_post.editText?.text.toString().trim()
        vm.userInfo.organization = inp_layout_organization.editText?.text.toString().trim()
        vm.userInfo.date = getCurrentDate()
        vm.userInfo.phone = intent.getStringExtra(Constants.PHONE)?:""
        vm.updateUserInfo(vm.userInfo)
        MainActivity.start(this)
    }

    private fun validateEmail() {
        val emailTxt = inp_email.text.toString().trim()

        if (emailTxt.isEmpty()) {
            inp_layout_email.error = "Field can't be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            inp_layout_email.error = "Please enter a valid email address"
        } else {
            inp_layout_email.error = null
            updateUserData()
        }
    }

    private fun getCurrentDate() = SimpleDateFormat.getDateInstance().format(Date())


    private fun toggleButtonState() {
        btn_save.isEnabled = inp_layout_email.editText?.text.toString().trim().isNotEmpty()
                && inp_layout_name.editText?.text.toString().trim().isNotEmpty()
                && inp_layout_organization.editText?.text.toString().trim().isNotEmpty()
                && inp_layout_post.editText?.text.toString().trim().isNotEmpty()
    }

    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                toggleButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    companion object {
        fun start(context: Context,phone:String?) {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(Constants.PHONE,phone)
            context.startActivity(intent)
        }
    }


}