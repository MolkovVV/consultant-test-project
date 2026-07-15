# Consultant.ru AutoTest

Production-ready UI automation: **Java 17 · Selenium 4 · JUnit 5 · Allure · Owner · WebDriverManager**.

## Стек

| Компонент | Назначение |
|-----------|------------|
| Page Object + fluent API | Читаемые сценарии |
| `WebDriverExtension` | Жизненный цикл браузера, diagnostics при падении |
| `RetryExtension` | Повтор с новой сессией WebDriver |
| `BrowserBinaryResolver` | Автоопределение пути к браузеру (OS + PATH) |
| Owner | Type-safe конфигурация |
| JUnit 5 parallel | Многопоточный запуск тестов |

## Конфигурация

`src/test/resources/config.properties` — базовые настройки.  
Переопределение: `-Dkey=value` или env-переменные (`KEY`).

| Параметр | Описание | По умолчанию |
|----------|----------|--------------|
| `baseUrl` | URL приложения | `https://www.consultant.ru/cons/` |
| `browser` | CHROME / EDGE / FIREFOX / REMOTE_* | CHROME |
| `browserBinaryPath` | Явный путь к браузеру | — |
| `browserBinaryAutoDetect` | Автоопределение бинарника | true |
| `headless` | Headless-режим | false |
| `timeout.seconds` | Explicit wait | 15 |
| `pageLoad.timeout.seconds` | Page load timeout | 60 |
| `retryCount` | Ретраи упавшего теста | 0 |
| `parallel.enabled` | Параллельный запуск | false |
| `parallel.threads` | Число потоков | 2 |

Тестовые данные: `src/test/resources/testdata.properties`.

## Запуск

```bash
# Локально
mvn clean test

# Headless + параллельно (CI)
mvn clean test -Dheadless=true -Dparallel.enabled=true -Dparallel.threads=4

# Allure-отчёт
mvn allure:serve
```

## BrowserBinaryResolver

Порядок поиска бинарника:

1. `browserBinaryPath` из конфига (если файл существует)
2. Типовые пути для OS (Program Files, `/Applications`, `/usr/bin`)
3. Каталоги из переменной `PATH`

Если бинарник не найден — WebDriverManager использует браузер по умолчанию.

## Структура

```
src/test/java/com/consultant/
├── config/          TestConfig, BrowserBinaryResolver, TestData
├── extensions/      WebDriverExtension, RetryExtension, DriverContext
├── pages/           MainPage, DocumentPage, WebDriverFactory
├── pages/helpers/   BaseTest, FrameHelper
├── tests/           ConsultantTest
└── utils/           XPathUtils
```

## Многопоточность

- Каждый тест получает **изолированный WebDriver** через `ExtensionContext.Store`
- `@Execution(CONCURRENT)` на тест-классах
- Параллелизм включается через `-Dparallel.enabled=true`
- Для CI рекомендуется `parallel.threads` = число CPU / 2

