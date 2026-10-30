package kz.narrator.app.di.modules.common

import dagger.Module
import dagger.Provides
import kz.narrator.app.data.openai.OpenAiInstance
import javax.inject.Singleton

@Module
class UtilsModule {

    @Provides
    @Singleton
    fun provideOpenAiInstance() = OpenAiInstance()

}