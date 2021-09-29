''''
Time event for the collection of data by the Raspberry PI

@author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
''''


import time

class TimeManager():
    #Initialize the DBManager class
    def __init__(self):
        self.start = time.time()
    
    #Trigger an event
    def eventTrigger(self, period):
        end = time.time()
        duration = end - self.start
        if duration>period:
            self.start = end
            return True
        return False