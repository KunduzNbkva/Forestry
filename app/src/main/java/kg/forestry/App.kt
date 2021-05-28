package kg.forestry

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import kg.forestry.di.appModule
import kg.forestry.di.networkModule
import kg.forestry.di.repositoryModule
import kg.forestry.di.vmModule
import kg.core.utils.LocaleManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.core.context.startKoin

class App : Application(){

    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            androidFileProperties()
            modules(
                vmModule,
                appModule,
                repositoryModule,
                networkModule
            )
        }

}

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
        MultiDex.install(this)
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}