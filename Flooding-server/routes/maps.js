var express = require('express');
var router = express.Router();

//Display leaflet map

router.get('/display',(req,res)=>{
    res.render('map', { lat: parseFloat(req.query.lat), lon:parseFloat(req.query.lon), ip_address:global.ip_address});
});

module.exports = router;