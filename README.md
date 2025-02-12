# Рефакторинг баз данных и приложений
## Сценарий 2: рефакторинг уже существующего проекта
### Проект: сервис для управления доступом к защищённой локальной сети
### Студенты:
- Исупов Денис
- Новиков Егор

### Краткое описание проекта:  
Этот проект представляет собой телеграмм-бот для управления доступом к защищённой локальной сети. 

С помощью бота пользователи могут приобретать доступ для подключения к приватным ресурсам, внутренним базам данных или удалённой работе с документами и файлами. Локальная сеть обеспечивает безопасный обмен данными между пользователями и необходимыми ресурсами, гарантируя стабильное подключение. 

Также предусмотрена реферальная программа и управление промокодами для получения скидок на доступ.

## Этапы рефакторинга
### Первый этап:
1. Переструктурировать бд
2. Пересмотреть систему промокодов
3. Добавить логирование
4. Добавить сохранение действий пользователей

### Второй этап:
1. Добавить обработчик ошибок
2. Добавить валидации (например: проверка наличия пользователя в бд)
3. Добавить балансировку выдачи доступов к серверам

### Третий этап:
1. Добавить unit-тесты
2. Добавить нагрузочные тесты
