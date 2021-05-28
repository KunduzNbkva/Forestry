package kg.forestry.localstorage

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Scheduler {

    fun ui(): io.reactivex.Scheduler {
        return AndroidSchedulers.mainThread()
    }

    fun computation(): io.reactivex.Scheduler {
        return Schedulers.computation()
    }

    fun io(): io.reactivex.Scheduler {
        return Schedulers.io()
    }

}