package kz.narrator.app.ui.splash

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kz.narrator.app.R
import kz.narrator.app.databinding.FragmentSplashBinding
import kz.narrator.app.ui.activities.main.MainActivity
import kz.narrator.app.ui_common.base.BaseFragment
import kz.narrator.app.utils.navigation.getSlideLeftAnimBuilder

class SplashFragment : BaseFragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var viewModel: SplashViewModel
    private lateinit var onBoardingScreen: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = getViewModel(SplashViewModel::class.java!!)
        binding.viewModel = viewModel

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.openMainActivity.observe(
            viewLifecycleOwner,
            Observer {
                it.getContentIfNotHandled()?.let {
                    val intent = MainActivity.getIntent(context)
                    startActivity(intent)

                    activity?.finish()
                }
            }
        )
    }

}