## **Cloud Photo Беляков Дмитрий**

Перед скачиванием необходимо в `~/.aws` создать файл `credentials` и `config` с `access key` и `secret key` от сервисного аккаунта `Yandex Cloud`. Подробнее: https://cloud.yandex.ru/docs/storage/tools/aws-sdk-java

Без данного действия будет невозможен доступ в Яндекс.Обалко

### Запуск приложения происходит путем скачивания готового архива или же собственная сборка

#### 1. Скачивание готового архива
    1.1 Скачиваем архив cloudphoto.rar
https://disk.yandex.ru/d/LjCu_I-MVZSfWw

    1.2 Распаковываем в нужную директорию и переходим в каталог **cloudphoto/**

    1.3 Открываем консоль в текущем каталоге
    
    1.3 Прописываем в консоли одну из следующих нужных нам комманд

`java -jar cloudphoto.jar upload -a album -p  'source path'` - загрузить фото из папки в альбом

`java -jar cloudphoto.jar download -a album -p path` - скачать фото из альбома в папку

`java -jar cloudphoto.jar list -a album` - показать список фото в альбоме

`java -jar cloudphoto.jar list` - показать список альбомов


#### 2. Сборка архива на основе исходного кода
    2.1 Скачиваем проект
    
    2.2 В каталоге в консоли прописываем `mvn package`

    2.3 Дожидаемся сборки

    2.4 Прописываем в консоли одну из следующих нужных нам комманд


`java -jar cloudphoto.jar upload -a album -p  'source path'` - загрузить фото из папки в альбом

`java -jar cloudphoto.jar download -a album -p 'path'` - скачать фото из альбома в папку

`java -jar cloudphoto.jar list -a album` - показать список фото в альбоме

`java -jar cloudphoto.jar list` - показать список альбомов
