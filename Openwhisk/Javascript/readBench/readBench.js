/**
 * A benchmark for CouchDB databases.
 * This function execute a query that search a specific word in the database documents.
 * @author Matteo Moratello
 */

//var rword = require('rword');
var openwhisk = require('openwhisk');
const nano = require('nano')('http://admin:sX5IWFOsWX3BKClsxB8G@10.152.183.24:5984');

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

exports.main = main;