/**
 * A benchmark for CouchDB databases.
 * This function deletes a number of documents from a CouchDB database at random.
 * @author Matteo Moratello
 */

//var rword = require('rword');
var openwhisk = require('openwhisk');

function main(args) {

    var db_selected;
    var size;

    // obtain the number of documents to delete
    if(args.__ow_path && args.__ow_path.length) {
        var parts = args.__ow_path.split('/');
        if (parts.length != 5) {
            return {
                statusCode: 400,
                headers: { 'Content-Type': 'application/json' },
                body: {
                    "message": "The path must be: ../deleteBench/{dbname}/{count}"
                }
            };
        }
        db_selected = parts[3];
        size = parts[4];
    }
    else {
        return {
            statusCode: 400,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The path must be: ../deleteBench/{dbname}/{count}"
            }
        };
    }

    var d = new Date();
    var start = d.getTime();

    var cloudantUrl = "https://" + params.username + ":" + params.password + "@" + params.host;

    const nano = require('nano')(cloudantUrl);

    // select database to use
    const couchdb = nano.db.use(db_selected);

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
        });
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