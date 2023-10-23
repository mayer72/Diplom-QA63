# Дипломный проект по профессии «Тестировщик»
## Документация

* [План выполнения работ](https://github.com/mayer72/Diplom-QA63/blob/main/Documentation/Plan.md)
* [Отчет о проведенном тестировании](https://github.com/mayer72/Diplom-QA63/blob/main/Documentation/Report.md)
* [Отчет о проведенной автоматизации](https://github.com/mayer72/Diplom-QA63/blob/main/Documentation/Summary.md)

# Задача
 Ключевая задача — автоматизировать позитивные и негативные сценарии покупки тура.
 ![service.png](Documentation%2Fpic%2Fservice.png)
 ## Задача разбита на 4 этапа:

1. Планирование автоматизации тестирования.
2. Автоматизация процесса.
3. Подготовка отчётных документов по итогам автоматизированного тестирования.
4. Подготовка отчётных документов по итогам автоматизации.
# Запуск приложения и автотестов.
## Подготовительный этап.
* Установить и запустить IntelliJ IDEA;
* Установать и запустить Docker Desktop;
* Клонировать репозиторий с Github по [ссылке](https://github.com/mayer72/Diplom-QA63).
* Открыть проект в IntelliJ IDEA.
 1. Запустить MySQL, PostgreSQL, NodeJS через терминал командой:
``` 
docker-compose up
```
2. В новом терминале запускаем следующие команды:
 * Для MySQL:
   ```
   java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar
   ```
 * Для PostgreSQL:
   ```
   java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar
   ```
 3.  Проверить доступность приложения в браузере по адресу:
   http://localhost:8080/
4. В новом окне терминала запускаем тесты
* Для MySQL:
   ```
   ./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app"
   ```
* Для PostgreSQL:
   ```
   ./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"
   ```
## Формирование отчёта о тестировании
Отчёт формируем через Allure.  Вводим команду.
```
./gradlew allureServe
```
