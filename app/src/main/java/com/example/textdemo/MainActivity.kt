package com.example.textdemo

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var text = findViewById<MyText>(R.id.text)
        text.setString("床前明月光床前明床前明月光床前明床前明月光床前明明光床前明明床前明月光床前明床前明月光床前明床前明月光床前明明光床前明明床前明床前明月光床前明床前明月光床前明明光床前明明",
            resources.getDrawable(R.drawable.cornermark_vip))

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }
}