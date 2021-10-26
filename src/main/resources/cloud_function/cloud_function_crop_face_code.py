import io
import time

from PIL import Image
import json
import boto3
import base64
import configparser
import pathlib
import requests
from io import BytesIO

DEFAULT = 'default'
JPEG = "JPEG"

HERE = pathlib.Path(__file__).parent
p = str(HERE)

cfg = configparser.ConfigParser()
cfg.read('{}/config'.format(p))
region = cfg[DEFAULT]['region']

cfg.read('{}/credentials'.format(p))
key_id = cfg[DEFAULT]['aws_access_key_id']
secret_key = cfg[DEFAULT]['aws_secret_access_key']

cfg.read('{}/keys'.format(p))
folder_id = cfg[DEFAULT]['folderId']
api_key = cfg[DEFAULT]['key']
queue_name = cfg[DEFAULT]['queue_name']

s3 = boto3.client(
    service_name='s3',
    endpoint_url='https://storage.yandexcloud.net',
    aws_access_key_id=key_id,
    aws_secret_access_key=secret_key,
    region_name=region
)

cloud_vision_url = 'https://vision.api.cloud.yandex.net/vision/v1/batchAnalyze'
sqs_url = 'https://message-queue.api.cloud.yandex.net'


def start(event, context):
    print(event)
    global json_object
    global bucket_id
    global object_id
    json_object = json.loads(json.dumps(event))
    bucket_id = json_object['messages'][0]['details']['bucket_id']
    object_id = json_object['messages'][0]['details']['object_id']

    file = s3.get_object(Bucket=bucket_id, Key=object_id)
    file_content = file['Body'].read()
    send_vision_request(base64.b64encode(file_content).decode('utf-8'))


def send_vision_request(base64_file):
    vision_json = {'folderId': f'{folder_id}',
                   'analyze_specs':
                       [
                           {
                               'content': base64_file,
                               'features':
                                   [
                                       {'type': 'FACE_DETECTION'}
                                   ]
                           }
                       ]
                   }

    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'{api_key}'
    }

    response = requests.post(cloud_vision_url, headers=headers, data=json.dumps(vision_json))

    get_faces(response, base64_file)


def get_faces(response, base64_file):
    response_json = json.loads(response.text)
    faces = response_json['results'][0]['results'][0]['faceDetection']['faces']

    paths = []
    for face in list(faces):
        coords = face['boundingBox']['vertices']
        img_main = Image.open(BytesIO(base64.b64decode(base64_file)))
        img_crop = img_main.crop((int(coords[0]['x']), int(coords[0]['y']),
                                  int(coords[2]['x']), int(coords[2]['y'])))

        buffer = io.BytesIO()
        img_crop.save(buffer, JPEG)
        buffer.seek(0)

        first_path = object_id.split('/')[0]
        faces_file_name = object_id.split('/')[-1].replace('.', '_')
        final_path = f'{first_path}/faces/{faces_file_name}/{time.time()}.jpg'

        s3.put_object(
            Bucket=bucket_id,
            Key=f'{final_path}',
            Body=buffer,
            ContentType='image/jpeg'
        )
        paths.append(final_path)

    send_to_queue(get_message_queue(), paths)


def get_message_queue():
    session = boto3.session.Session()
    return session.resource(
        service_name='sqs',
        endpoint_url=sqs_url,
        aws_access_key_id=key_id,
        aws_secret_access_key=secret_key,
        region_name=region
    )


def send_to_queue(sqs, paths):
    queue = sqs.get_queue_by_name(QueueName=f'{queue_name}')
    queue.send_message(
        MessageBody=f'Object Id: {object_id}',
        MessageAttributes={
            'string': str(paths),
            'DataType': 'string'
        }
    )
