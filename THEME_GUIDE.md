# 🎨 Система тем Schedule App

## 📁 Структура файлов

```
ui/theme/
├── Color.kt       - Все цвета приложения
├── Theme.kt       - Цветовые схемы и главная тема
└── Typography.kt  - Типография (размеры шрифтов)
```

---

## 🎨 Как использовать цвета в коде

### 1. Базовое использование (рекомендуется)

```kotlin
@Composable
fun MyScreen() {
    Text(
        text = "Привет",
        color = MaterialTheme.colorScheme.primary  // ✅ Автоматически меняется при смене темы
    )
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        // Содержимое карточки
    }
}
```

### 2. Прямое использование цветов (для особых случаев)

```kotlin
import com.example.sheduleapp.ui.theme.Primary
import com.example.sheduleapp.ui.theme.MondayColor

@Composable
fun MyComponent() {
    Box(
        modifier = Modifier.background(Primary)  // Всегда фиолетовый
    )
    
    Text(
        text = "Понедельник",
        color = MondayColor  // Синий цвет для понедельника
    )
}
```

---

## 🌓 Светлая и темная темы

### Автоматическое переключение

```kotlin
@Composable
fun App() {
    ScheduleAppTheme {  // Автоматически определяет тему системы
        ScheduleScreen()
    }
}
```

### Принудительная тема

```kotlin
@Composable
fun App() {
    ScheduleAppTheme(darkTheme = true) {  // Всегда темная
        ScheduleScreen()
    }
    
    // или
    
    ScheduleAppTheme(darkTheme = false) {  // Всегда светлая
        ScheduleScreen()
    }
}
```

### Переключение темы по состоянию

```kotlin
@Composable
fun App() {
    var isDarkTheme by remember { mutableStateOf(false) }
    
    ScheduleAppTheme(darkTheme = isDarkTheme) {
        Column {
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { isDarkTheme = it }
            )
            ScheduleScreen()
        }
    }
}
```

---

## 📊 Все доступные цвета

### Material Design 3 цвета (автоматически меняются)

```kotlin
MaterialTheme.colorScheme.primary           // Основной цвет
MaterialTheme.colorScheme.onPrimary         // Текст на primary
MaterialTheme.colorScheme.primaryContainer  // Контейнер primary
MaterialTheme.colorScheme.onPrimaryContainer // Текст на primaryContainer

MaterialTheme.colorScheme.secondary         // Второстепенный цвет
MaterialTheme.colorScheme.onSecondary
MaterialTheme.colorScheme.secondaryContainer
MaterialTheme.colorScheme.onSecondaryContainer

MaterialTheme.colorScheme.tertiary          // Третичный цвет
MaterialTheme.colorScheme.onTertiary
MaterialTheme.colorScheme.tertiaryContainer
MaterialTheme.colorScheme.onTertiaryContainer

MaterialTheme.colorScheme.error             // Ошибки
MaterialTheme.colorScheme.onError
MaterialTheme.colorScheme.errorContainer
MaterialTheme.colorScheme.onErrorContainer

MaterialTheme.colorScheme.background        // Фон приложения
MaterialTheme.colorScheme.onBackground

MaterialTheme.colorScheme.surface           // Поверхности (карточки)
MaterialTheme.colorScheme.onSurface
MaterialTheme.colorScheme.surfaceVariant
MaterialTheme.colorScheme.onSurfaceVariant

MaterialTheme.colorScheme.outline           // Границы
MaterialTheme.colorScheme.outlineVariant
```

### Цвета дней недели (постоянные)

```kotlin
MondayColor       // Синий
TuesdayColor      // Зеленый
WednesdayColor    // Оранжевый
ThursdayColor     // Красный
FridayColor       // Фиолетовый
SaturdayColor     // Бирюзовый
SundayColor       // Коричневый

// Или через функцию:
getDayColor(0)    // Понедельник
getDayColor(6)    // Воскресенье
```

### Цвета для событий расписания

```kotlin
ScheduleEventPrimary      // Основной фиолетовый
ScheduleEventSecondary    // Серый
ScheduleEventSuccess      // Зеленый (успех)
ScheduleEventWarning      // Оранжевый (предупреждение)
ScheduleEventInfo         // Синий (информация)
```

---

## 📝 Типография

