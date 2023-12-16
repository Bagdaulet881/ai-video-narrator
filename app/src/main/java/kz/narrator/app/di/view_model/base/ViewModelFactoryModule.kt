package kz.narrator.app.di.view_model.base

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}