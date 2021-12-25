import configparser
import pathlib

import telebot
import boto3

DEFAULT = 'default'
DB_NAME = 'tg_user.db'

HERE = pathlib.Path(__file__).parent
p = str(HERE)

cfg = configparser.ConfigParser()
cfg.read('{}/token'.format(p))

# Token
token = cfg[DEFAULT]['token']
chat_id = cfg[DEFAULT]['chat_id']

cfg.read('{}/credentials'.format(p))
key_id = cfg[DEFAULT]['aws_access_key_id']
secret_key = cfg[DEFAULT]['aws_secret_access_key']

cfg.read('{}/keys'.format(p))
folder_id = cfg[DEFAULT]['folderId']
api_key = cfg[DEFAULT]['key']
queue_name = cfg[DEFAULT]['queue_name']

cfg.read('{}/config'.format(p))
region = cfg[DEFAULT]['region']

bot = telebot.TeleBot(token)

s3 = boto3.client(
    service_name='s3',
    endpoint_url='https://storage.yandexcloud.net',
    aws_access_key_id=key_id,
    aws_secret_access_key=secret_key,
    region_name=region
)


@bot.message_handler(commands=['start'])
def start_command(message):
    bot.send_message(message.chat.id, f'Hello')
    print(message.chat.id)


@bot.message_handler(commands=['name'])
def name_command(message):
    if message.reply_to_message is not None:
        caption = message.json['reply_to_message']['caption']
        name = message.json['text'][6:]
        link_arr = caption.split("/")
        original_photo = f'{link_arr[0]}/{link_arr[1]}/{link_arr[2]}/{link_arr[3]}/{link_arr[4]}/{link_arr[6].replace("_jpg_endmain", ".jpg_endmain")}'
        if name:
            print(f'{name}')
            s3.put_object(Body=f'{name}@{original_photo}', Bucket='cloudphoto',
                          Key=f'{name}@{original_photo.replace("/", "*")}.txt')

            bot.send_message(message.chat.id, 'Данные добавлены')
        else:
            bot.send_message(message.chat.id, 'Имя не должно быть пустым. /name Имя')
    else:
        bot.send_message(message.chat.id, 'Нужно переслать сообщение с фото!')


@bot.message_handler(commands=['id'])
def chat_id_command(message):
    bot.send_message(chat_id=message.chat.id, text=f'Chat ID: {message.chat.id}')


@bot.message_handler(commands=['find'])
def find_command(message):
    print(message)
    name = message.json['text'][6:]
    print(f'{name}')
    resp = s3.list_objects_v2(Bucket='cloudphoto')
    flag = False
    obj_arr = []
    for obj in resp['Contents']:
        new_obg = obj['Key'].split('@')
        if str(new_obg[0].strip()).__eq__(str(name.strip())):
            obj_arr.append(new_obg[1])

    if len(obj_arr) > 0:
        for o in obj_arr:
            o1 = o.replace('*', '/')
            o2 = o1.replace('.txt', '')
            print(o2)
            bot.send_message(chat_id=message.chat.id, text='Вот')
            bot.send_photo(chat_id=message.chat.id, photo=f'{o2}')
    else:
        bot.send_message(chat_id=message.chat.id, text='Имя не найдено')
