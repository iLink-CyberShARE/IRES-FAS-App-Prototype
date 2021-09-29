var express = require('express');
var router = express.Router();

//Connect to MongoDB
var mongodb=require('mongodb');
var MongoClient = mongodb.MongoClient;

 router.get('/retrieve',(req,res,next)=> {
     var lim=parseInt(req.query.limit);
     console.log("Limit= ",lim);
     MongoClient.connect(global.mongoURL, {useNewUrlParser: true}, function (err, client) {
         if (err) {
             console.log("Unable to connect to MongoDB", err);
         } else {
             var db = client.db('app');
             db.collection("reports").find({}).sort({_id:-1}).limit(lim).toArray(function(err,result){
                 if(err) throw err;
                 //console.log(result);
                 console.log("Documents retrieved");
                 res.setHeader('Content-Type', 'application/json');
                 res.send(result);
                 res.end()
             });

             client.close();
         }
     });
 });

router.post('/create',(req,res,next)=>{
    MongoClient.connect(global.mongoURL,{useNewUrlParser:true} ,function(err, client) {
        if (err) {
            console.log("Unable to connect to MongoDB",err);
        }else{
            var data=req.body;
            console.log(data);
            var userID=data.userID;
            var severity=data.severity;
            var description=data.description;
            var lat=parseFloat(data.lat);
            var lon=parseFloat(data.lon);
            var date=new Date(Date.now()-(60000*60*6)).toISOString().replace(/T/, ' ').replace(/\..+/, '');
            var imageName=data.imageName;
            var json={
                'userID' : userID,
                'severity':severity,
                'description':description,
                'location':{
                    'lat':lat,
                    'lon':lon
                },
                'date':date,
                'imageName':imageName
            };
            console.log(json);
            var db=client.db('app');
            //Reports or test collections
            db.collection('reports').insertOne(json, function(err, result) {
                if (err) throw err;
                console.log("1 document inserted");
                res.setHeader('Content-Type', 'application/json');
                res.send(result["ops"]);
                res.end()
            });
            client.close();
        }
    });
});

router.get('/generateRandomReports',(req,res,next)=>{
    var json=[];
    var jsonBuffer;
    var lim=parseInt(req.query.limit);
    var latitude=parseFloat(req.query.lat);
    var longitude=parseFloat(req.query.lon);
    var severityValues=["Low","Medium","High"];

    for(var i=0;i<lim;i++) {
        var random1 = (Math.random()-.5) / 10;
        var random2 = (Math.random()-.5) / 10;
        var index = Math.floor(Math.random() * 3);
        var severity = severityValues[index];
        var description = "Random report";
        var lat = latitude + random1;
        var lon = longitude + random2;
        var date = new Date(Date.now() - (60000 * 60 * 6)).toISOString().replace(/T/, ' ').replace(/\..+/, '');
        var imageName = "test.jpg";
        jsonBuffer = {
            'severity': severity,
            'description': description,
            'location': {
                'lat': lat,
                'lon': lon
            },
            'date': date,
            'imageName': imageName
        };
        json.push(jsonBuffer);
    }
    console.log(json);

    MongoClient.connect(global.mongoURL,{useNewUrlParser:true} ,function(err, client) {
        if (err) {
            console.log("Unable to connect to MongoDB",err);
        }else{
            db=client.db('app');
                //Reports or test collections
                db.collection('reports').insertMany(json, function(err, result) {
                    if (err) throw err;
                    console.log(result["ops"]);
                });
            }
            client.close();
            res.end()
    });
});

router.post('/delete',(req,res,next)=>{
    MongoClient.connect(global.mongoURL, {useNewUrlParser: true}, function (err, client) {
        if (err) {
            console.log("Unable to connect to MongoDB",err);
        }else{
            db.collection('reports').deleteOne (json, function(err, result){
                if (err) throw err;
                json_page = {
                    '_id': ObjectID()
                }
                console.log("1 document deleted");
            })
            client.close();
        }
    })
})

module.exports = router;