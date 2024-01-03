package kz.narrator.app.ui.main_page

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.cloud.speech.v1.*
import kz.narrator.app.R
import kz.narrator.app.data.enums.app.RequestCodeEnums
import kz.narrator.app.data.models.network.Status
import kz.narrator.app.databinding.FragmentMainPageBinding
import kz.narrator.app.ui.ai_processor.image_analyze.ImageAnalyzerViewModel
import kz.narrator.app.ui.ai_processor.speech_to_text.SpeechWithTimestamp
import kz.narrator.app.ui.ai_processor.text_to_speech.TextToSpeechViewModel
import kz.narrator.app.ui.ai_processor.video_crop.VideoCropViewModel
import kz.narrator.app.ui.attach_files.AttachFilesUtils
import kz.narrator.app.ui.attach_files.AttachFilesViewModel
import kz.narrator.app.ui.bottom_sheet.LoggingStateDialog
import kz.narrator.app.ui_common.base.BaseFragment
import kz.narrator.app.ui_common.video_tools.video.TextOnVideo
import kz.narrator.app.utils.file.FileUtils
import kz.narrator.app.utils.file.FileUtils.playAudioFromFile
import kz.narrator.app.utils.file.VideoFileUtils
import java.io.File

class MainPageFragment : BaseFragment() {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
    private lateinit var videoCropViewModel: VideoCropViewModel
    private lateinit var ivAnalyzerViewModel: ImageAnalyzerViewModel
    private lateinit var textSpeechViewModel: TextToSpeechViewModel
    private lateinit var attachFilesViewModel: AttachFilesViewModel

    private lateinit var attachFilesUtils: AttachFilesUtils
    lateinit var font: File

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: StyledPlayerView
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isPlayerPlaying = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main_page,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModelOfActivity(MainPageViewModel::class.java)
        playerView = binding.includeMediaEdit.playerView
        binding.viewModel = viewModel
        ///test
        val file = File(requireContext().filesDir, "audio_name_20240102_053427.mp3")
        binding.tvFile.text = "FILE FOUND STATUS ${file.exists()}"
        ///
        initAndObserveAttachFileViewModel()
        attachFilesUtils = AttachFilesUtils(
            context = requireContext(),
            activity = requireActivity(),
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            attachFilesViewModel = attachFilesViewModel
        )
        font = VideoFileUtils.copyTTFFileToExternalStorage(
            R.font.roboto_black,
            "myFont.ttf",
            requireContext()
        )

        initAndObserveVideoCropViewModel()
        initAndObserveImageAnalyzerViewModel()
        initAndObserveTextSpeechViewModel()

