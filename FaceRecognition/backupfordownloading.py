# -*- coding: utf-8 -*-
"""
Created on Mon Jan 21 18:13:28 2019

@author: Stoyan
"""

# Import gcloud
from google.cloud import storage
import os
os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="camera-detection-73a01-firebase-adminsdk-e31px-f129000ad6.json"

import numpy as np
import cv2
import urllib
import collections
# Enable Storage
client = storage.Client()

# Reference an existing bucket.
bucket = client.get_bucket('camera-detection-73a01.appspot.com')

#current_id = 0
#label_ids = {}



blobs = bucket.list_blobs()
file='test/'
delimiter='/'
folder='./'
#blobs = bucket.list_blobs(prefix='/test/')


#Fetching the images from the firebase storage 
#for idX in range(1,len(result)):
#        for imageY in range(0,64):
#            pathWay='test/' + str(idX) + '.' + str(imageY)
#            print(pathWay)
#            imageBlob = bucket.blob(pathWay)
#            print(imageBlob)
#            url = imageBlob.public_url
#            req = urllib.urlopen(url)
#            arr = np.asarray(bytearray(req.read()),dtype=np.uint8)
#            imp = cv2.imdecode(arr,-1)
#            cv2.imshow("image",img)
#            cv2.waitKey(10)

import re
char_list = ['/test']

list = {}

for blob in blobs:
    list = blob.name
    if list.endswith('jpg') or list.endswith('png'):
        list = re.sub("|".join(char_list), "", list)
 #       print(list)
        giraffeBlob = bucket.blob(list)
        print (giraffeBlob)
        with open(list, 'wb') as file_obj:
            giraffeBlob.download_to_file(file_obj)



# Create this folder locally
#if not os.path.exists(folder1):
#    os.makedirs(folder1)

# Retrieve all blobs with a prefix matching the folder
#print(bucket)
#blobs=list(bucket.list_blobs(prefix=folder1))
#print(blobs)
#for blob in blobs:
#    if(not blob.name.endswith("/")):
#        blob.download_to_filename(blob.name)

# [End download to multiple files]



#print('Blobs:')
#for blob in blobs:
#    print(blob.name)

                
        
#giraffeBlob = bucket.blob('7.png')
#with open("7.png", 'wb') as file_obj:
 #   giraffeBlob.download_to_file(file_obj)