''''
Information for the connection to the database to insert and find data.

@author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
''''


from pymongo import MongoClient
#Class for using MongoDB on python
class DBManager():
    #Initialize the DBManager class
    def __init__(self):
        # Use your local ip address
        self.mongoURL = "mongodb://localhost:27017/"
        self.ip_address = "http://localhost:4000/"
        self.db_name = "app"
        self.flooding_col_name = "waterLevel"
        self.client = MongoClient()
        self.db = self.client[self.db_name]
        self.collection = self.db[self.flooding_col_name]

    #CRUD: CREATE, READ, UPDATE and DELETE
        
    #Insert data into a collection
    def insert_one(self, data):
        doc = self.collection.insert_one(data)
        return doc.inserted_id
    
    #Find all the documents in a collection
    def find(self):
        return [doc for doc in self.collection.find()]

