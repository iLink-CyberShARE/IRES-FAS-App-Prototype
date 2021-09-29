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
            var db = client.db(global.db_name);
            db.collection(global.raspi_collection).find({}).sort({_id:-1}).limit(lim).toArray(function(err,result){
                if(err) throw err;
                console.log("Documents retrieved: Raspberry Pi data");
                res.setHeader('Content-Type', 'application/json');
                res.send(result);
                res.end();
                client.close();
            });
        }
    });
});

router.post('/insert',(req,res,next)=>{
    MongoClient.connect(global.mongoURL,{useNewUrlParser:true} ,function(err, client) {
        if (err) {
            console.log("Unable to connect to MongoDB",err);
        }else{
            var data=req.body;
            var sensorId = data.sensorId;
            var severity = data.severity;
            var timestamp = data.timestamp;
            var date=new Date(Date.now()-(60000*60*6)).toISOString().replace(/T/, ' ').replace(/\..+/, '');
            var lat = parseFloat(data.lat);
            var lon =parseFloat(data.lon);
            var imageName = data.imageName;
            var json={
                'sensorId': sensorId,
                'severity':severity,
                'timestamp': timestamp,
                'date':date,
                'location':{
                    'lat':lat,
                    'lon':lon
                },
                'imageName': imageName,
            };
            console.log(json);
            var db=client.db(global.db_name);
            //Reports or test collections
            db.collection(global.raspi_collection).insertOne(json, function(err, result) {
                if (err) throw err;
                console.log("1 document inserted: Raspberry Pi data");
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

module.exports = router;