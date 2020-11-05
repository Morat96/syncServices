/**
 * A benchmark for CouchDB databases.
 * This function creates a number of random documents and executes a series of sorts to them.
 * @author Matteo Moratello 
 */

var rword = require('rword');
var openwhisk = require('openwhisk');
const nano = require('nano')('http://admin:sX5IWFOsWX3BKClsxB8G@10.152.183.24:5984');

function main(params) {

    var db_selected;

    // obtain the database name from the path
    if(params.__ow_path && params.__ow_path.length) {
        var parts = params.__ow_path.split('/');
        if (parts.length != 4) {
            return {
                statusCode: 400,
                headers: { 'Content-Type': 'application/json' },
                body: {
                    "message": "The path must be: ../createBench/{dbname}"
                }
            };
        }
        db_selected = parts[3];
    }
    else {
        return {
            statusCode: 400,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The path must be: ../createBench/{dbname}"
            }
        };
    }

    if (!params.size) {
        return {
            statusCode: 400,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The parameter 'size' must be defined!"
            }
        };
    }
    if (!params.ndocs) {
        return {
            statusCode: 400,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The parameter 'ndocs' must be defined!"
            }
        };
    }
    if (!params.sorts) {
        return {
            statusCode: 400,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The parameter 'sorts' must be defined!"
            }
        };
    }

    // parameters
    var size = params.size;
    var ndocs = params.ndocs;
    var sorts = params.sorts;

    var d = new Date();
    var start = d.getTime();

    //var all_docs = '{ "docs" : ['; 
    var all_docs = { "docs" : []};

    // number of documents 
    for (var doc = 0; doc < ndocs; doc++) {
        
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

    //save(all_docs);

    // select database to use
    const couchdb = nano.db.use(db_selected);

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
    });
}

/**
* Save documents to the database
* @param doc object with new documents
*/
function save(doc)  {
    var options = {
        apihost: '192.168.30.151:31001', 
        api_key: '23bc46b1-71f6-4ed5-8c54-816aa4f8c502:123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP',
        ignore_certs: true
    }
    var ow = openwhisk(options)
    const blocking = true, result = true
    return ow.actions.invoke({
        "name": "/guest/couchdb/manage-bulk-documents",
        //blocking, result,
        "params": { 
        "dbname": "benchdb",
        "docs": doc
        }
    }).then(result => {
        console.log(result);
      }).catch(err => {
        console.error('failed to invoke actions', err);
      })
}

/**
* Shuffle an array
* @param array array of string
*/
function shuffle(array) {
    array.sort(() => Math.random() - 0.5);
}

exports.main = main;