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

        // parameters
        console.log(event['pathParameters']['dbname']);
        console.log(event['pathParameters']['query']);

        var dbname = event['pathParameters']['dbname'];
        var query = event['pathParameters']['query'];

        var d = new Date();
        var start = d.getTime();

        var cloudantUrl = "https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix:3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991@7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud";

        const nano = require('nano')(cloudantUrl);

        // select database to use
        const couchdb = nano.db.use(dbname);

        const q = {
            selector: {
                "content": {
                    "$regex": query
                }
            }
        };

        return couchdb.find(q).then((body) => {

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
    } catch (err) {
        console.log(err);
        return err;
    }
};
