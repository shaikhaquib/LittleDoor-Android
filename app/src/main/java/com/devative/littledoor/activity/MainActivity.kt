package com.devative.littledoor.activity

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityMainBinding
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
            navController.setGraph(R.navigation.dr_nav_graph)
            binding.bottomNavigation.menu.clear()
            binding.bottomNavigation.inflateMenu(R.menu.dr_bottom_navigation);
        }
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        vm = MainViewModel.getViewModel(this)
        vm.fetchUserData()
        vm.basicDetails.observe(this){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
            }
        }

    }

}
