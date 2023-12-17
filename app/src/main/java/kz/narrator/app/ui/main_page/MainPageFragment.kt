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
import kz.narrator.app.ui.attach_files.AttachFilesUtils
import kz.narrator.app.ui.attach_files.AttachFilesViewModel
import kz.narrator.app.ui_common.base.BaseFragment

class MainPageFragment : BaseFragment() {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
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

        attachFilesUtils = AttachFilesUtils(
            context = context!!,
            activity = activity!!,
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            attachFilesViewModel = getViewModel(AttachFilesViewModel::class.java)
        )

        initAndObserveAttachFilesViewModel()
        observeViewModel()
    }

    private fun initAndObserveAttachFilesViewModel() {
//        attachFilesUtils.attachFilesViewModel.adapterImagesWithAdd.observe(
//            viewLifecycleOwner,
//            Observer {
//                (binding.recyclerView.adapter as CommentAdapter).submitList(it)
//            }
//        )
    }

    private fun observeViewModel() {
        viewModel.setAttachFilesCallback(attachFilesUtils.attachFilesViewModel.attachFilesCallback)

        viewModel.apply {
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