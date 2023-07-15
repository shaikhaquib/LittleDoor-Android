package com.devative.littledoor.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityMainBinding
import com.devative.littledoor.model.LoginModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var basicDetails: LoginModel.BasicDetails
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
