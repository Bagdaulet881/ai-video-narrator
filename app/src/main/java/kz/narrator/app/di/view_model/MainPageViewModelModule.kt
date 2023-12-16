package kz.narrator.app.di.view_model

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kz.narrator.app.di.view_model.base.ViewModelKey
import kz.narrator.app.ui.main_page.MainPageViewModel

@Module
abstract class MainPageViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainPageViewModel::class)
    abstract fun bindMainPageViewModel(viewModel: MainPageViewModel): ViewModel


}