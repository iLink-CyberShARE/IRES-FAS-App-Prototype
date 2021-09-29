 /**
  * <h1> App </h1>
  *
  * Connection to the mongodb database
  *
  *
  * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
  * Smart Cities investigators and contributing participants.
  */
/**
 * Run mongodb server
 * cd C:\Program Files\MongoDB\Server\4.0\bin
 * cd C:\Program Files\MongoDB\Server\4.2\bin
 * mongod
 *
 * Notes:
 * run "npm install"
 * display_map.js (2 times) and staff.js make requests from client to server
 * users.js and reports.js use the mongodb url
 */

//Global variables
//MongoURL for the storage of information into the database
global.ip_address = "http://localhost:8500";
global.mongoURL = "mongodb://flood_usr2:changeThisPassword@localhost:27017/?authSource=app";
global.db_name = "app";
global.raspi_collection = "raspiData";
global.destinationAppPics = 'opt/nodeapps/flooding-app/Flooding-server/public/uploads/';
global.destinationRasPiPics = 'opt/nodeapps/flooding-app/Flooding-server/public/uploadsRaspi/';

var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var cors= require('cors');

var usersRouter = require('./routes/users');
var reportsRouter = require('./routes/reports');
var imagesRouter = require('./routes/images');
var mapsRouter = require('./routes/maps');
var managerRouter = require('./routes/manager');
var raspiRouter = require('./routes/raspi');
var feedbackRouter = require('./routes/feedback')

var app = express();
//View engine setup

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
console.log(path.join(__dirname, 'public'));
app.use(express.static(path.join(__dirname, 'public')));
console.log(path.join(__dirname , '/node_modules'));
app.use(express.static(path.join(__dirname , '/node_modules')));

app.use('/users', usersRouter);
app.use('/reports', reportsRouter);
app.use('/images', imagesRouter);
app.use('/maps',mapsRouter);
app.use('/manager',managerRouter);
app.use('/raspi', raspiRouter);  //Raspberry Pi endpoints
app.use('/feedback', feedbackRouter);

//Catch 404 and forward to error handler
app.use(function(req, res, next) {
    next(createError(404));
});

//Error handler
app.use(function(err, req, res, next) {
  //Set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  //Render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;