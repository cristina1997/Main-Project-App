# -*- coding: utf-8 -*-
"""
Created on Thu Feb 14 18:11:40 2019

@author: Stoyan
"""

import pyrebase
import json
import objectpath

def login_user():
    config = {
        "apiKey": "AIzaSyBpRsAequKTjY25_ew-RutT31eE4COHU9E",
        "authDomain": "camera-detection-73a01.firebaseapp.com",
        "databaseURL": "https://camera-detection-73a01.firebaseio.com",
        "projectId": "camera-detection-73a01",
        "storageBucket": "camera-detection-73a01.appspot.com",
        "messagingSenderId": "56675147413"
    }
    
    firebase = pyrebase.initialize_app(config)
    
    auth = firebase.auth()
    
    email = input ('Please enter your email\n')
    password = input ('Please enter your password\n')
    
    #user = auth.create_user_with_email_and_password(email,password)
    user = auth.sign_in_with_email_and_password(email,password)
    
    users = (auth.get_account_info(user['idToken']))
    
    json.dump(users, open("users.txt",'w'))
    
    tree_obj = objectpath.Tree(users)
    
    displayName = tuple(tree_obj.execute('$..displayName'))
    
    print (displayName[1])





