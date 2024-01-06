package kz.narrator.app.ui.bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.narrator.app.R
import kz.narrator.app.databinding.DialogLoggingStateBinding
import kz.narrator.app.ui.main_page.MainPageViewModel
import kz.narrator.app.ui_common.base.BaseBottomSheetDialogFragment

class LoggingStateDialog: BaseBottomSheetDialogFragment() {

    private lateinit var binding: DialogLoggingStateBinding
    private lateinit var viewModel: MainPageViewModel
    private var type: String = ""

    companion object {
        fun newInstance(type: String): LoggingStateDialog {
            val dialog = LoggingStateDialog()
            dialog.type = type
            return dialog
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_logging_state, null, false
            )
        binding.lifecycleOwner = this

        dialog.setContentView(binding.root)

        initView()
        setBottomSheetCallback(binding.root)
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    fun initView(){
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        binding.btnClose.setOnClickListener {
            viewModel.onClearLogs()
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModelOfActivity(MainPageViewModel::class.java)

        binding.viewModel = viewModel
        binding.recyclerView.adapter = LoggingAdapter(requireContext())

        observeViewModel()
    }

    fun observeViewModel(){
        viewModel.listOfLogs.observe(
            this,
            Observer {
                (binding.recyclerView.adapter as LoggingAdapter).submitList(it)
                if(it.size > 3){
                    binding.recyclerView.smoothScrollToPosition(it.size - 1)
                }
            })

        viewModel.onBottomSheetOpened(type)
    }
}