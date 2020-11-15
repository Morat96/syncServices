// const axios = require('axios')
// const url = 'http://checkip.amazonaws.com/';
let response;

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

        console.log(event['pathParameters']['dbname']);
        console.log(event['pathParameters']['count']);

        var dbname = event['pathParameters']['dbname'];
        var size = event['pathParameters']['count'];

        var d = new Date();
        var start = d.getTime();

        var cloudantUrl = "https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix:3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991@7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud";

        const nano = require('nano')(cloudantUrl);

        // select database to use
        const couchdb = nano.db.use(dbname);

        // get list of documents in the current database
        return couchdb.list().then((body) => {

            // number of documents
            var docsCount = body.total_rows;

            if (size > docsCount) {
                return {
                    statusCode: 400,
                    headers: { 'Content-Type': 'application/json' },
                    body: {
                        "message": "The number of docs to delete is " + size + ", but the docs in the database '" + db_selected + "' are " + docsCount
                    }
                };
            }

            // select a random list of indices
            var list = getRandomList(size, docsCount);

            var docToDelete;
            docToDelete = { "docs" : []};

            // create the object
            for (var i = 0; i < size; i++) {
                docToDelete.docs[i] = {};
                docToDelete.docs[i]._id = body.rows[list[i]].id;
                docToDelete.docs[i]._rev = body.rows[list[i]].value.rev;
                docToDelete.docs[i]._deleted = true;
            }

            // delete documents from the database
            return couchdb.bulk(docToDelete).then((body) => {

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