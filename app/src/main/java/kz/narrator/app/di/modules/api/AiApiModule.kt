package kz.narrator.app.di.modules.api

import dagger.Module
import dagger.Provides
import kz.narrator.app.network.api.AiApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AiApiModule {

    @Provides
    @Singleton
    fun provideAiApiModule(retrofit: Retrofit): AiApi {
        return retrofit.create(AiApi::class.java)
    }

}
