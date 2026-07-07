package com.utp.avance2_proyectofinal.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EcoSpotDB(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ecospots_usuario.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ECOSPOTS = "eco_spots"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_TIPO_RESIDUO = "tipo_residuo"
        const val COL_LATITUD = "latitud"
        const val COL_LONGITUD = "longitud"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_ECOSPOTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_DESCRIPCION TEXT NOT NULL,
                $COL_TIPO_RESIDUO TEXT NOT NULL,
                $COL_LATITUD REAL NOT NULL,
                $COL_LONGITUD REAL NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ECOSPOTS")
        onCreate(db)
    }

    fun insertarEcoSpot(ecoSpot: EcoSpot): Long {
        val values = ContentValues().apply {
            put(COL_NOMBRE, ecoSpot.nombre)
            put(COL_DESCRIPCION, ecoSpot.descripcion)
            put(COL_TIPO_RESIDUO, ecoSpot.tipoResiduo)
            put(COL_LATITUD, ecoSpot.latitud)
            put(COL_LONGITUD, ecoSpot.longitud)
        }

        return writableDatabase.insert(TABLE_ECOSPOTS, null, values)
    }

    fun obtenerEcoSpots(): List<EcoSpot> {
        val lista = mutableListOf<EcoSpot>()

        val cursor = readableDatabase.query(
            TABLE_ECOSPOTS,
            null,
            null,
            null,
            null,
            null,
            "$COL_ID DESC"
        )

        cursor.use { c ->
            val idIdx = c.getColumnIndexOrThrow(COL_ID)
            val nombreIdx = c.getColumnIndexOrThrow(COL_NOMBRE)
            val descripcionIdx = c.getColumnIndexOrThrow(COL_DESCRIPCION)
            val tipoResiduoIdx = c.getColumnIndexOrThrow(COL_TIPO_RESIDUO)
            val latitudIdx = c.getColumnIndexOrThrow(COL_LATITUD)
            val longitudIdx = c.getColumnIndexOrThrow(COL_LONGITUD)

            while (c.moveToNext()) {
                lista += EcoSpot(
                    id = c.getLong(idIdx),
                    nombre = c.getString(nombreIdx),
                    descripcion = c.getString(descripcionIdx),
                    tipoResiduo = c.getString(tipoResiduoIdx),
                    latitud = c.getDouble(latitudIdx),
                    longitud = c.getDouble(longitudIdx)
                )
            }
        }

        return lista
    }

    fun eliminarEcoSpot(id: Long): Int {
        return writableDatabase.delete(
            TABLE_ECOSPOTS,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }
}