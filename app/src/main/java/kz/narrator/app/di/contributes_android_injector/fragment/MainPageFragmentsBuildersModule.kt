package kz.narrator.app.di.contributes_android_injector.fragment

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kz.narrator.app.ui.bottom_sheet.LoggingStateDialog
import kz.narrator.app.ui.main_page.MainPageFragment

@Module
abstract class MainPageFragmentsBuildersModule {

    @ContributesAndroidInjector
    internal abstract fun mainPageFragment(): MainPageFragment

    @ContributesAndroidInjector
    internal abstract fun loggingStateDialog(): LoggingStateDialog

}