        observeViewModel()
        ///test
        binding.btnTimestamp.setOnClickListener {
            initGoogleSpeechTimeStamp()
        }
        ///
    }

    fun initGoogleSpeechTimeStamp() {
        SpeechWithTimestamp.with(requireActivity(), requireContext())
            .setUILoggingCallback(viewModel.uiLoggingCallback)
            .getTimeStamps("audio_name_20240102_053427.mp3")
    }

    private fun initAndObserveImageAnalyzerViewModel() {
        ivAnalyzerViewModel = getViewModel(ImageAnalyzerViewModel::class.java)

        ivAnalyzerViewModel.apply {
            openAiResource.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        viewModel.uiLoggingCallback.onLogState("OpenAI: request state", it.status)
                        when (it.status) {
                            Status.SUCCESS -> {
                                viewModel.uiLoggingCallback.onLogState("OpenAI: response size ${it.data?.choices?.size}", Status.SUCCESS)
                                onOpenAiResourceSuccess(it.data)
                            }
                            Status.ERROR -> {
                                handleExceptionView(it.exception)
                                viewModel.uiLoggingCallback.onLogState("OpenAI: request error ${it.exception}", it.status)
                            }
                            else -> {}
                        }
                    }
                }
            )
        }
    }

    private fun initAndObserveTextSpeechViewModel() {
        textSpeechViewModel = getViewModel(TextToSpeechViewModel::class.java)

        textSpeechViewModel.apply {
            audioAiResource.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        viewModel.uiLoggingCallback.onLogState("ElevenLabs: request state", it.status)

                        when (it.status) {
                            Status.SUCCESS -> {
                                viewModel.uiLoggingCallback.onLogState("ElevenLabs: response", Status.SUCCESS)
                                onAudioFileResourceSuccess(it.data)
                            }
                            Status.ERROR -> {
                                viewModel.uiLoggingCallback.onLogState("ElevenLabs: error ${it.exception}", Status.ERROR)
                            }
                            else -> {}
                        }
                    }
                }
            )
        }
    }

    private fun observeViewModel() {
        viewModel.setAttachFilesCallback(attachFilesUtils.attachFilesViewModel.attachFilesCallback)

        viewModel.apply {

            createVideo.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        viewModel.attachedMedia.value?.uri?.let {
                            val file = FileUtils.getVideoFileFromUri(requireContext(), it)

                            if (file != null) {
                                TextOnVideo.with(requireContext())
                                    .setFile(file)
//                                        .setAudioFile(FileUtils.getAudioFile(requireContext(), viewModel.audioFileNameData.value?:""))
                                    .setAudioFile(
                                        FileUtils.getAudioFile(
                                            requireContext(),
                                            "audio_name_20240102_053427.mp3"
                                        )
                                    )
                                    .setOutputPath(VideoFileUtils.outputPath + "video")
                                    .setOutputFileName("textOnVideo_asdasdTEST" + ".mp4")
                                    .setFont(font) //Font .ttf of text
                                    .setText("Text Displayed on Video!!") //Text to be displayed
                                    .setColor("#50b90e") //Color of Text
                                    .setSize("34") //Size of text
                                    .addBorder(true) //This will add background with border on text
                                    .setPosition(TextOnVideo.POSITION_NARRATING_TEXT) //Can be selected
                                    .setCallback(viewModel.ffMpegCallback)
                                    .draw()
                            }
                        }
                    }
                }
            )

            runVideoPlayer.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        initPlayer(it)
                    }
                }
            )

            runVoiceLab.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        textSpeechViewModel.onRun()
                    }
                }
            )

            runOpenAi.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        ivAnalyzerViewModel.onRun()
                    }
                }
            )

            runVideoCrop.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        videoCropViewModel.onRun()
                    }
                }
            )

            playAudio.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        playAudioFromFile(requireContext(), it)
                    }
                }
            )
        }

        viewModel.runBottomSheet.observe(
            viewLifecycleOwner,
            Observer {
                it.getContentIfNotHandled()?.let {
                    val dialog = LoggingStateDialog.newInstance()
                    dialog.setTargetFragment(this, RequestCodeEnums.SHOW_LOGS_LOADING.id)
                    dialog.show(requireFragmentManager(), "")
                }
            }
        )

        videoCropViewModel.setMainMediaCallback(viewModel.mainAnalyzeCallback)
        ivAnalyzerViewModel.setMainMediaCallback(viewModel.mainAnalyzeCallback)
        textSpeechViewModel.setMainMediaCallback(viewModel.mainAnalyzeCallback)

        videoCropViewModel.setUILoggingCallback(viewModel.uiLoggingCallback)
        ivAnalyzerViewModel.setUILoggingCallback(viewModel.uiLoggingCallback)
        textSpeechViewModel.setUILoggingCallback(viewModel.uiLoggingCallback)

    }






    //----------------------------------------------------------------------------------------------
    private fun initAndObserveAttachFileViewModel() {
        attachFilesViewModel = getViewModel(AttachFilesViewModel::class.java)

        attachFilesViewModel.apply {
            mediaSelected.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        viewModel.setMediaAttachedStatus(true)
                    }
                }
            )
        }
    }

    private fun initAndObserveVideoCropViewModel() {
        videoCropViewModel = getViewModel(VideoCropViewModel::class.java)
    }

    private fun releasePlayer() {
        if (this::exoPlayer.isInitialized) {
            isPlayerPlaying = exoPlayer.playWhenReady
            playbackPosition = exoPlayer.currentPosition
            currentWindow = exoPlayer.currentMediaItemIndex
            exoPlayer.release()
        }
    }
    private fun initPlayer(mediaUri: Uri) {
        exoPlayer = ExoPlayer.Builder(requireContext()).build().apply {
            playWhenReady = isPlayerPlaying
            seekTo(currentWindow, playbackPosition)
            setMediaItem(MediaItem.fromUri(mediaUri))
            prepare()
        }

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING) {
                    viewModel.setPlaybackCurrentPosition(exoPlayer.currentPosition)
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                viewModel.setPlaybackCurrentPosition(exoPlayer.currentPosition)
            }
        })
        playerView.player = exoPlayer
        binding.includeMediaEdit.spvControllers.player = exoPlayer
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        attachFilesUtils.onActivityResult(
            requestCode = requestCode,
            resultCode = resultCode,
            data = data
        )

        if (resultCode != Activity.RESULT_OK) {
            return
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        val processed = attachFilesUtils.onRequestPermissionsResult(
            requestCode = requestCode,
            permissions = permissions,
            grantResults = grantResults
        )
        if (!processed) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    companion object {
        const val HLS_STATIC_URL =
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }

}