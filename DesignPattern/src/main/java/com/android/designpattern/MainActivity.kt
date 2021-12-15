package com.android.designpattern

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.android.designpattern.创建型.建造者模式.CommonDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val build = CommonDialog.Builder()
        build.create()
        AlertDialog.Builder(this).setMessage("hahaha")
    }
}