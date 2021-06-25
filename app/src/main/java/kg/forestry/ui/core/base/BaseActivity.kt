package kg.forestry.ui.core.base

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kg.core.Event
import kg.forestry.ui.core.ProgressDialog
import kg.core.utils.ConnectionLiveData
import kg.core.utils.LocaleManager
import kg.core.utils.isConnected
import kg.forestry.R
import org.koin.androidx.viewmodel.ext.android.getViewModel
import kotlin.reflect.KClass

abstract class BaseActivity<T : ViewModel>(@LayoutRes val layout: Int, val vmClass: KClass<T>) :
    AppCompatActivity() {

    lateinit var vm: T
    var progressBar: Dialog? = null
    protected lateinit var connectionLiveData: ConnectionLiveData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = getViewModel(vmClass)
        setContentView(layout)
        connectionLiveData = ConnectionLiveData(this)
        (vm as? BaseViewModel<*>)?.let { it ->
            it.showProgress.observe(this, Observer {
                it?.let { setProgressBarState(it) }
            })
        }
        (vm as? BaseViewModel<*>)?.let { vm->
            connectionLiveData.observe(this, Observer {
                vm.isNetworkConnected = it
            })
            vm.isNetworkConnected = isConnected
            Log.d("NETWORK",vm.isNetworkConnected.toString())
        }
        (vm as? BaseViewModel<*>)?.let {
            it.event.observe(this, Observer {
                when(it){
                   is Event.Message -> Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setViewBackground(view: View, resId: Int) {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN -> view.setBackgroundDrawable(
                resources.getDrawable(resId)
            )
            Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1 -> view.background =
                resources.getDrawable(resId)
            else -> view.background = ContextCompat.getDrawable(this, resId)
        }
    }

    private fun setProgressBarState(isShow: Boolean) {
        if (isShow) {
            progressBar = ProgressDialog.progressDialog(this)
            progressBar?.show()
        } else {
            progressBar?.dismiss()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    protected fun finishActivityListener() = View.OnClickListener { onBackPressed() }

    fun getResourceString(id: Int): String {
        return getString(id)
    }


    fun exitWithQuery() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.you_want_to_exist))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
            .setNegativeButton(getString(R.string.no), null)
            .create().show()
    }

    protected fun setupToolbar(
        toolbar: Toolbar, @Nullable title: String, @DrawableRes icon: Int,
        listener: View.OnClickListener?
    ) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        if (icon != -1) {
            toolbar.setNavigationIcon(icon)
            toolbar.setNavigationOnClickListener(listener)
        } else {
            toolbar.navigationIcon = null
        }
    }

}
