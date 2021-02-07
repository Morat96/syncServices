/**
 * A benchmark for CouchDB databases.
 * This function updates a number of random documents and executes a series of sorts to them.
 * @author Matteo Moratello 
 */

var rword = require('rword');

module.exports = async function (context, req) {

    context.log('JavaScript benchmark for CouchDB databases');

    var dbname = context.bindingData.dbname;
    var count = context.bindingData.count;

    if (!req.query.size) {
        return context.res = {
            status: 400,
            body: {
                'Error': "The parameter 'size' must be defined!"
            }
        };
    }
    if (!req.query.sorts) {
        return context.res = {
            status: 400,
            body: {
                'Error': "The parameter 'sorts' must be defined!"
            }
        };
    }
    
    // parameters
    var size = req.query.size;
    var sorts = req.query.sorts;

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
                status: 400,
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

            return context.res = {
                status: 200,
                body: {
                    "response": body,
                    "time": time + ' ms'
                }
            }
        }).catch((error) => {
            console.log('Error: ' + error.message);
            return context.res = {
                status: 400,
                body: {
                    "error": error
                }
            }
        });
    }).catch((error) => {
        console.log('Error: ' + error.message);
        return context.res = {
            status: 400,
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