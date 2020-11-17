/**
 * A benchmark for CouchDB databases.
 * This function updates a number of random documents and executes a series of sorts to them.
 * @author Matteo Moratello 
 */

var rword = require('rword');

function main(params) {

    var db_selected;
    var count;

    // obtain the number of documents to update
    if(params.__ow_path && params.__ow_path.length) {
        var parts = params.__ow_path.split('/');
        if (parts.length != 5) {
            return {
                statusCode: 500,
                headers: { 'Content-Type': 'application/json' },
                body: {
                    "message": "The path must be: ../updateBench/{dbname}/{count}"
                }
            };
        }
        db_selected = parts[3];
        count = parts[4];
    }
    else {
        return {
            statusCode: 500,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The path must be: ../updateBench/{dbname}/{count}"
            }
        };
    }

    if (!params.size) {
        return {
            statusCode: 500,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The parameter 'size' must be defined!"
            }
        };
    }
    if (!params.sorts) {
        return {
            statusCode: 500,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The parameter 'sorts' must be defined!"
            }
        };
    }

    // parameters
    var size = params.size;
    var sorts = params.sorts;

    var d = new Date();
    var start = d.getTime();

    //var all_docs = '{ "docs" : ['; 
    var all_docs = { "docs" : []};

    // number of documents 
    for (var doc = 0; doc < count; doc++) {
        
        // create a document with random words of size "size"
        words = rword.rword.generate(size);

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

    var cloudantUrl = "https://" + params.username + ":" + params.password + "@" + params.host;

    const nano = require('nano')(cloudantUrl);

    // select database to use
    const couchdb = nano.db.use(db_selected);

    // get list of documents in the current database
    return couchdb.list().then((body) => {

        // number of documents
        var docsCount = body.total_rows;

        if (count > docsCount) {
            return {
                statusCode: 500,
                headers: { 'Content-Type': 'application/json' },
                body: {
                    "message": "The number of docs to delete is " + count + ", but the docs in the database '" + db_selected + "' are " + docsCount
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
                statusCode: 500,
                headers: { 'Content-Type': 'application/json' },
                body: {
                    "error": error
                }
            }
        });
    }).catch((error) => {
        console.log('Error: ' + error.message);
        return { 
            statusCode: 500,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "error": error
            }
        }
    });
}

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

exports.main = main;