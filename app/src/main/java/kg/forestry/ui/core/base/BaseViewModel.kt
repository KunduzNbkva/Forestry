package kg.forestry.ui.core.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kg.core.Event

open class BaseViewModel<T : Event> : ViewModel() {

    var event: MutableLiveData<T> = MutableLiveData()
    var isNetworkConnected = false
    var showProgress: MutableLiveData<Boolean> = MutableLiveData()

    fun setProgress(isShow: Boolean) {
        showProgress.value = isShow
    }
}
