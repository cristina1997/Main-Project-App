# -*- coding: utf-8 -*-
"""
Created on Mon Feb 11 16:12:19 2019

@author: Stoyan
"""
import cv2
import os
import numpy as np
from PIL import Image
import pickle
from google.cloud import storage
import re
import pyrebase
import json
import objectpath

os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="camera-detection-73a01-firebase-adminsdk-e31px-f129000ad6.json"

# Enable Storage
client = storage.Client()

# Reference an existing bucket.
bucket = client.get_bucket('camera-detection-73a01.appspot.com')




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

displayName = (displayName[1])


prefix='images/' + displayName + '/' 
folder = {}
char_list = ['/images/' + displayName]
list = {}
blobs = bucket.list_blobs(prefix=prefix)
dl_dir = 'images/' + displayName

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
        if os.path.isdir(folder):    
            print ("The Folder Already Exists")
           
        else:
            print("The Folder Doesnt Exist")
            os.mkdir(folder)



checking_folder()       
downloading_files()




def training_faces():

    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    image_dir = os.path.join(BASE_DIR, "images")
    
    face_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_frontalface_alt2.xml')
    recognizer = cv2.face.LBPHFaceRecognizer_create()
    
    
    current_id = 0
    label_ids = {}
    y_labels = []
    x_train = []
    
    for root, dirs, files in os.walk(image_dir):
    	for file in files:
    		if file.endswith("png") or file.endswith("jpg"):
    			path = os.path.join(root, file)
    			label = os.path.basename(root).replace(" ", "-").lower()
    			#print(label, path)
    			if not label in label_ids:
    				label_ids[label] = current_id
    				current_id += 1
    			id_ = label_ids[label]
    			#print(label_ids)
    			#y_labels.append(label) # some number
    			#x_train.append(path) # verify this image, turn into a NUMPY arrray, GRAY
    			pil_image = Image.open(path).convert("L") # grayscale
    			size = (550, 550)
    			final_image = pil_image.resize(size, Image.ANTIALIAS)
    			image_array = np.array(final_image, "uint8")
    			#print(image_array)
    			faces = face_cascade.detectMultiScale(image_array, scaleFactor=1.1, minNeighbors=5)
    
    			for (x,y,w,h) in faces:
    				roi = image_array[y:y+h, x:x+w]
    				x_train.append(roi)
    				y_labels.append(id_)
    
    with open("pickles/face-labels.pickle", 'wb') as f:
    	pickle.dump(label_ids, f)
    
    recognizer.train(x_train, np.array(y_labels))
    recognizer.save("recognizers/face-trainner.yml")
    




training_faces()





def face_detection():

    face_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_frontalface_alt2.xml')
   # eye_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_eye.xml')
  #  smile_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_smile.xml')
    
    
    recognizer = cv2.face.LBPHFaceRecognizer_create()
    recognizer.read("./recognizers/face-trainner.yml")
    
    labels = {"person_name": 1}
    with open("pickles/face-labels.pickle", 'rb') as f:
    	og_labels = pickle.load(f)
    	labels = {v:k for k,v in og_labels.items()}
    
    cap = cv2.VideoCapture(0)
    
    while(True):
    # Capture frame-by-frame
        ret, frame = cap.read()
        gray  = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5)
        for (x, y, w, h) in faces:
        	#print(x,y,w,h)
        	roi_gray = gray[y:y+h, x:x+w] #(ycord_start, ycord_end)
        	roi_color = frame[y:y+h, x:x+w]
    
        	# recognize? deep learned model predict keras tensorflow pytorch scikit learn
        	id_, conf = recognizer.predict(roi_gray)
        	if conf>=4 and conf <= 85:
        		#print(5: #id_)
        		#print(labels[id_])
        		#print(conf)
        		font = cv2.FONT_HERSHEY_SIMPLEX
        		name = labels[id_]
        		color = (255, 255, 255)
        		stroke = 2
        		cv2.putText(frame, name, (x,y), font, 1, color, stroke, cv2.LINE_AA)
        	else:
        		cv2.putText(frame, "unrecognized", (x,y), font, 1, color, stroke, cv2.LINE_AA)
        		#unrecognized = "unrecognized.png"
        		#cv2.imwrite(unrecognized,roi_color)                
                
        	img_item = "7.png"
        	cv2.imwrite(img_item, roi_color)
    
        	color = (255, 0, 0) #BGR 0-255 
        	stroke = 2
        	end_cord_x = x + w
        	end_cord_y = y + h
        	cv2.rectangle(frame, (x, y), (end_cord_x, end_cord_y), color, stroke)
        	#subitems = smile_cascade.detectMultiScale(roi_gray)
        	#for (ex,ey,ew,eh) in subitems:
        	#	cv2.rectangle(roi_color,(ex,ey),(ex+ew,ey+eh),(0,255,0),2)
        # Display the resulting frame
        cv2.imshow('frame',frame)
        if cv2.waitKey(20) & 0xFF == ord('q'):
            break

    # When everything done, release the capture
    cap.release()
    cv2.destroyAllWindows()
    



face_detection()
