# UrbanChronicle (Android)

Мобільний блог для **ВМПтФ, лабораторна робота №3** (тема з ЛР 2).

Якби класики дивилися на нашу дійсність - місто, вулиці, побут.

## Рівень 2

- CRUD статей (додати / редагувати / видалити)
- Категорії статей
- Коментарі до статей
- Стрічка з фільтром за категорією

## Стек

Kotlin, Jetpack Compose (Material 3), Room (локальна SQLite), Navigation Compose.

## Запуск

```bash
open -a "Android Studio" ~/UrbanChronicle-android
# Sync Gradle → Device Manager (API 26+) → Run app

# або
cd ~/UrbanChronicle-android
./gradlew :app:installDebug
```

Скопіюйте `local.properties.example` → `local.properties` і вкажіть `sdk.dir`, якщо Android Studio ще не створила файл.

## Репозиторій

https://github.com/LeoTkach/UrbanChronicle-android

Звіти DOCX/PDF і скріни - лише локально, не в git.
