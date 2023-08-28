package com.example.kelineyt.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kelineyt.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
    }
}