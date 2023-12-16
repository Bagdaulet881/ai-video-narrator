package kz.narrator.app.di.contributes_android_injector.activity

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kz.narrator.app.di.contributes_android_injector.fragment.*
import kz.narrator.app.ui.activities.main.MainActivity
import kz.narrator.app.ui.activities.splash.SplashActivity

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(
        modules = [
            SplashFragmentsBuildersModule::class,
            OnboardFragmentsBuildersModule::class,
            AuthTypeFragmentsBuildersModule::class,
            AuthenticationFragmentsBuildersModule::class
        ]
    )
    internal abstract fun splashActivity(): SplashActivity

    @ContributesAndroidInjector(
        modules = [
            AboutFragmentsBuildersModule::class,
            MainPageFragmentsBuildersModule::class,
            OnboardFragmentsBuildersModule::class,
            AuthenticationFragmentsBuildersModule::class,
            AuthTypeFragmentsBuildersModule::class
        ]
    )
    internal abstract fun mainActivity(): MainActivity

}