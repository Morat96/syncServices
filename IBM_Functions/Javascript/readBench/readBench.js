/**
 * A benchmark for CouchDB databases.
 * This function execute a query that search a specific word in the database documents.
 * @author Matteo Moratello
 */

//var rword = require('rword');
var openwhisk = require('openwhisk');

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

    var cloudantUrl = "https://" + args.username + ":" + args.password + "@" + args.host;

    const nano = require('nano')(cloudantUrl);

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
}

exports.main = main;