package kz.narrator.app.di.contributes_android_injector.fragment

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kz.narrator.app.ui.splash.SplashFragment

@Module
abstract class SplashFragmentsBuildersModule {

    @ContributesAndroidInjector
    internal abstract fun splashFragment(): SplashFragment

}