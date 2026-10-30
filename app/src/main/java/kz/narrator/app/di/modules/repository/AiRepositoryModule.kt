package kz.narrator.app.di.modules.repository

import dagger.Binds
import dagger.Module
import kz.narrator.app.data.repository.ai_repos.OpenAiRepositiry
import kz.narrator.app.data.repository.ai_repos.OpenAiRepositiryImpl

@Module
abstract class AiRepositoryModule {

    @Binds
    abstract fun bindOpenAiRepositiry(impl: OpenAiRepositiryImpl): OpenAiRepositiry

}
