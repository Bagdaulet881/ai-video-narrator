package kz.narrator.app.ui.activities.main

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.analytics.FirebaseAnalytics
import kz.narrator.app.R
import kz.narrator.app.databinding.ActivityMainBinding
import kz.narrator.app.ui.main_page.MainPageViewModel
import kz.narrator.app.ui_common.base.BaseActivity

class MainActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context?) = Intent(context, MainActivity::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    lateinit var mainPageviewModel: MainPageViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        viewModel = getViewModel(MainViewModel::class.java)
        mainPageviewModel = getViewModel(MainPageViewModel::class.java)
        binding.viewModel = viewModel

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        initView()
    }

    private fun initView() {

        navController = findNavController(R.id.nav_host_fragment)

        initToolbar()
        initOnDestinationChangedListener()
        initBottomNavigation()
    }

    private fun initToolbar() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                //TODO 881 bottom navigation
            )
        )
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun initOnDestinationChangedListener() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.aboutFragment -> {
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                R.id.mainPageFragment -> {
                    binding.appBarLayout.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                R.id.authenticationFragment,
                R.id.registrationFragment,
                R.id.resetPasswordFragment -> {
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.appBarLayout.visibility = View.GONE
                }
            }

            val colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(this, R.color.black),
                    PorterDuff.Mode.MULTIPLY
                )
            val navigationIcon = binding.toolbar.navigationIcon
            if (navigationIcon != null) {
                navigationIcon.colorFilter = colorFilter
                binding.toolbar.navigationIcon = navigationIcon
                binding.toolbar.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            binding.toolbar
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavigation.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_main_page -> {
                    mainPageviewModel.onAttachFileBtnClick()
                    true
                }
                else -> {
                    // Let the NavController handle the navigation
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
            }
        }

//        binding.bottomNavigation.background = null
//        binding.bottomNavigation.menu.getItem(0).isEnabled = false
    }

}