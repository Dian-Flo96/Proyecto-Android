package com.example.proyecto.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: String,      // "Ingreso" o "Gasto"
    val categoria: String, // "Salario", "Regalo", "Otros" (para Ingreso)
                           // "Salud", "Educacion", "Ocio", "Casa", "Familia", "Alimentos" (para Gasto)
    val fecha: Long,       // Timestamp
    val monto: Double,
    val descripcion: String
)
