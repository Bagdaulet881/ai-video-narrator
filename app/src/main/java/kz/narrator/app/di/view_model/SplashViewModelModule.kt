package kz.narrator.app.di.view_model

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kz.narrator.app.di.view_model.base.ViewModelKey
import kz.narrator.app.ui.splash.SplashViewModel

@Module
abstract class SplashViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

}