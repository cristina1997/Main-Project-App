import tkinter
import cv2 
import os
import numpy as np
from PIL import Image
import pickle
from google.cloud import storage
import re
import pyrebase
import objectpath
import time
from imutils.video import VideoStream
import imutils
import datetime
from pusher_push_notifications import PushNotifications
import sys 



#Creates the main window , sets the title of it  and the size
main = tkinter.Tk()
main.title('Authentication Box')
main.geometry('400x150')


def clear_widget(event):

    # clears out the entry when the user focuses over for the email and password
    # focus to the widgets defined below
    if username_box == main.focus_get() and username_box.get() == 'Enter Email':
        username_box.delete(0, tkinter.END)
    elif password_box == password_box.focus_get() and password_box.get() == '     ':
        password_box.delete(0, tkinter.END)

def repopulate_defaults(event):

    #basically will set the username box equal to enter username 
    #basically will set the password box equal to stars or empty
    if username_box != main.focus_get() and username_box.get() == '':
        username_box.insert(0, 'Enter Username')
    elif password_box != main.focus_get() and password_box.get() == '':
        password_box.insert(0, '     ')

def login(*event):
    #creates a email and password global so it can be accessed anywhere and sets them 
    #equal to the username and password entered
    global email, password
    email = username_box.get() 
    password = password_box.get() 
    
    #destroys main window
    main.destroy()
    
    
    
# defines a grid 50 x 50 cells in the main window
rows = 0
while rows < 10:
    main.rowconfigure(rows, weight=1)
    main.columnconfigure(rows, weight=1)
    rows += 1


# adds username entry widget and defines its properties
username_box = tkinter.Entry(main)
username_box.insert(0, 'Enter Email')
username_box.bind("<FocusIn>", clear_widget)
username_box.bind('<FocusOut>', repopulate_defaults)
username_box.grid(row=1, column=5, sticky='NS')


# adds password entry widget and defines its properties
password_box = tkinter.Entry(main, show='*')
password_box.insert(0, '     ')
password_box.bind("<FocusIn>", clear_widget)
password_box.bind('<FocusOut>', repopulate_defaults)
password_box.bind('<Return>', login)
password_box.grid(row=2, column=5, sticky='NS')


# adds login button and defines its properties
login_btn = tkinter.Button(main, text='Login', command=login)
login_btn.bind('<Return>', login)
login_btn.grid(row=5, column=5, sticky='NESW')

main.mainloop()

#sets the os environemt , google application credintials equal to the firebase admin sdk for log in etc.
os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="camera-detection-73a01-firebase-adminsdk-e31px-f129000ad6.json"

# Enable Storage
client = storage.Client()

# Referencec a bucket
bucket = client.get_bucket('camera-detection-73a01.appspot.com')

#sets the configurations for the firebase
config = {
    "apiKey": "AIzaSyBpRsAequKTjY25_ew-RutT31eE4COHU9E",
    "authDomain": "camera-detection-73a01.firebaseapp.com",
    "databaseURL": "https://camera-detection-73a01.firebaseio.com",
    "projectId": "camera-detection-73a01",
    "storageBucket": "camera-detection-73a01.appspot.com",
    "messagingSenderId": "56675147413"
}
#initializes firebase and uses the configurations above
firebase = pyrebase.initialize_app(config)
#begins authorising
auth = firebase.auth()
#sets the username and password and signs into firebase
user = auth.sign_in_with_email_and_password(email,password)
#gets the username information from the database(firebase)
users = (auth.get_account_info(user['idToken']))
#converts it to tree
tree_obj = objectpath.Tree(users)
#converts it to tuple so that it can be displayed and used later on
displayName = tuple(tree_obj.execute('$..displayName'))
#sets displayName to the actual display name of the user that logged in
displayName = (displayName[1])
#sets the prefix to images so that your in the bucket /images
prefix='images/' + displayName + '/' 
#assigns folder for future use
folder = {}
#character list putting the folder to be images and the user name
char_list = ['/images/' + displayName]
#creates a list
list = {}
#sets blobs to the bucket with the prefixes
blobs = bucket.list_blobs(prefix=prefix)
#download directory for firebase
dl_dir = 'images/' + displayName
#sets iterator to the prefix and delimiter / to find out the folders in the bucket
iterator = bucket.list_blobs(prefix=prefix, delimiter='/')
prefixes = set()
for page in iterator.pages:
    prefixes.update(page.prefixes)
#print (prefixes)
#print (type(prefixes))
test = ', '.join(prefixes)
#print (test)
    