### Использование стилей текста

```kotlin
@Composable
fun MyScreen() {
    // Заголовки
    Text(
        text = "Большой заголовок",
        style = MaterialTheme.typography.headlineLarge
    )
    
    Text(
        text = "Средний заголовок",
        style = MaterialTheme.typography.headlineMedium
    )
    
    Text(
        text = "Маленький заголовок",
        style = MaterialTheme.typography.headlineSmall
    )
    
    // Названия
    Text(
        text = "Название секции",
        style = MaterialTheme.typography.titleLarge
    )
    
    Text(
        text = "Название карточки",
        style = MaterialTheme.typography.titleMedium
    )
    
    // Основной текст
    Text(
        text = "Обычный текст",
        style = MaterialTheme.typography.bodyLarge
    )
    
    Text(
        text = "Описание",
        style = MaterialTheme.typography.bodyMedium
    )
    
    Text(
        text = "Подпись",
        style = MaterialTheme.typography.bodySmall
    )
    
    // Метки и кнопки
    Text(
        text = "Кнопка",
        style = MaterialTheme.typography.labelLarge
    )
    
    Text(
        text = "Метка",
        style = MaterialTheme.typography.labelSmall
    )
}
```

---

## 🎯 Примеры использования

### Пример 1: Карточка события

```kotlin
@Composable
fun EventCard(event: EventDto) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant  // Фон карточки
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface  // Текст на поверхности
            )
            Text(
                text = event.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant  // Второстепенный текст
            )
        }
    }
}
```

### Пример 2: Заголовок дня с цветом

```kotlin
@Composable
fun DayHeader(day: String, dayIndex: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = getDayColor(dayIndex).copy(alpha = 0.1f)  // Прозрачный цвет дня
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(getDayColor(dayIndex), CircleShape)  // Индикатор цвета
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = day,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
```

### Пример 3: Кнопка с темным фоном

```kotlin
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
```

---

## 🔧 Как изменить цвета

### Изменить основной цвет приложения

Открой `Color.kt` и измени:

```kotlin
val Primary = Color(0xFF6200EE)  // Фиолетовый
// на
val Primary = Color(0xFF2196F3)  // Синий
```

### Добавить новый цвет

В `Color.kt`:

```kotlin
// Добавь в конец файла
val CustomColor = Color(0xFFFF5722)
```

Используй в коде:

```kotlin
import com.example.sheduleapp.ui.theme.CustomColor

Box(modifier = Modifier.background(CustomColor))
```

### Создать альтернативную тему

В `Theme.kt`:

```kotlin
private val BlueColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color(0xFFFFFFFF),
    // ... остальные цвета
)

@Composable
fun ScheduleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useBlueTheme: Boolean = false,  // Новый параметр
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useBlueTheme -> BlueColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
```

---

## ✅ Best Practices

1. **Всегда используй `MaterialTheme.colorScheme`** вместо прямых цветов
   - ✅ `color = MaterialTheme.colorScheme.primary`
   - ❌ `color = Color(0xFF6200EE)`

2. **Используй правильные пары цветов**
   - `primary` + `onPrimary`
   - `surface` + `onSurface`
   - `background` + `onBackground`

3. **Используй типографию из темы**
   - ✅ `style = MaterialTheme.typography.titleMedium`
   - ❌ `fontSize = 16.sp`

4. **Тестируй в обеих темах**
   - Проверяй светлую и темную темы
   - Убедись, что текст читаемый

5. **Используй прозрачность для теней**
   ```kotlin
   color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
   ```

---

## 🐛 Troubleshooting

### Цвета не меняются при смене темы

**Проблема:** Использовал прямой цвет вместо `MaterialTheme.colorScheme`

```kotlin
// ❌ Плохо
Text(color = Color(0xFF6200EE))

// ✅ Хорошо
Text(color = MaterialTheme.colorScheme.primary)
```

### Текст не видно на фоне

**Проблема:** Неправильная пара цветов

```kotlin
// ❌ Плохо
Box(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
    Text(color = MaterialTheme.colorScheme.onSurface)  // Может быть не видно
}

// ✅ Хорошо
Box(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
    Text(color = MaterialTheme.colorScheme.onPrimary)  // Правильная пара
}
```

---

Теперь у тебя полная система тем! 🎉

