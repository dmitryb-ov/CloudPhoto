import configparser
import json
import pathlib

import boto3
import telebot

DEFAULT = 'default'

HERE = pathlib.Path(__file__).parent
p = str(HERE)

cfg = configparser.ConfigParser()
cfg.read('{}/token'.format(p))

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

s3 = boto3.client(
    service_name='sqs',
    endpoint_url='https://message-queue.api.cloud.yandex.net',
    aws_access_key_id=key_id,
    aws_secret_access_key=secret_key,
    region_name=region
)
sqs_url = 'https://message-queue.api.cloud.yandex.net'
q_url = 'https://message-queue.api.cloud.yandex.net/b1gs4a51unfsngpt0hke/dj6000000003pfmr06dt/cloudphotomessagequeue'

bot = telebot.TeleBot(token)


def handler(event, context):
    global json_object
    global folder_id
    global image_path
    global message_id

    print(event)
    json_object = json.loads(json.dumps(event))

    folder_id = json_object['messages'][0]['event_metadata']['folder_id']
    image_path = json_object['messages'][0]['details']['message']['message_attributes']['string']['string_value']
    message_id = json_object['messages'][0]['details']['message']['message_id']

    body = event['messages'][0]['details']['message']['body']
    print(f'TAG1 {body}')
    send_tg_message(image_path)

    return {
        'statusCode': 200,
        'body': 'Ok',
    }


def send_tg_message(msg):
    bot.send_message(chat_id=chat_id, text='Кто это?')
    bot.send_photo(chat_id=chat_id, photo=f'https://storage.yandexcloud.net/cloudphoto/{msg}', caption=f'https://storage.yandexcloud.net/cloudphoto/{msg}')
