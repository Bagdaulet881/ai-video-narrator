package kz.narrator.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg
//import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
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
import kotlin.time.Duration.Companion.seconds

class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var lifecycleListener: ApplicationObserver

//    @Inject
//    lateinit var openAiInstance: OpenAI

    //    @Inject
//    lateinit var appNotificationOpenedHandler: AppNotificationOpenedHandler
    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(lifecycleListener)

//        initFfmpeg()
    }

//    fun initFfmpeg(){
//        //Load FFMpeg library
//        try {
//            FFmpeg.getInstance(this).loadBinary(object : FFmpegLoadBinaryResponseHandler {
//                override fun onFailure() {
//                    Log.e("FFMpeg", "Failed to load FFMpeg library.")
//                }
//
//                override fun onSuccess() {
//                    Log.i("FFMpeg", "FFMpeg Library loaded!")
//                }
//
//                override fun onStart() {
//                    Log.i("FFMpeg", "FFMpeg Started")
//                }
//
//                override fun onFinish() {
//                    Log.i("FFMpeg", "FFMpeg Stopped")
//                }
//            })
//        } catch (e: FFmpegNotSupportedException) {
//            e.printStackTrace()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

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