var express = require('express');
var router = express.Router();


var multer = require('multer');
var fileType = require('file-type');
var fs = require('fs');

var storage = multer.diskStorage(
    {
        destination:global.destinationAppPics,
        filename: function ( req, file, cb ) {
            cb( null, file.originalname);
        }
    }
);

const upload = multer({
    destination:global.destinationAppPics,
    limits: {fileSize: 10 * 1024 * 1024, files: 1},
    storage:storage,
    fileFilter:  (req, file, callback) => {

        if (!file.originalname.match(/\.(jpg|jpeg|png)$/)) {

            return callback(new Error('Only Images are allowed !'), false);
        }

        callback(null, true);
    }
}).single('file');

router.post('/upload', (req, res) => {

    upload(req, res, function (err) {
        if (err) {
            console.log(err.message);   //-------------------------------
            res.status(400).json({message: err.message});
        } else {
            let path = global.destinationAppPics+req.file.filename;
            res.status(200).json({message: 'Image Uploaded Successfully !', path: path});
        }
    })
});

router.get('/download/:imagename', (req, res) => {

    let imagename = req.params.imagename;
    let imagepath = global.destinationAppPics+ imagename;
    console.log(imagepath);
    var type = 'image/jpeg';
    var s = fs.createReadStream(imagepath);
    s.on('open', function () {
        res.set('Content-Type', type);
        s.pipe(res);
    });
    s.on('error', function () {
        res.set('Content-Type', 'text/plain');
        res.status(404).end('Not found');
    });
});

var storageRaspi = multer.diskStorage(
    {
        destination:global.destinationRasPiPics,
        filename: function ( req, file, cb ) {
            cb( null, file.originalname);
        }
    }
);

const uploadRaspi = multer({
    destination:global.destinationRasPiPics,
    limits: {fileSize: 10 * 1024 * 1024, files: 1},
    storage:storageRaspi,
    fileFilter:  (req, file, callback) => {

        if (!file.originalname.match(/\.(jpg|jpeg|png)$/)) {

            return callback(new Error('Only Images are allowed !'), false);
        }

        callback(null, true);
    }
}).single('file');

router.post('/uploadRaspi', (req, res) => {

    uploadRaspi(req, res, function (err) {
        if (err) {
            console.log(err.message);   //-------------------------------
            res.status(400).json({message: err.message});
        } else {
            let path = global.destinationRasPiPics+req.file.filename;
            res.status(200).json({message: 'Image Uploaded Successfully !', path: path});
        }
    })
});
module.exports = router;
