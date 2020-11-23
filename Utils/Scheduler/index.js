/* 
 *  Scheduler for Serverless cold start benchmark.
 */
"use strict";
var http = require('https');

function getRandomBoolean() {
    return Math.random() > 0.5;
}

function getRandomInt(max) {
  return Math.floor(Math.random() * Math.floor(max));
}

if (process.argv.length != 4 ) { 
    console.log("The invocation must be: node index.js [#calls] [#max_time]");
    return;
}

//The url we want is: 'www.random.org/integers/?num=1&min=1&max=10&col=1&base=10&format=plain&rnd=new'
var options = {
    host: '192.168.30.151',
    port: '31001',
    path: '/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/rsa/js?n=9010760112349&e=65537&cipher=1234',
    rejectUnauthorized: false
  };

var counter = 1;
var iterations = process.argv[2];
var max_time = process.argv[3];
var url = "";

console.log("Number of calls: " + iterations);

var interval = getRandomInt(max_time);
console.log(counter + "° interval: " + interval);

var myFunction = function() {

    console.log("HTTP call");
    var start = new Date();
    var req = http.request(options, (response) => {
        var end = new Date();
        console.log('Request time in ms', end - start);
        console.log('STATUS: ' + response.statusCode);
        //console.log('HEADERS: ' + JSON.stringify(response.headers));
        response.setEncoding('utf8');
        response.on('data', function (chunk) {
          console.log('BODY: ' + chunk);
        });
    }).end();

    req.on('error', function(e) {
        console.log('problem with request: ' + e.message);
    });

    if (counter == iterations) {
        return;
    }
    
    interval = getRandomInt(max_time);
    console.log(counter + "° interval: " + interval);
    counter ++;

    if (counter <= iterations) {
        setTimeout(myFunction, interval);
    }
}
setTimeout(myFunction, interval);