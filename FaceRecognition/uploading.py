# -*- coding: utf-8 -*-
"""
Created on Fri Jan 18 18:57:18 2019

@author: Stoyan
"""

   # Import gcloud
from google.cloud import storage
import os
os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="camera-detection-73a01-firebase-adminsdk-e31px-f129000ad6.json"

# Enable Storage
client = storage.Client()

# Reference an existing bucket.
bucket = client.get_bucket('camera-detection-73a01.appspot.com')

#Upload a local file to a new file to be created in your bucket.
zebraBlob = bucket.blob('7.png')
zebraBlob.upload_from_filename(filename='7.png')


