var express = require("express");
var router = express.Router();

//Connect to MongoDB
var mongodb = require("mongodb");
var MongoClient = mongodb.MongoClient;

router.post("/create", (req, res, next) => {
  MongoClient.connect(global.mongoURL, { useNewUrlParser: true }, function (
    err,
    client
  ) {
    if (err) {
      console.log("Unable to connect to MongoDB", err);
    } else {
      var data = req.body;
      console.log(data);
      var userID = data.userID;
      var severity = data.severity;
      var credibility = data.credibility;
      var description = data.description;
      var date = new Date(Date.now() - 60000 * 60 * 6)
        .toISOString()
        .replace(/T/, " ")
        .replace(/\..+/, "");
      var imageName = data.imageName;
      var json = {
        userID: userID,
        severity: severity,
        credibility: credibility,
        description: description,
        date: date,
        imageName: imageName,
      };
      console.log(json);
      var db = client.db("app");
      //Reports or test collections
      db.collection("feedback").insertOne(json, function (err, result) {
        if (err) throw err;
        console.log("1 document inserted");
        res.setHeader("Content-Type", "application/json");
        res.send(result["ops"]);
        res.end();
      });
      client.close();
    }
  });
});

module.exports = router;
