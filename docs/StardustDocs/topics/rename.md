[//]: # (title: rename)

Renames one or several columns without changing its location in `DataFrame`

```kotlin
df.rename { columns }.into(name)
df.rename { columns }.into { nameExpression }

nameExpression = (DataColumn) -> String
```