def uploading_files():
    # Enable Storage
    client = storage.Client()
    # Enables Storage
    db = firebase.database()
    
    # Reference an existing bucket.
    bucket = client.get_bucket('camera-detection-73a01.appspot.com')
   # print (recognized)
   # print (names)
   
    #if it is recognized to save the file in the folder related to the username
    if 'recognized' in recognized :
       #Upload a local file to a new file to be created in your bucket.
        zebraBlob = bucket.blob(prefix + name + "/"  + filename)
        zebraBlob.upload_from_filename(filename=filename)
        db.child("videos").child(name).push({"name":filename})
    #if its not recognized to save the file in the folder called unkown   
    if 'unrecognized' in recognized :
        zebraBlob = bucket.blob(prefix + "unknown" + "/"  + filename)
        zebraBlob.upload_from_filename(filename=filename)
        db.child("videos").child("unknown").push({"name":filename})         
        


def downloading_files():
    #for loop to check the files that jpg and png (pictures) at the end and list them and save them locally 
    for blob in blobs:
        list = blob.name
            
        if list.endswith('jpg') or list.endswith('png'):
            list = re.sub("|".join(char_list), "", list)
            #print(list)
            giraffeBlob = bucket.blob(list)
  
            with open(list, 'wb') as file_obj:
                giraffeBlob.download_to_file(file_obj)
     
                
                
def checking_folder():
    #for loop to check if what your download is already there locally, if the folder exists
    for folder in prefixes:
        if os.path.isdir(folder):    
            print ("The Folder Already Exists")
        #if it doesnt exist then create it (the folder)   
        else:
            #print("The Folder Doesnt Exist")
            os.mkdir(folder)



def training_faces():
    #base directory , basically the directory of this file,sets the directory called images
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    image_dir = os.path.join(BASE_DIR, "images")
    
    #face classifier provided by the OpenCV library (API)
    face_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_frontalface_alt2.xml')
    #uses cv2 face recognizer 
    recognizer = cv2.face.LBPHFaceRecognizer_create()
    
    #creates variables for the training
    current_id = 0
    label_ids = {}
    y_labels = []
    x_train = []
    #for loop going through the folder
    for root, dirs, files in os.walk(image_dir):
    	for file in files:
            #looks for png and jpg files in folders
            #sets the path and label equal to the folder
            #replaces empty with - for example stoyan-rizov
    		if file.endswith("png") or file.endswith("jpg"):
    			path = os.path.join(root, file)
    			label = os.path.basename(root).replace(" ", "-").lower()
    			if not label in label_ids:
    				label_ids[label] = current_id
    				current_id += 1
             #sets the id to the label id (stoyan for example)       
    			id_ = label_ids[label]
            #converts the image to grayscale   
    			pil_image = Image.open(path).convert("L") # grayscale
             #size of image 550 , 550   
    			size = (550, 550)
             #resizes image   
    			final_image = pil_image.resize(size, Image.ANTIALIAS)
            #numpy array    
    			image_array = np.array(final_image, "uint8")
    			faces = face_cascade.detectMultiScale(image_array, scaleFactor=1.1, minNeighbors=5)
             #for loop for the faces trains the x and y labels
    			for (x,y,w,h) in faces:
    				roi = image_array[y:y+h, x:x+w]
    				x_train.append(roi)
    				y_labels.append(id_)
    #saves it in pickles face labels
    with open("pickles/face-labels.pickle", 'wb') as f:
    	pickle.dump(label_ids, f)
    #trains the data and saves it in the pickle file
    recognizer.train(x_train, np.array(y_labels))
    recognizer.save("recognizers/face-trainner.yml")
    

def face_detection():
    #sets the webcam on 
    cap = cv2.VideoCapture(0)
    #uses the cv2 cascade clasifier (frontal face)
    face_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_frontalface_alt2.xml')
    
    #sets global variabls  
    global name
    global names
    global recognized
    global filename
    global out
    global notifyName
    global iName
    
    #the duration for the video capture
    capture_duration = 10
    #sets recognized class
    recognized = [""]
    #sets filename to current date , time and mp4
    filename = ( datetime.datetime.now().strftime("%A%d%B%Y%I%M%S%p")+".mp4")
    #print (filename)
    #assigns the video writer to mp4 format
    fourcc = cv2.VideoWriter_fourcc(*'mp4v')
    #output of the file to the filename fourcc 20 frames per second and resolution 640,480
    out = cv2.VideoWriter(filename,fourcc, 20, (640,480))    
    #recognizes face using cv2 library
    recognizer = cv2.face.LBPHFaceRecognizer_create()
    recognizer.read("./recognizers/face-trainner.yml")
    
    labels = {"person_name": 1}
    with open("pickles/face-labels.pickle", 'rb') as f:
    	og_labels = pickle.load(f)
    	labels = {v:k for k,v in og_labels.items()}
    
    #print (capture_duration)
    while(True):
