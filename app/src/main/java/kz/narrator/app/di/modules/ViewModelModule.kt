package kz.narrator.app.di.modules

import dagger.Module
import kz.narrator.app.di.view_model.*

@Module(
    includes = [
        ActivityViewModelModule::class,
        SplashViewModelModule::class,
        AboutViewModelModule::class,
        MainPageViewModelModule::class,
        AuthTypeViewModelModule::class,
        AuthenticationViewModelModule::class,
        RegistrationViewModelModule::class,
        OnboardViewPagerViewModelModule::class
    ]
)
class ViewModelModule {
}