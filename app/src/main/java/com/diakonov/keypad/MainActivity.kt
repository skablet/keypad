package com.diakonov.keypad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diakonov.keypad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}