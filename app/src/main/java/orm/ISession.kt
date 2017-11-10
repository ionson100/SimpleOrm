package orm

import android.database.sqlite.SQLiteDatabase

public  interface ISession {

    val sqLiteDatabase: SQLiteDatabase

    fun <T> update(item: T): Int

    fun <T> updateWhere(item: T, whereSql: String): Int

    fun <T> insert(item: T): Int

    fun <T> delete(item: T): Int

    fun <T> getList(tClass: Class<T>, where: String, vararg objects: Any): List<T>

    operator fun <T> get(tClass: Class<T>, id: Any): T

    fun executeScalar(sql: String, vararg objects: Any): Any

    fun execSQL(sql: String, vararg objects: Any)

    fun beginTransaction()

    fun commitTransaction()

    fun endTransaction()

    fun deleteTable(tableName: String): Int

    fun deleteTable(tableName: String, where: String, vararg objects: Any): Int


}



