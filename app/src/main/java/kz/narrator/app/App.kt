package kz.narrator.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
//import com.onesignal.OneSignal
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kz.narrator.app.data.app_lifecycle.ApplicationObserver
import kz.narrator.app.di.utils.AppInjector
//import kz.narrator.app.one_signal.AppNotificationOpenedHandler
//import kz.narrator.app.one_signal.AppNotificationReceivedHandler
//import kz.narrator.app.one_signal.handler.AppNotificationHandler
import kz.narrator.app.utils.locale.LocaleUtils
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var lifecycleListener: ApplicationObserver

//    @Inject
//    lateinit var appNotificationOpenedHandler: AppNotificationOpenedHandler

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(lifecycleListener)

//        OneSignal.startInit(this)
//            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//            .unsubscribeWhenNotificationsAreDisabled(true)
//            .setNotificationOpenedHandler(appNotificationOpenedHandler)
//            .setNotificationReceivedHandler(AppNotificationReceivedHandler())
//            .init()
//          TODO получить токен и передать в бэк
//        AppNotificationHandler.uniqueToken = OneSignal.getPermissionSubscriptionState()?.subscriptionStatus?.userId
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(base)
        super.attachBaseContext(LocaleUtils.setLocale(base))
        MultiDex.install(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtils.setLocale(this)
    }

}