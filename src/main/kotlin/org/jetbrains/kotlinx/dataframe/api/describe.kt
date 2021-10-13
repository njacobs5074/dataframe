package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.columns.ndistinct
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.isComparable
import org.jetbrains.kotlinx.dataframe.isNumber
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typed
import kotlin.reflect.KType

public fun <T> DataFrame<T>.describe(columns: ColumnsSelector<T, *> = { numberCols() }): DataFrame<ColumnDescriptionSchema> = describe(this[columns])
public fun <T> DataColumn<T>.describe(): DataFrame<ColumnDescriptionSchema> = describe(listOf(this))

@DataSchema
public interface GeneralColumnDescriptionSchema {
    public val column: String
    public val count: Int
    public val nulls: Int
}

@DataSchema
public interface ColumnDescriptionSchema : GeneralColumnDescriptionSchema {
    public val unique: Int
    public val top: Any
    public val freq: Int
    public val type: KType
}

@DataSchema
public interface NumberColumnDescriptionSchema : GeneralColumnDescriptionSchema {
    public val mean: Double
    public val min: Any
    public val max: KType
}

internal fun describe(cols: List<AnyCol>): DataFrame<ColumnDescriptionSchema> {
    val hasNumeric = cols.any { it.isNumber() }
    val hasCategorical = cols.any { !it.isNumber() }
    var df = cols.toDataFrame {
        GeneralColumnDescriptionSchema::column { name }
        GeneralColumnDescriptionSchema::count { size }
        GeneralColumnDescriptionSchema::nulls { values.count { it == null } }

        if (hasCategorical) {
            ColumnDescriptionSchema::unique { ndistinct }
            ColumnDescriptionSchema::top { values.groupBy { it }.maxByOrNull { it.value.size }?.key }
            ColumnDescriptionSchema::type { type.toString() }
        }
        if (hasNumeric) {
            NumberColumnDescriptionSchema::mean { if (it.isNumber()) (it as DataColumn<Number?>).mean() else null }
            NumberColumnDescriptionSchema::min { if (it.isComparable()) (it as DataColumn<Comparable<Any?>>).min() else null }
            NumberColumnDescriptionSchema::max { if (it.isComparable()) (it as DataColumn<Comparable<Any?>>).max() else null }
        }
    }
    if (hasCategorical) df = df.add(ColumnDescriptionSchema::freq) {
        val top = it[ColumnDescriptionSchema::top]
        val data = cols[index]
        data.values.count { it == top }
    }.move(ColumnDescriptionSchema::freq).after(ColumnDescriptionSchema::top)

    return df.typed()
}
