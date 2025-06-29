package com.example.proyecto

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class formulario : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)
        val button = findViewById<Button>(R.id.btn_gastos2)
        button.setOnClickListener {
            val i = Intent(this, gastos::class.java)
            startActivity(i)
        }
        val button2 = findViewById<Button>(R.id.btn_ingresos2)
        button2.setOnClickListener {
            val i = Intent(this, ingresos::class.java)
            startActivity(i)
        }
    }
}