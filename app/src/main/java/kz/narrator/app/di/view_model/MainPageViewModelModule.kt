package kz.narrator.app.di.view_model

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kz.narrator.app.di.view_model.base.ViewModelKey
import kz.narrator.app.ui.ai_processor.image_analyze.ImageAnalyzerViewModel
import kz.narrator.app.ui.ai_processor.text_to_speech.TextToSpeechViewModel
import kz.narrator.app.ui.ai_processor.video_crop.VideoCropViewModel
import kz.narrator.app.ui.attach_files.AttachFilesViewModel
import kz.narrator.app.ui.main_page.MainPageViewModel

@Module
abstract class MainPageViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainPageViewModel::class)
    abstract fun bindMainPageViewModel(viewModel: MainPageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AttachFilesViewModel::class)
    abstract fun bindAttachFilesViewModel(viewModel: AttachFilesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ImageAnalyzerViewModel::class)
    abstract fun bindImageAnalyzerViewModel(viewModel: ImageAnalyzerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TextToSpeechViewModel::class)
    abstract fun bindTextToSpeechViewModel(viewModel: TextToSpeechViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoCropViewModel::class)
    abstract fun bindVideoCropViewModel(viewModel: VideoCropViewModel): ViewModel


}