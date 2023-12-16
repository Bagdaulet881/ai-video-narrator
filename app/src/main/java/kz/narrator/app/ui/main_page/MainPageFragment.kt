package kz.narrator.app.ui.main_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kz.narrator.app.R
import kz.narrator.app.databinding.FragmentMainPageBinding
import kz.narrator.app.ui_common.base.BaseFragment

class MainPageFragment : BaseFragment() {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel

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

        observeViewModel()

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun observeViewModel() {
        viewModel.onSuccess()
    }
}