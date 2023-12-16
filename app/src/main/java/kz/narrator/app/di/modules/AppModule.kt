package kz.narrator.app.di.modules

import dagger.Module
import kz.narrator.app.di.modules.common.NetworkModule
import kz.narrator.app.di.modules.common.PreferenceModule
import kz.narrator.app.di.modules.common.UtilsModule
import kz.narrator.app.di.view_model.base.ViewModelFactoryModule

@Module(
    includes = [
        NetworkModule::class,
        PreferenceModule::class,
        UtilsModule::class,

        //api
        ApiModule::class,

        //repository
        RepositoryModule::class,

        //
        ViewModelFactoryModule::class,
        ViewModelModule::class
    ]
)
class AppModule {
}