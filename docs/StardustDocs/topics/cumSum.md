[//]: # (title: cumSum)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes cumulative sum for selected columns of `DataFrame` or for single `DataColumn`.

```kotlin
cumSum(skipNA = true) { columns }
```

Returns a `DataFrame` or `DataColumn` containing the cumulative sum.

**Parameters:**
* `skipNA: Boolean = true` - ignore `NA` (`null` or `NaN`) values. When `false`, all values after first `NA` will be either `NaN` (for `Double` and `Float` columns) or `null` (for integer columns). 

<!---FUN cumSum-->

```kotlin
df.cumSum { weight }
df.weight.cumSum()
```

<!---END-->