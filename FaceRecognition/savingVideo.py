# -*- coding: utf-8 -*-
"""
Created on Mon Feb 18 20:49:31 2019

@author: Stoyan
"""

import numpy as np
import cv2
import time


#
#cap = cv2.VideoCapture(0)
#
## Define the codec and create VideoWriter object
#fourcc = cv2.VideoWriter_fourcc(*'XVID')
#out = cv2.VideoWriter('output.avi',fourcc, 20.0, (640,480))
#
#while(cap.isOpened()):
#    ret, frame = cap.read()
#    if ret==True:
#        ret, frame = cap.read()
#
#        # write the flipped frame
#        out.write(frame)
#
#        cv2.imshow('frame',frame)
#        if cv2.waitKey(1) & 0xFF == ord('q'):
#            break
#    else:
#        break
#
## Release everything if job is finished
#cap.release()
#out.release()
#cv2.destroyAllWindows()





# Define the codec and create VideoWriter object
fourcc = cv2.VideoWriter_fourcc(*'XVID')
out = cv2.VideoWriter('output.avi',fourcc, 20.0, (640,480))
t0 = time.time() # start time in seconds

cap = cv2.VideoCapture(0)
while(True):
   ret, frame = cap.read()
   # ... processing or preview
   
   out.write(frame)
   
   t1 = time.time() # current time
   num_seconds = t1 - t0 # diff
   if num_seconds > 10:  # e.g. break after 30 seconds
      break
  
# Release everything if job is finished
cap.release()
out.release()
cv2.destroyAllWindows()