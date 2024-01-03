package com.devative.littledoor.activity

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var vm:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNavigation.itemIconTintList = null
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        if (intent.hasExtra(Constants.IS_DOCTOR)){
            Constants.isDoctor = true
            navController.setGraph(R.navigation.dr_nav_graph)
            binding.bottomNavigation.menu.clear()
            binding.bottomNavigation.inflateMenu(R.menu.dr_bottom_navigation)
        }else{
            Constants.isDoctor = false
        }
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        vm = MainViewModel.getViewModel(this)
        vm.fetchUserData()
        vm.basicDetails.observe(this){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
            }
        }


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            var selectedColor = R.color.white_for_black
            val id = destination.id
            if ((purpleHeaderFragment(id)) && selectedColor == R.color.white_for_black){
                selectedColor = R.color.overlays_purple
                changeStatusBarColor(selectedColor)
            }else{
                selectedColor = R.color.white_for_black
                changeStatusBarColor(selectedColor)
            }
        }
    }

    private fun purpleHeaderFragment(id: Int) = id == R.id.bottom_navigation_chat || id == R.id.bottom_navigation_search || id == R.id.bottom_navigation_settings

    private fun changeStatusBarColor(color:Int) {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor =
            ContextCompat.getColor(this, color)
    }

    fun setNavigationSelection(id:Int){
        binding.bottomNavigation.selectedItemId = id
    }

}
