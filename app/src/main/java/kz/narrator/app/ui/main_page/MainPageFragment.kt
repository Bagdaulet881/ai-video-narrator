package kz.narrator.app.ui.main_page

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kz.narrator.app.R
import kz.narrator.app.data.models.network.Status
import kz.narrator.app.databinding.FragmentMainPageBinding
import kz.narrator.app.ui.ai_processor.image_analyze.ImageAnalyzerViewModel
import kz.narrator.app.ui.ai_processor.text_to_speech.TextToSpeechViewModel
import kz.narrator.app.ui.ai_processor.video_crop.VideoCropViewModel
import kz.narrator.app.ui.attach_files.AttachFilesUtils
import kz.narrator.app.ui.attach_files.AttachFilesViewModel
import kz.narrator.app.ui_common.base.BaseFragment
import kz.narrator.app.utils.file.FileUtils.playAudioFromFile

class MainPageFragment : BaseFragment() {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
    private lateinit var videoCropViewModel: VideoCropViewModel
    private lateinit var ivAnalyzerViewModel: ImageAnalyzerViewModel
    private lateinit var textSpeechViewModel: TextToSpeechViewModel
    private lateinit var attachFilesViewModel: AttachFilesViewModel

    private lateinit var attachFilesUtils: AttachFilesUtils

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
        viewModel = getViewModel(MainPageViewModel::class.java)
        binding.viewModel = viewModel

        initAndObserveAttachFileViewModel()

        attachFilesUtils = AttachFilesUtils(
            context = requireContext(),
            activity = requireActivity(),
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            attachFilesViewModel = attachFilesViewModel
        )


        initAndObserveVideoCropViewModel()
        initAndObserveImageAnalyzerViewModel()
        initAndObserveTextSpeechViewModel()

        observeViewModel()
    }

    private fun initAndObserveAttachFileViewModel(){
        attachFilesViewModel = getViewModel(AttachFilesViewModel::class.java)

        attachFilesViewModel.apply {
            mediaSelected.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        videoCropViewModel.mediaSelected()
                    }
                }
            )
        }
    }

    private fun initAndObserveVideoCropViewModel(){
        videoCropViewModel = getViewModel(VideoCropViewModel::class.java)
        videoCropViewModel.setAttachFilesCallback(attachFilesUtils.attachFilesViewModel.attachFilesCallback)

        videoCropViewModel.apply {
            selectedMediaFrame.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        binding.ivAttachFile.setImageBitmap(it)
                    }
                }
            )
        }
    }

    private fun initAndObserveImageAnalyzerViewModel(){
        ivAnalyzerViewModel = getViewModel(ImageAnalyzerViewModel::class.java)
        ivAnalyzerViewModel.setAttachFilesCallback(attachFilesUtils.attachFilesViewModel.attachFilesCallback)

        ivAnalyzerViewModel.apply {
            openAiResource.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                onOpenAiResourceSuccess(it.data)
                                dismissProgressDialog()
                            }
                            Status.ERROR -> {
                                handleExceptionView(it.exception)
                                dismissProgressDialog()
                            }
                            Status.LOADING -> {
                                showProgressDialog()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun initAndObserveTextSpeechViewModel(){
        textSpeechViewModel = getViewModel(TextToSpeechViewModel::class.java)

        textSpeechViewModel.apply {
            audioAiResource.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                onAudioFileResourceSuccess(it.data)
                                dismissProgressDialog()
                            }
                            Status.ERROR -> {
                                handleExceptionView(it.exception)
                                dismissProgressDialog()
                            }
                            Status.LOADING -> {
                                showProgressDialog()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun observeViewModel() {
        viewModel.setAttachFilesCallback(attachFilesUtils.attachFilesViewModel.attachFilesCallback)

        viewModel.apply {
            playAudio.observe(
                viewLifecycleOwner,
                Observer {
                    it.getContentIfNotHandled()?.let {
                        playAudioFromFile(requireContext(), it)
                    }
                }
            )
        }
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
}