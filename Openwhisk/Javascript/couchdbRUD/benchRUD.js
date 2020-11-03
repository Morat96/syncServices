/**
 * A benchmark for CouchDB databases.
 * @author Matteo Moratello 
 */

var rword = require('rword');
var openwhisk = require('openwhisk');

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

function shuffle(array) {
    array.sort(() => Math.random() - 0.5);
  }

function main(params) {

    // parameters
    var size = params.size;
    var ndocs = params.ndocs;
    var sorts = params.sorts;

    var d = new Date();
    var start = d.getTime();

    var all_docs = '{ "docs" : ['; 

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

        if (doc == ndocs - 1) all_docs += '{ "content": ' + '"' + content + '"' + '} ';
        else all_docs += '{ "content": ' + '"' + content + '"' + '}, ';
    }

    all_docs += ']}';

    console.log(all_docs);

    save(all_docs);

    d = new Date();
    var end = d.getTime();
    var time = end - start;
    console.log("Time for sorting: " + time + " ms");

    return {
        'ok': 'true',
        'time': time + ' ms'
    };
}

exports.main = main;