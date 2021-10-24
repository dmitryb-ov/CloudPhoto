## **Cloud Photo Беляков Дмитрий**
## Задание 2

### Шаг 1
    Создаём облачную функцию

### Шаг 2. Создание триггера
    Триггер создается для ресурса Object Storage
    Тип события: создание объекта
    Префикс ключа: main_
    Суффикс ключа: _endmain
    Выбираем сервисный аккаунт
    Создаём триггер

### Шаг 3. Редактор облачной функции
Среда выполнения: *python 3.9*

В редакторе кода создаём следующие файлы:

`config` с содержимым

    [default]
    region = ru-central1

`credentials` с содержимым

    [default]
    aws_access_key_id = <key>
    aws_secret_access_key = <key>

`keys` с содержимым
    
    [default]
    folderId = <key>
    key = Api-Key <key>

`requirenets.txt` с содержимым

    boto3==1.18.58
    Pillow==8.3.2

`main.py` с содержимым в файле `cloud_function_crop_face.py` в `src/main/resources` (https://github.com/dmitryb-ov/CloudPhoto/blob/task2/src/main/resources/cloud_function_crop_face_code.py)

##### Точка входа: `main.start`
##### Таймаут: `10`

### Принцип работы
При добавлении альбома, создается файл в бакете со следующим ключом: `main_название_альбома/название_файла.jpg_endmain`
На данный префикс `main_` и суффикс `_endmain` срабатывает триггер, отправляет фото в среду обнаружения лиц, получает координаты лиц, обрезает лица и загружает в созданный альбом с подпапкой `faces` и подпапкой, совпадающей с названием фото в котом уже хранятся обрезанные фото лиц