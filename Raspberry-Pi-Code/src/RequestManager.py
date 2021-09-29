''''
Connection to the database for the insertion of the Raspberry PI information including images.

@author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
''''


import requests
import pycurl
import cv2

class RequestManager():
    #Initialize the DBManager class
    def __init__(self, debug = False):
        self.debug = debug
        self.ip_address = "http://localhost:3000/"
        self.url_insert = self.ip_address + "raspi/insert"
        self.url_upload = self.ip_address + "images/uploadRaspi"

    #CRUD: CREATE, READ, UPDATE and DELETE
        
    #Insert data into a collection
    def insertData(self, json, file_path):
        if self.debug:
            print("Submitting data")
        self._insertJSON(json)
        self._uploadImage(json['imageName'], file_path)
        return
    
    def _insertJSON(self, json):
        response = requests.post(url = self.url_insert, data = json)
        return response.json

    #Upload image
    def _uploadImage(self, imageName, file_path):
        if self.debug:
            print("Uploading image")
        #Adding the header attribute to send to multipart
        #A script to send all the images at certain time of the day. Protocol: ftps
        #Having a local ftp to synchronize all the files in the server
        files = {'file': open(file_path ,'rb') }
        response = requests.post( url = self.url_upload, files=files)
        return response.text