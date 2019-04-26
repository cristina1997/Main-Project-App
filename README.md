# Webcam Face Recognition Door Security System

### 4th Year Final Project
### Cristina Nita & Stoyan Rizov


## Introduction
Webcam face recognition door security system capabale of recording, recognizing visitors to the house or intruders; and being able to handle the situation by sending the house owners notifications to their mobile devices to let them know someone is at the door. Users have the capablity of registering and login in (authentication). In emergency situation user is able to call the police.


## Android Application

### Features

##### Authentication:
User can log in with username and password.

##### Registration:
User can register with their name, username and password.

##### Face Recognition:
Visitors are recognised if they are in the database if not they are stored and displayed as unkown. 

##### Pull Notification:
As soon as video is finished recording and recognizing a pull notification is receibed by the android user with the name of the person in the video.

##### Upload / Download Firebase Files:
The user can upload images to the database as well as download videos from the firebase.

##### Emergency Call:
The user can call 911 directly from the mobile.

### Running Application
To run application simply clone this repository, navigate to CameraDetection folder
> Use Android Studio to run the project

## Server-Side
### Libraries required
- tkinter (python-tk)
- cv2 (opencv-contrib-python)
- os 
- numpy (numpy)
- PIL (Pillow)
- pickle (pickle-mixin)
- google.cloud (google-cloud)
- pyrebase (Pyrebase)
- re
- json
- VideoStream
- imutils

### Installing the libraries 
Make sure to install all libraries as shown above replace python-tk with the library name.
> pip install python-tk

### Server-Side Features 

##### Authentication:
User can log in with username and password.

##### Face Recognition:
Visitors are recognised if they are in the database if not they are stored and displayed as unkown. 

##### Motion Detection:
Motion detection starts up and if there is any movement , motion detection window is closed and video is being recorded
for 10 seconds with face recognition.

##### Push Notification:
As soon as video is finished recording and recognizing push notification is sent to the android user.

##### Upload / Download Firebase Files:
Every time a video is recorded it is uploaded on the firebase database storage so that the android application 
can download the video and view it.Every time a user logs in to the database his/her folder is downloaded locally 
with the pictures related to that account.


### Running Application
To run application simply clone this repository,navigate to FaceRecognition folder
> Run the script: python FaceDetection.py
> or you can use any python environment compiler (I used Spyder IDE ).

## Tasks Completed

### Stoyan Rizov
- Created Face Recognition (Server Side)
- Trained the data / faces (Server Side)
- Designed GUI  (Server Side)
- Organized Firebase Storage 
- Installed Rasberry Pi libraries 
- Created Motion Detection (Server Side)
- Push Notifications (Server Side)
- Upload / Download files to/ from Firebase

### Cristina Nita
- User Authentification Login/Registration (Server Side)
- Uploading Images (Client Side)
- Downloading Videos (Client Side)
- Firebase Database Creation
- Emergency Call (Client Side)
- Pull Notifications (Client Side)
- Download videos from Firebase

### Team Work
- Installed Rasbian on Rasberry Pi
- Final Report / Dissertation
- Presentation
- Pusher (Notifications)
