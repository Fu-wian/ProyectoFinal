package com.utp.avance2_proyectofinal.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HistorialResiduos(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ecospot.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_RESIDUOS   = "residuos"
        const val COL_ID           = "id"
        const val COL_CATEGORIA    = "categoria"
        const val COL_CANTIDAD     = "cantidad"
        const val COL_UNIDAD       = "unidad"
        const val COL_ORIGEN       = "origen"
        const val COL_FECHA        = "fecha"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_RESIDUOS (
                $COL_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CATEGORIA TEXT NOT NULL,
                $COL_CANTIDAD  REAL NOT NULL,
                $COL_UNIDAD    TEXT NOT NULL,
                $COL_ORIGEN    TEXT NOT NULL,
                $COL_FECHA     TEXT NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESIDUOS")
        onCreate(db)
    }

    fun insertarResiduo(r: RegistrarResiduos): Long {
        val values = ContentValues().apply {
            put(COL_CATEGORIA, r.categoria)
            put(COL_CANTIDAD,  r.cantidad)
            put(COL_UNIDAD,    r.unidad)
            put(COL_ORIGEN,    r.origen)
            put(COL_FECHA,     r.fecha)
        }
        return writableDatabase.insert(TABLE_RESIDUOS, null, values)
    }

    fun obtenerTodos(filtroCategoria: String? = null): List<RegistrarResiduos> {
        val selection     = if (filtroCategoria != null) "$COL_CATEGORIA = ?" else null
        val selectionArgs = if (filtroCategoria != null) arrayOf(filtroCategoria) else null

        val cursor = readableDatabase.query(
            TABLE_RESIDUOS, null,
            selection, selectionArgs,
            null, null, "$COL_FECHA DESC"
        )

        val lista = mutableListOf<RegistrarResiduos>()
        cursor.use { c ->
            val idIdx  = c.getColumnIndexOrThrow(COL_ID)
            val catIdx = c.getColumnIndexOrThrow(COL_CATEGORIA)
            val canIdx = c.getColumnIndexOrThrow(COL_CANTIDAD)
            val uniIdx = c.getColumnIndexOrThrow(COL_UNIDAD)
            val oriIdx = c.getColumnIndexOrThrow(COL_ORIGEN)
            val feIdx  = c.getColumnIndexOrThrow(COL_FECHA)
            while (c.moveToNext()) {
                lista += RegistrarResiduos(
                    id        = c.getLong(idIdx),
                    categoria = c.getString(catIdx),
                    cantidad  = c.getDouble(canIdx),
                    unidad    = c.getString(uniIdx),
                    origen    = c.getString(oriIdx),
                    fecha     = c.getString(feIdx)
                )
            }
        }
        return lista
    }

    fun actualizarResiduo(r: RegistrarResiduos): Int {
        val values = ContentValues().apply {
            put(COL_CATEGORIA, r.categoria)
            put(COL_CANTIDAD,  r.cantidad)
            put(COL_UNIDAD,    r.unidad)
            put(COL_ORIGEN,    r.origen)
            put(COL_FECHA,     r.fecha)
        }
        return writableDatabase.update(
            TABLE_RESIDUOS, values, "$COL_ID = ?", arrayOf(r.id.toString())
        )
    }

    fun eliminarResiduo(id: Long): Int =
        writableDatabase.delete(TABLE_RESIDUOS, "$COL_ID = ?", arrayOf(id.toString()))

    fun contarResiduos(): Int {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_RESIDUOS", null)
        var count = 0
        cursor.use { if (it.moveToFirst()) count = it.getInt(0) }
        return count
    }
}