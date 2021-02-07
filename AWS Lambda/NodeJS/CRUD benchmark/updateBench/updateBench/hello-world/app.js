// const axios = require('axios')
// const url = 'http://checkip.amazonaws.com/';
let response;

var rword = require('rword');

/**
 *
 * Event doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format
 * @param {Object} event - API Gateway Lambda Proxy Input Format
 *
 * Context doc: https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-context.html 
 * @param {Object} context
 *
 * Return doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html
 * @returns {Object} object - API Gateway Lambda Proxy Output Format
 * 
 */
exports.lambdaHandler = async (event, context) => {
    try {

        // parameters
        console.log(event['pathParameters']['dbname']);
        console.log(event['pathParameters']['count']);
        console.log(event['queryStringParameters']['size']);
        console.log(event['queryStringParameters']['sorts']);

        if (!event['queryStringParameters']['size']) {
            return {
                'statusCode': 400,
                'body': JSON.stringify({
                    message: "The parameter 'size' must be defined!"
                })
            };
        }

        if (!event['queryStringParameters']['sorts']) {
            return {
                'statusCode': 400,
                'body': JSON.stringify({
                    message: "The parameter 'sorts' must be defined!"
                })
            };
        }

        // parameters
        var size = event['queryStringParameters']['size'];
        var sorts = event['queryStringParameters']['sorts'];
        var dbname = event['pathParameters']['dbname'];
        var count = event['pathParameters']['count'];

        var d = new Date();
        var start = d.getTime();

        //var all_docs = '{ "docs" : ['; 
        var all_docs = { "docs" : []};

        // number of documents 
        for (var doc = 0; doc < count; doc++) {
            
            // create a document with random words of size "size"
            var words = rword.rword.generate(size);

            if (sorts > 1) {
                // some sorting of documents
                for (var i = 0; i < sorts - 1; i++) {
                    // do sort
                    words.sort();
                    // do shuffle
                    shuffle(words);
                }
            }

            words.sort();

            var content = words.join(' ');

            all_docs.docs[doc] = {};
            all_docs.docs[doc].content = content;
        }

        console.log(all_docs);

        var cloudantUrl = "https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix:3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991@7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud";

        const nano = require('nano')(cloudantUrl);

        // select database to use
        const couchdb = nano.db.use(dbname);

        // get list of documents in the current database
        return couchdb.list().then((body) => {

            // number of documents
            var docsCount = body.total_rows;

            if (count > docsCount) {
                return {
                    statusCode: 400,
                    headers: { 'Content-Type': 'application/json' },
                    body: {
                        "message": "The number of docs to delete is " + count + ", but the docs in the database '" + dbname + "' are " + docsCount
                    }
                };
            }

            // select a random list of indices
            var list = getRandomList(count, docsCount);

            // create the object
            for (var i = 0; i < count; i++) {
                all_docs.docs[i]._id = body.rows[list[i]].id;
                all_docs.docs[i]._rev = body.rows[list[i]].value.rev;
            }

            // delete documents from the database
            return couchdb.bulk(all_docs).then((body) => {

                d = new Date();
                var end = d.getTime();
                var time = end - start;
                console.log("Time for sorting: " + time + " ms");

                return { 
                    statusCode: 200,
                    headers: { 'Content-Type': 'application/json' },
                    body: {
                        "response": body,
                        "time": time + ' ms'
                    }
                }
            }).catch((error) => {
                console.log('Error: ' + error.message);
                return { 
                    statusCode: 400,
                    headers: { 'Content-Type': 'application/json' },
                    body: {
                        "error": error
                    }
                }
            });
        }).catch((error) => {
            console.log('Error: ' + error.message);
            return { 
                statusCode: 400,
                headers: { 'Content-Type': 'application/json' },
                body: {
                    "error": error
                }
            }
        });
    } catch (err) {
        console.log(err);
        return err;
    }
};

/**
* Shuffle an array
* @param array array of string
*/
function shuffle(array) {
    array.sort(() => Math.random() - 0.5);
}

/**
* Get an array composed of random values
* @param size size of the array to generate
* @param max generate numbers between 0 and (max - 1)
* @returns array of random numbers
*/
function getRandomList(size, max) {
    var arr = [];
    while(arr.length < size){
        var r = Math.floor(Math.random() * max);
        if(arr.indexOf(r) === -1) arr.push(r);
    }
    return arr;
}