#    if text == ("Occupied"): 
     
        start_time = time.time()
        while(int(time.time() - start_time) < capture_duration):

    # Capture frame-by-frame
         ret, frame = cap.read()
         gray  = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
         faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5)
         for (x, y, w, h) in faces:
        	 roi_gray = gray[y:y+h, x:x+w] #(ycord_start, ycord_end)
        	 #roi_color = frame[y:y+h, x:x+w]
        	 cv2.putText(frame, datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p"),
        	 (10, frame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
           #writes the file (starts recording)  
        	 out.write(frame)
        	# recognize? deep learned model predict keras tensorflow pytorch scikit learn
        	 id_, conf = recognizer.predict(roi_gray)
        	 if conf>=3 and conf <= 85:

        			name = labels[id_]
        			names = name.split()               
        			if names in name.split() :
        				break
        			else:
        				names = name.split()
                    
        			if 'recognized' in recognized :
        				break
        			else:
        				recognized = recognized + ["recognized"]
                    
        	 else:

        			if 'unrecognized' in recognized :
        				break
        			else:
        				recognized = recognized + ["unrecognized"]  
            		                                 
    
        	 color = (255, 0, 0) #BGR 0-255 
        	 stroke = 2
        	 end_cord_x = x + w
        	 end_cord_y = y + h
        	 cv2.rectangle(frame, (x, y), (end_cord_x, end_cord_y), color, stroke)
             
        #converts it so that it can be used in push notification
        space = " "
        namez = (name.split("-"))                
        notifyName = (space.join(namez))
        #print (notifyName.capitalize())
        
        #converts it so that it can be used in push notification
        iName = displayName
        iName = name.split()
        iName = '-'.join(name)
        iName = str(name).lower()
        #print (iName)
   
        cap.release()
        out.release()
        cv2.destroyAllWindows()
        push_notification()
        motion_detection()
    
    #uploading_files()
    #main()
      
def motion_detection():

    vs = VideoStream(src=0).start()
    #time.sleep(4.0)
    global text
    
    # initialize the first frame in the video stream
    #firstFrame = None
    global firstFrame
    firstFrame = None
    min_area = 500
    # loop over the frames of the video
    while True :
    	# grab the current frame and initialize the occupied/unoccupied
    	# text
    	frame = vs.read()
    	frame = frame
    	text = "Unoccupied"
    	 
    	# if the frame could not be grabbed, then we have reached the end
    	# of the video
    	if frame is None:
    		break
    
    	# resize the frame, convert it to grayscale, and blur it
    	frame = imutils.resize(frame, width=500)
    	gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    	gray = cv2.GaussianBlur(gray, (21, 21), 0)
    
    	# if the first frame is None, initialize it
    	if firstFrame is None:
    		firstFrame = gray
    		continue
    
    	# compute the absolute difference between the current frame and
    	# first frame
    	frameDelta = cv2.absdiff(firstFrame, gray)
    	thresh = cv2.threshold(frameDelta, 25, 255, cv2.THRESH_BINARY)[1]
    
    	# dilate the thresholded image to fill in holes, then find contours
    	# on thresholded image
    	thresh = cv2.dilate(thresh, None, iterations=2)
    	cnts = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
    		cv2.CHAIN_APPROX_SIMPLE)
    	cnts = imutils.grab_contours(cnts)
    
    	# loop over the contours
    	for c in cnts:
    		# if the contour is too small, ignore it
    		if cv2.contourArea(c) < min_area:
    			continue
    
    		# compute the bounding box for the contour, draw it on the frame,
    		# and update the text
    		(x, y, w, h) = cv2.boundingRect(c)
    		cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
    		text = "Occupied"
    		
    	# draw the text and timestamp on the frame
    	cv2.putText(frame, "Room Status: {}".format(text), (10, 20),
    		cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
    	cv2.putText(frame, datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p"),
    		(10, frame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
    	
    
    	# show the frame and record if the user presses a key
    	cv2.imshow("Security Feed", frame)

    	key = cv2.waitKey(1) & 0xFF
    
    	# if the `q` key is pressed, break from the lop
    	if key == ord("q"):
    		vs.stream.release()
    		vs.stop()                        
    		cv2.destroyAllWindows()
    		sys.exit("Exiting Application!")
    		break
        
    	if text == ("Occupied"):           
    		break
    # cleanup the camera and close any open windows
    
    
    vs.stream.release()
    vs.stop()
    cv2.destroyAllWindows()
    face_detection()
    
    
      
def push_notification():
    
    
    
    beams_client = PushNotifications(
        instance_id='2f23a1d1-dc77-48d8-8474-d7dda1d9ee14',
        secret_key='BD15BDBFF5D0A9D5D9D0A42223D54F72027CAB42C527A2C626B3AE626222EC71',
            
    )
    
    response = beams_client.publish_to_interests(
        #interests=[displayName[1]],
        interests=[iName],
        publish_body={
            'apns': {
                'aps': {
                    'alert': 'Someone is at the door!'
                }
            },
            'fcm': {
                'notification': {
                    'title': notifyName.capitalize() + ' is at the door!',
                  #  'body': name
                }
            }
        }
    )

    print(response['publishId'])    
    

def main():         
  checking_folder()
  downloading_files()
  training_faces()  
  face_detection()
  motion_detection()
  uploading_files()
  
if __name__ == '__main__':
    main()

