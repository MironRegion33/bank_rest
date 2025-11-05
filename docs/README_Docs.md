Основные эндпоинты
Пользователи (администратор)

- GET /api/admin/users – список всех пользователей;
- GET /api/admin/users/{id} – получение пользователя по id;
- POST /api/admin/users – создание пользователя;
- PATCH /api/admin/users/{id} – обновление пользователя;
- DELETE /api/admin/users/{id} – удаление пользователя.

  Карты (администратор)

- GET /api/admin/cards – список всех карт;
- POST /api/admin/cards – создание карты;
- PATCH /api/admin/cards/{id}/block – блокировка карты;
- PATCH /api/admin/cards/{id}/activate – активация карты;
- DELETE /api/admin/cards/{id} – удаление карты.

  Карты (пользователь)

- GET /api/users/{userId}/cards – просмотр своих карт, поддерживается фильтр по статусу и пагинация;
- PATCH /api/users/{userId}/cards/{cardId}/request-block – запрос блокировки своей карты;
- POST /api/users/{userId}/cards/transfer – перевод средств между своими картами.
