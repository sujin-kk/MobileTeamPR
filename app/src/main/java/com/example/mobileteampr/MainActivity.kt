package com.example.mobileteampr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileteampr.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    val iconarr = arrayListOf<Int>(R.drawable.people_icon, R.drawable.map_icon, R.drawable.set_icon)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        // this = MainActivity (fragmentActivity 상속받음)
        binding.viewPager.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager){
                tab, position ->
            tab.setIcon(iconarr[position])

        }.attach()
    }
}