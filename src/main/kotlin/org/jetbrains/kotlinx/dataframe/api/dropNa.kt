package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public fun <T> DataFrame<T>.dropNa(whereAllNa: Boolean = false, selector: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[selector]

    fun DataRow<T>.checkNa(col: AnyCol): Boolean {
        val value = col[this]
        return value == null || (value is Double && value.isNaN())
    }

    return if (whereAllNa) drop { cols.all { checkNa(it) } }
    else drop { cols.any { checkNa(it) } }
}

public fun <T> DataFrame<T>.dropNa(vararg cols: KProperty<*>, whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNa(vararg cols: String, whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNa(vararg cols: Column, whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNa(cols: Iterable<Column>, whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { cols.toColumnSet() }

public fun <T> DataFrame<T>.dropNa(whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { all() }