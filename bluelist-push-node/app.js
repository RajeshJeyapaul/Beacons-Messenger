/*
 * Copyright 2014 IBM Corp. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var express = require('express'),
    bodyparser = require('body-parser'),
    ibmbluemix = require('ibmbluemix'),
    ibmpush = require('ibmpush');
var url = require('url');
//configuration for application
var appConfig = {
    applicationId: "3a098049-9fc2-473b-bfba-5816a6ad8f8c",
    applicationRoute: "http://ibmbeaconmessaging.mybluemix.net"
};
var mongo = process.env.VCAP_SERVICES;
var conn_str = "";
if (mongo) {
  var env = JSON.parse(mongo);
  if (env['mongolab']) {
    mongo = env['mongolab'][0]['credentials'];
    if (mongo.uri) {
      conn_str = mongo.uri;
    } else {
      console.log("No mongo found");
    }  
  } else {
    conn_str = 'mongodb://localhost:27017';
  }
} else {
  conn_str = 'mongodb://localhost:27017/test';
}

var MongoClient = require('mongodb').MongoClient;
var db;
MongoClient.connect(conn_str, function(err, database) {
  if(err) throw err;
  db = database;
}); 
// create an express app
var app = express();
app.use(bodyparser.json());
app.use(bodyparser.urlencoded({
  extended: true
}));

//uncomment below code to protect endpoints created afterwards by MAS
//var mas = require('ibmsecurity')();
//app.use(mas);


//initialize mbaas-config module
ibmbluemix.initialize(appConfig);
var logger = ibmbluemix.getLogger();

app.use(function(req, res, next) {
	req.ibmpush = ibmpush.initializeService(req);
	req.logger = logger;
	next();
});

//initialize ibmconfig module
var ibmconfig = ibmbluemix.getConfig();

//get context root to deploy your application
//the context root is '${appHostName}/v1/apps/${applicationId}'
var contextRoot = ibmconfig.getContextRoot();
appContext=express.Router();
app.use(contextRoot, appContext);

console.log("contextRoot: " + contextRoot);

// log all requests
app.all('*', function(req, res, next) {
	console.log("Received request to " + req.url);
	next();
});

// create resource URIs
// endpoint: https://mobile.ng.bluemix.net/${appHostName}/v1/apps/${applicationId}/notifyOtherDevices/
appContext.post('/notifyOtherDevices', function(req,res) {
	var results = 'Sent notification to all registered devices successfully.';

	console.log("Trying to send push notification via JavaScript Push SDK");
	var message = { "alert" : "The BlueList has been updated.",
					"url": "http://www.google.com"
	};

	req.ibmpush.sendBroadcastNotification(message,null).then(function (response) {
		console.log("Notification sent successfully to all devices.", response);
		res.send("Sent notification to all registered devices.");
	}, function(err) {
		console.log("Failed to send notification to all devices.");
		console.log(err);
		res.send(400, {reason: "An error occurred while sending the Push notification.", error: err});
	});
});
appContext.get('/register',function(req,res){
	console.log('Request came in');
	var url_parts = url.parse(req.url, true);
	var Name = url_parts.query.name;
	var emailId = url_parts.query.email;
	var pwd = url_parts.query.pwd;
    
    var collection = db.collection('users');

    collection.insert({
        "Name" : Name,
        "EmailId" : emailId,
        "Password" : pwd
    }, function (err, doc) {
        if (err) {
            res.send("There was a problem adding the information to the database.");
        }
        else {
            //res.location("userlist");
            //res.redirect("userlist");
            res.header('Access-Control-Allow-Origin', '*');
			res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
			res.header('Access-Control-Allow-Headers', 'Content-Type');
			res.send('Thanks for the request');
            //res.render('index');
        }
    });
	
});
appContext.get('/login',function(req,res){
	var url_parts = url.parse(req.url, true);
	var emailId = url_parts.query.email;
	var pwd = url_parts.query.pwd;
	db.collection('users').find({EmailId:emailId,Password:pwd}).toArray(function(err,items){
		res.json({'user':items});
	});
});
appContext.get('/getNotification',function(req,res){
	console.log('Request hit');
	var url_parts = url.parse(req.url, true);
	var id = url_parts.query.id;
	console.log(id);
	
	db.collection('beacons').find({beaconId:id}).toArray(function(err,items){
		console.log(items);
		db.collection('meetups').find().toArray(function(err1,items1){
			res.json({'message':items[0]['message'],'meetups':items1});
		});
		//res.json({'message':items[0]['message'],'meetups':[{'title':'AngularJS','venueAvailable':true,'url':'www.google.com','address':'EGL','meetTime':'26/1/15'}]});
	});
	/*if(id == 'F94DBB23-2266-7822-3782-57BEAC0952AC')
	{
		res.json({'message':'Hey welcome to EGL. Would you like to see some upcoming meetups?','meetups':[{'title':'AngularJS','venueAvailable':true,'url':'www.google.com','address':'EGL','meetTime':'26/1/15'}]});
	}
	else
	{
		res.json({'message':'Invalid ID'});
	}*/
});
appContext.get('/getMeetups',function(req,res){
	res.json({'meetups':[{'title':'AngularJS','url':'www.google.com'}]});
});
// host static files in public folder
// endpoint:  https://mobile.ng.bluemix.net/${appHostName}/v1/apps/${applicationId}/static/
appContext.use('/static', express.static('public'));

//redirect to cloudcode doc page when accessing the root context
app.get('/', function(req, res){
	res.sendfile('public/index.html');
});
app.get('/users',function(req,res){
		db.collection('users').find().toArray(function (err, items) {
		res.header('Access-Control-Allow-Origin', '*');
		res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
		res.header('Access-Control-Allow-Headers', 'Content-Type');
		res.json({'users':items});
    });
});


	
app.listen(ibmconfig.getPort());
console.log('Server started at port: '+ibmconfig.getPort());
