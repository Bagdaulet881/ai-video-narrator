package kz.narrator.app.di.components

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import kz.narrator.app.App
import kz.narrator.app.di.contributes_android_injector.activity.ActivityBuildersModule
import kz.narrator.app.di.modules.AppModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityBuildersModule::class,
        AppModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

}