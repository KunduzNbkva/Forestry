package kg.forestry.ui.start

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import kg.core.Event
import kg.forestry.localstorage.Preferences
import kg.forestry.repos.AccountRepository
import kg.forestry.repos.FirebaseQueryLiveData
import kg.forestry.ui.core.base.BaseViewModel

class StartViewModel(private val accountRepository: AccountRepository,
                     private val prefs: Preferences
): BaseViewModel<Event>() {

    fun isUserLoggedIn() = prefs.isUserAuthorized()

    fun saveUserSession(userToken: String?) {
        accountRepository.saveUserSessionToken(userToken)
    }

    fun isUserAnonymous(isAnonymous: Boolean) {
        accountRepository.isUserAnonymous(isAnonymous)
    }


    fun getDataSnapshotLiveData(userId: String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(accountRepository.getUserProfileInfoFromServer(userId))
    }
}