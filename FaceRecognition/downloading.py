# -*- coding: utf-8 -*-
"""
Created on Mon Jan 21 18:13:28 2019

@author: Stoyan
"""

# Import gcloud
from google.cloud import storage
import os
import re
from ast import literal_eval

os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="camera-detection-73a01-firebase-adminsdk-e31px-f129000ad6.json"

# Enable Storage
client = storage.Client()

# Reference an existing bucket.
bucket = client.get_bucket('camera-detection-73a01.appspot.com')

prefix='images/'
folder = {}
char_list = ['/images']
list = {}
blobs = bucket.list_blobs(prefix=prefix)
dl_dir = 'images/'

iterator = bucket.list_blobs(prefix=prefix, delimiter='/')
prefixes = set()
for page in iterator.pages:
    prefixes.update(page.prefixes)
print (prefixes)
print (type(prefixes))
test = ', '.join(prefixes)
print (test)
        
def downloading_files():
    for blob in blobs:
        list = blob.name
            
        if list.endswith('jpg') or list.endswith('png'):
            list = re.sub("|".join(char_list), "", list)
            print(list)
            giraffeBlob = bucket.blob(list)
  
            with open(list, 'wb') as file_obj:
                giraffeBlob.download_to_file(file_obj)
                
def checking_folder():
    for folder in prefixes:
        if os.path.isdir(folder):    # True  
            print ("The Folder Already Exists")
           
        else:
            print("The Folder Doesnt Exist")
            os.mkdir(folder)

checking_folder()       
downloading_files()
