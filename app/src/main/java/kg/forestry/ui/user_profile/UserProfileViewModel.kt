package kg.forestry.ui.user_profile

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import kg.core.Event
import kg.forestry.ui.core.base.BaseViewModel
import kg.forestry.localstorage.model.User
import kg.forestry.repos.AccountRepository
import kg.forestry.repos.FirebaseQueryLiveData


class UserProfileViewModel(val accountRepository: AccountRepository) : BaseViewModel<Event>() {
    var userInfo = User()
    var photoUrl = String()

    private val liveData = FirebaseQueryLiveData(accountRepository.getUserProfileInfoFromServer(accountRepository.getUserToken()))

    fun getDataSnapshotLiveData(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun updateUserInfo(user: User) {
        accountRepository.updateUserInfoInServer(user)
    }

}
