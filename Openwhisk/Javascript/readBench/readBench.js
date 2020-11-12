/**
 * A benchmark for CouchDB databases.
 * This function execute a query that search a specific word in the database documents.
 * @author Matteo Moratello
 */

//var rword = require('rword');
var openwhisk = require('openwhisk');
const nano = require('nano')('https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix:3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991@7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud');

function main(args) {

    var db_selected;
    var query;

    // obtain the number of documents to delete
    if(args.__ow_path && args.__ow_path.length) {
        var parts = args.__ow_path.split('/');
        if (parts.length != 6) {
            return {
                statusCode: 400,
                headers: { 'Content-Type': 'application/json' },
                body: {
                    "message": "The path must be: ../readBench/{dbname}/{query}"
                }
            };
        }
        db_selected = parts[4];
        query = parts[5];
    }
    else {
        return {
            statusCode: 400,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The path must be: ../readBench/{dbname}/{query}"
            }
        };
    }

    var d = new Date();
    var start = d.getTime();

    // select database to use
    const couchdb = nano.db.use(db_selected);

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
    });
}

exports.main = main;