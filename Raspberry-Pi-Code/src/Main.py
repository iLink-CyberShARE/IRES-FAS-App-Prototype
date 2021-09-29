''''
Main class for the continuous reading of the raspberry PI information.

@author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
''''


from WaterLevelDetector import WaterLevelDetector
from RequestManager import RequestManager
from TimeManager import TimeManager
import Constants as c
import cv2

def main():
    #Variables declaration
    wlv=WaterLevelDetector(debug = False)
    requestManager = RequestManager(debug = True)
    timer1 = TimeManager()

    while(True):
        #Read from video
        initialFrame=wlv.readVideo()
        currentFrame=initialFrame.copy()

        #Segmentation
        mask=wlv.colorSegmentation(currentFrame,"red")

        #Morphological operations
        mask=wlv.morphologicOperations(mask)

        #Shape detection
        currentFrame=wlv.shapeDetection2(currentFrame,mask)

        #Location
        currentFrame=wlv.drawLocation(currentFrame)

        #Date
        currentFrame=wlv.drawDate(currentFrame)

        #Severity
        currentFrame=wlv.drawSeverity(currentFrame)

        #Print output
        wlv.printOutput(currentFrame)

        # Send data to a database
        data = wlv.getData()
        
        #Collect data
        if timer1.eventTrigger(c.DATAPERIOD):
            file_path = ''.join([ './Images/', data['imageName']])
            print(file_path)
            cv2.imwrite(file_path, currentFrame)
            requestManager.insertData(data, file_path)
    
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # When everything done, release the capture
    wlv.release()

if __name__ == "__main__":
    main()
