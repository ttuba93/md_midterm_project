package com.example.myshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myshelf.ui.navigation.MyShelfApp
import com.example.myshelf.ui.theme.MyShelfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyShelfTheme {
                MyShelfApp()
            }
        }
    }
}
