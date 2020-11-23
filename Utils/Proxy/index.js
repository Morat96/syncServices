const express = require('express');
const router = express();

const AWS = require('aws-sdk');
AWS.config.loadFromPath('./config.json');

const lambda = new AWS.Lambda();

const bodyParser = require('body-parser');
router.use(bodyParser.text());

/** 
 * Get method for RSA Decryption Algorithm.
*/
router.get('/rsa', function(req, res) {

    // function name
    console.log(req.query.function_name);
    var function_name = req.query.function_name;

    // Parameters
    console.log(req.query.n);
    console.log(req.query.e);
    console.log(req.query.cipher);
    var n = req.query.n;
    var e = req.query.e;
    var cipher = req.query.cipher;

    var params = {
        FunctionName: function_name,
        //InvocationType: 'Event',
        Payload: JSON.stringify(
            {
                "httpMethod": "GET",
                "queryStringParameters": {
                    "n" : n,
                    "e" : e,
                    "cipher" : cipher
                }
              
            })
       };

    // lets invoke the Lambda function
    lambda.invoke(params, function(err, data) {
        var r;
        if (err) { 
            console.log(err, err.stack); 
        }
        else {     
            r = JSON.parse(data.Payload)
            console.log(r)
        }
    res.status(data.StatusCode).json(JSON.parse(r.body));
    });
});

/** 
 * Vehicle Routing Problem solver.
*/
router.post('/vrp', function(req, res, next) {

    // function name
    console.log(req.query.function_name);
    var function_name = req.query.function_name;

    var k_opt = 1;
    var n_iter = 1;

    // Parameters
    console.log(req.query.vns);
    console.log(req.query.k_opt);
    console.log(req.query.n_iter);
    var vns = req.query.vns;
    if(vns) {
        k_opt = req.query.k_opt;
        n_iter = req.query.n_iter;
    }

    // Instance
    //console.log(req.body);
    
    var params = {
        FunctionName: function_name,
        //InvocationType: 'Event',
        Payload: JSON.stringify(
            {
                "httpMethod": "POST",
                "body": req.body,
                "queryStringParameters": {
                    "vns" : vns,
                    "k_opt" : k_opt,
                    "n_iter" : n_iter
                }
              
            })
       };
    // lets invoke the Lambda function
    lambda.invoke(params, function(err, data) {
        var r;
        if (err) { 
            console.log(err, err.stack); 
        }
        else {     
            r = JSON.parse(data.Payload)
            console.log(r)
        }
    res.status(data.StatusCode).json(JSON.parse(r.body));
    });
});

router.listen(3000)