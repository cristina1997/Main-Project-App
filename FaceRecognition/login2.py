# -*- coding: utf-8 -*-
"""
Created on Thu Feb 14 19:23:10 2019

@author: Stoyan
"""

import firebase_admin
from firebase_admin import credentials

cred = credentials.Cert('camera-detection-73a01-firebase-adminsdk-e31px-f129000ad6.json')
firebase_admin.initialize_app(cred, {
    'databaseURL' : 'https://camera-detection-73a01.firebaseio.com'
})

from firebase_admin import db

root = db.reference()

new_user = root.child('users').push({
    'name' : 'Mary Anning', 
    'since' : 1700
})

mary = db.reference('users/{0}'.format(new_user.key)).get()
print ('Name:', mary['name'])