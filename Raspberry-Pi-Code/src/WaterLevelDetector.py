''''
Information and algorithms for the processing of the images taken by the Raspberry PI

@author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
''''


import numpy as np
import cv2
import Constants as c
from statistics import mean
import datetime
from geopy.geocoders import Nominatim

#Image processing algorithm for water level detection
class WaterLevelDetector():
    #Initialize the object
    def __init__(self, debug = False):
        self.debug = debug
        # Read using opencv functions
        self.cap=cv2.VideoCapture(0)
        #Initialize constants and variables
        self.sensorId= c.SENSORID
        self.levels = c.DETECTEDLEVELS
        self.categories = [c.GREEN, c.ORANGE, c.MAGENTA]
        self.ARMean=4.7
        self.error=0.2
        self.ARError=self.ARMean*self.error
        self.ARArray=[]
        self.extentArray=[]
        #Default UTEP address
        self.lat=31.76771355145084
        self.lng=-106.50184997506044
        #Date
        self.date=datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        #Get Address and Geolocation
        self.address=self.getAddress()
        #Severity
        self.severityList=["Low","Medium","High"]
        self.severity="Low"

    #Get address from latitude and longitude
    def getAddress(self):
        geolocator = Nominatim(user_agent="Test-app")
        location = geolocator.reverse(str(self.lat)+", "+str(self.lng))
        address=location.raw['display_name'].split(",")[0:2]
        address=", ".join(address)
        return address

    #Read Video using openCV
    def readVideo(self):
        # Capture frame-by-frame
        ret, frame = self.cap.read()
        if self.debug:
            cv2.imshow("Input Frame",frame)
        return frame

    #Improve the contrast in an image
    def equalizeHist(self,input):
        eqImg = cv2.equalizeHist(input)
        if self.debug:
            cv2.imshow('Equalized Image', eqImg)
        return eqImg

    #Image segmentation by color
    def colorSegmentation(self,currentFrame,color="red"):
        first=True
        img_hsv = cv2.cvtColor(currentFrame, cv2.COLOR_BGR2HSV)
        if color=='red':
            boundaries=[([0, 120, 70],[10, 255, 255]),
                        ([170, 120, 70],[180, 255, 255])]
        elif color=='raspi':
            boundaries=[([0, 170, 50],[255, 255, 255])]

        for(lower,upper) in boundaries:
            lower=np.array(lower,dtype="uint8")
            upper=np.array(upper,dtype="uint8")

            temp=cv2.inRange(img_hsv,lower,upper)
            if first:
                mask=np.zeros_like(temp)
                first=False
            mask=mask+temp
        if self.debug:
            cv2.imshow("Mask",mask)
        return mask

    #Apply morphologic operations
    def morphologicOperations(self,input):
        kernel3 = cv2.getStructuringElement(cv2.MORPH_RECT,(5,5))
        edges2 = cv2.morphologyEx(input, cv2.MORPH_OPEN, kernel3)
        if self.debug:
            cv2.imshow("Morphological operations", edges2)
        return edges2

    #Apply thresholding by using Otsu Method
    def thresholdingOtsu(self,input):
        # Our operations on the frame come here
        gray = cv2.cvtColor(input, cv2.COLOR_BGR2GRAY)
        if self.debug:
            cv2.imshow('Gray',gray)
        # Otsu's thresholding after Gaussian filtering
        blur = cv2.GaussianBlur(gray, (5, 5), 0)
        ret3, th3 = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
        if self.debug:
            cv2.imshow('Otsu',th3)
        return th3

    #Sorting using the second value of the list
    def sortSecond(self,val):
        return val[1]

    #Shape detection using openCV functions
    def shapeDetection(self,currentFrame,mask):
        area=0
        positions=[]
        levelMarksCnts=[]
        _,cnts,_=cv2.findContours(mask,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        output=currentFrame.copy()
        for c in cnts:
            area=cv2.contourArea(c)
            if area>800:
                # Compute the center of the contour, then detect the name of the
                # shape using only the contour
                M = cv2.moments(c)
                cX = int(M["m10"] / M["m00"])
                cY = int(M["m01"] / M["m00"])
                peri=cv2.arcLength(c,True)
                approx=cv2.approxPolyDP(c,0.01*peri,True)
                # If the shape has 4 vertices, it is either a square or
                # a rectangle
                if len(approx) == 3:
                    shape ="triangle"
                elif len(approx) == 4:
                    # Compute the bounding box of the contour and use the
                    # bounding box to compute the aspect ratio
                    (x, y, w, h) = cv2.boundingRect(approx)
                    ar = w / float(h)
                    # A square will have an aspect ratio that is approximately
                    # equal to one, otherwise, the shape is a rectangle
                    shape = "square" if ar >= 0.95 and ar <= 1.05 else "rectangle"
                    if self.ARMean>self.ARMean-self.ARError and self.ARMean<self.ARMean+self.ARError:
                        positions.append((cX,cY))
                        self.ARArray.append(ar)
                        levelMarksCnts.append(c)
                    # If the shape is a pentagon, it will have 5 vertices
                elif len(approx) == 5:
                    shape = "pentagon"
                elif 6 < len(approx) < 15:
                    shape="ellipse"
                else:
                    shape="circle"
        # Sorts the array in ascending according to second element
        positions.sort(key=self.sortSecond,reverse=True)
        #Calculate aspect ratio mean
        if len(self.ARArray)!=0:
            self.ARMean=mean(self.ARArray)
            self.ARError=self.ARMean*self.error
        self.ARArray.clear()
        offset=self.levels-len(positions)
        #Calculate offset
        if offset<0:
            offset=0
        elif offset==0:
            self.severity=self.severityList[offset]
        elif offset==1:
            self.severity=self.severityList[offset]
        elif offset==2:
            self.severity=self.severityList[offset]
        #Draw shapes detected
        for i in range(len(positions)):
            index=i+offset
            if i<self.levels:
                rect = cv2.minAreaRect(levelMarksCnts[i])
                box = cv2.boxPoints(rect)
                box = np.int0(box)
                cv2.drawContours(output,[box],0,self.categories[index],2)
                cv2.putText(output, self.severityList[index], positions[i], cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
        if self.debug:
            cv2.imshow("Shape detection", output)
        return output

    #Shape detection by using extent feature
    def shapeDetection2(self,currentFrame,mask):
        area=0
        positions=[]
        boxesCnts=[]
        _,cnts,_=cv2.findContours(mask,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        output=currentFrame.copy()
        for c in cnts:
            area=cv2.contourArea(c)
            if area>800:
                # Compute the center of the contour, then detect the name of the
                # shape using only the contour
                M = cv2.moments(c)
                cX = int(M["m10"] / M["m00"])
                cY = int(M["m01"] / M["m00"])
                #Draw min bounding rectangle
                rect = cv2.minAreaRect(c)
                box = cv2.boxPoints(rect)
                box = np.int0(box)
                #Aspect Ratio
                (w,h)=rect[1]
                ar = w / float(h)
                #Rectangle extent
                rect_area=w*h
                extent=float(area)/rect_area
                if extent>0.89:
                        positions.append((cX,cY,box))
                        self.extentArray.append(extent)
                        boxesCnts.append(box)
        # Sorts the array in ascending according to second element
        positions.sort(key=self.sortSecond,reverse=True)
        self.extentArray.clear()
        #Calculate offset
        offset=self.levels-len(positions)
        if offset<0:
            offset=0
        elif offset==0:
            self.severity=self.severityList[offset]
        elif offset==1:
            self.severity=self.severityList[offset]
        elif offset==2:
            self.severity=self.severityList[offset]
        #Draw shapes detected
        for i in range(len(positions)):
            index=i+offset
            if i<self.levels:
                cv2.drawContours(output,[positions[i][2]],0,self.categories[index],2)
                cv2.putText(output, self.severityList[index], (positions[i][0],positions[i][1]), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
        if self.debug:
            cv2.imshow("Shape detection", output)
        return output

    #Apply edge detection
    def edgeDectection(self,input):
        edges=cv2.Canny(input, 100, 200)
        if self.debug:
            cv2.imshow("edges", edges)
        return edges

    #Apply hough transformation lfor line detection
    def houghTransformation(self,input,frame):
        #Longest line
        longLine = 0
        max_dist = 0
        p1=np.array([])
        p2=np.array([])
        lines = cv2.HoughLinesP(
            input,
            rho=6,
            theta=np.pi / 60,
            threshold=160,
            lines=np.array([]),
            minLineLength=40,
            maxLineGap=25
        )
        for line in lines:
            x1, y1, x2, y2 = line[0]
            theta=np.rad2deg(np.arctan2(y2 - y1, x2 - x1))
            p1 = np.array((x1,y1))
            p2 = np.array((x2, y2))
            dist = np.linalg.norm(p1 - p2)
            angleConstraint=20
            if (dist > max_dist) and (theta<angleConstraint and theta>-angleConstraint):
                #print(theta)
                max_dist = dist
                longLine = line[0]
            #Draw All the Lines
            cv2.line(frame, (x1,y1), (x2,y2), (255, 0, 0), 3)
          #Draw Longest Line
        cv2.line(frame, (p1[0],p1[1]), (p2[0],p2[1]), (0,0,255), 3)
        if self.debug:    
            cv2.imshow("Hough Transformation",frame)
        return input

    #Draw location on image frame
    def drawLocation(self,input):
        cv2.putText(input, "Location: ", (5,20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (51, 255, 51), 2)
        cv2.putText(input, self.address, (80,20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 0), 2)
        return input

    #Draw date on image frame
    def drawDate(self,input):
        self.date=datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        cv2.putText(input, "Date: ", (5,35), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (51, 255, 51), 2)
        cv2.putText(input, self.date, (50,35), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 0), 2)
        return input

    #Draw severity on image frame
    def drawSeverity(self,input):
        cv2.putText(input, "Severity: ", (5,50), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (51, 255, 51), 2)
        cv2.putText(input, self.severity, (75,50), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 0), 2)
        return input

    #Draw final output
    def printOutput(self,input):
        cv2.imshow("Output",input)

    #Get data to send to a database
    def getData(self):
        dateTimeObj = datetime.datetime.now()
        imageName = self.getImageName()
        data = {
            "sensorId":self.sensorId,
            "severity":self.severity,
            "timestamp": dateTimeObj.timestamp(),
            "date": self.date,
            "lat":self.lat,
            "lon":self.lng,
            "imageName": imageName
            }
        return data

    def getImageName(self):
        imageName = ''.join(['_'.join([self.sensorId, '_'.join(self.date.split())]), '.jpg'])
        imageName = imageName.replace(':', '-')
        return imageName
        
    #Release resources
    def release(self):
        self.cap.release()
        cv2.destroyAllWindows()

