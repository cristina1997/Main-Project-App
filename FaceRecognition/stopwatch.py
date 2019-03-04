# -*- coding: utf-8 -*-
"""
Created on Mon Feb 18 20:12:01 2019

@author: Stoyan
"""

import time

def stopwatch(seconds):
    start = time.time()
    time.clock()    
    elapsed = 0
    while elapsed < seconds:
        elapsed = time.time() - start
        print ("loop cycle time: %f, seconds count: %02d" % (time.clock() , elapsed)) 
        time.sleep(1)  

stopwatch(2)